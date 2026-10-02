package ru.itis.billing.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.itis.billing.dto.BillingResponse;
import ru.itis.billing.dto.InferenceEvent;
import ru.itis.billing.entity.BillingRecord;
import ru.itis.billing.repository.BillingRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BillingService {

    private final BillingRepository repository;

    @Transactional
    public void recordUsage(InferenceEvent event) {
        BillingRecord record = repository
                .findByTenantAndModel(event.tenant(), event.model())
                .orElseGet(() -> new BillingRecord(event.tenant(), event.model(), event.category()));

        record.addUsage(event.inputTokens(), event.outputTokens(), event.durationMs());
        repository.save(record);

        log.info("Recorded: tenant={}, model={}, requests={}, tokens={}/{}, durationMs={}",
                record.getTenant(), record.getModel(), record.getRequestCount(),
                record.getTotalInputTokens(), record.getTotalOutputTokens(),
                record.getTotalDurationMs());
    }

    public List<BillingResponse> getByTenant(String tenant) {
        return repository.findByTenant(tenant).stream()
                .map(this::toResponse)
                .toList();
    }

    public List<BillingResponse> getAll() {
        return repository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    private BillingResponse toResponse(BillingRecord r) {
        return new BillingResponse(
                r.getTenant(),
                r.getModel(),
                r.getCategory(),
                r.getRequestCount(),
                r.getTotalInputTokens(),
                r.getTotalOutputTokens(),
                r.getTotalDurationMs(),
                calculateCost(r)
        );
    }

    private double calculateCost(BillingRecord r) {
        return switch (r.getCategory()) {
            case "text_classification" -> 0.001 * r.getRequestCount();
            case "llm"                 -> 0.00001 * (r.getTotalInputTokens() + r.getTotalOutputTokens());
            case "speech"              -> 0.0001 * (r.getTotalDurationMs() / 1000.0);
            case "vision"              -> 0.002 * r.getRequestCount();
            case "token_classification", "embeddings" -> 0.0005 * r.getRequestCount();
            default                    -> 0.001 * r.getRequestCount();
        };
    }
}
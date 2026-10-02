package ru.itis.billing.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.itis.billing.dto.InferenceEvent;
import ru.itis.billing.service.BillingService;

@Slf4j
@Component
@RequiredArgsConstructor
public class InferenceEventConsumer {

    private final BillingService billingService;

    @KafkaListener(topics = "inference.completed", groupId = "billing-group")
    public void onInferenceCompleted(InferenceEvent event) {
        log.info("Received event: tenant={}, model={}, category={}, tokens={}/{}, durationMs={}",
                event.tenant(), event.model(), event.category(),
                event.inputTokens(), event.outputTokens(), event.durationMs());
        billingService.recordUsage(event);
    }
}
package ru.itis.drift.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.itis.drift.dto.DriftReport;
import ru.itis.drift.dto.InferenceEvent;
import ru.itis.drift.kafka.DriftEventProducer;
import ru.itis.drift.repository.EventRepository;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class DriftService {

    private final EventRepository repository;
    private final DriftEventProducer producer;

    @Value("${drift.threshold:0.3}")
    private double threshold;

    @Value("${drift.baseline.window-minutes:60}")
    private int baselineWindow;

    @Value("${drift.current.window-minutes:10}")
    private int currentWindow;

    public void recordEvent(InferenceEvent event) {
        try {
            repository.save(event);
            log.debug("Recorded event: tenant={}, model={}, result={}",
                    event.tenant(), event.model(), event.result());
        } catch (Exception e) {
            log.error("Failed to save event", e);
        }
    }

    public List<DriftReport> analyzeAll() {
        List<DriftReport> reports = new ArrayList<>();
        List<Map<String, Object>> pairs = repository.getActiveTenantModelPairs(currentWindow);

        for (Map<String, Object> pair : pairs) {
            String tenant = (String) pair.get("tenant");
            String model = (String) pair.get("model");
            try {
                DriftReport report = analyzeOne(tenant, model);
                if (report.drifted()) {
                    producer.sendDriftDetected(report);
                }
                reports.add(report);
            } catch (Exception e) {
                log.error("Drift analysis failed for {}/{}", tenant, model, e);
            }
        }
        return reports;
    }

    /** Проверка дрифта для одной пары. */
    public DriftReport analyzeOne(String tenant, String model) {
        Map<String, Double> baseline = repository.getDistribution(tenant, model, baselineWindow);
        Map<String, Double> current = repository.getDistribution(tenant, model, currentWindow);

        double score = computeDriftScore(baseline, current);
        boolean drifted = score > threshold;

        DriftReport report = new DriftReport(
                tenant, model, baseline, current, score, drifted, Instant.now()
        );

        if (drifted) {
            log.warn("Drift alert: tenant={}, model={}, score={} (threshold={})",
                    tenant, model, score, threshold);
        }
        return report;
    }

    private double computeDriftScore(Map<String, Double> baseline, Map<String, Double> current) {
        if (baseline.isEmpty() || current.isEmpty()) return 0.0;

        double sum = 0.0;
        java.util.Set<String> keys = new java.util.HashSet<>();
        keys.addAll(baseline.keySet());
        keys.addAll(current.keySet());

        for (String key : keys) {
            double b = baseline.getOrDefault(key, 0.0);
            double c = current.getOrDefault(key, 0.0);
            sum += Math.abs(b - c);
        }
        return sum / 2.0;
    }

    public List<DriftReport> getReportsForTenant(String tenant, String model) {
        List<DriftReport> list = new ArrayList<>();
        list.add(analyzeOne(tenant, model));
        return list;
    }
}
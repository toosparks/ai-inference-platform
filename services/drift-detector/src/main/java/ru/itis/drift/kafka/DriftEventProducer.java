package ru.itis.drift.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.itis.drift.dto.DriftReport;

@Slf4j
@Component
@RequiredArgsConstructor
public class DriftEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendDriftDetected(DriftReport report) {
        kafkaTemplate.send("model.drift.detected", report);
        log.warn("DRIFT DETECTED: tenant={}, model={}, score={}",
                report.tenant(), report.model(), report.driftScore());
    }
}
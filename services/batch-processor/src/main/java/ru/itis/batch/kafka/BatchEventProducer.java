package ru.itis.batch.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.itis.batch.dto.BatchRequested;

import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class BatchEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendBatchRequested(UUID jobId, String model, String tenant, List<String> texts) {
        BatchRequested event = new BatchRequested(jobId.toString(), model, tenant, texts);
        kafkaTemplate.send("batch.requested", event);
        log.info("Sent batch.requested: jobId={}, count={}", jobId, texts.size());
    }
}
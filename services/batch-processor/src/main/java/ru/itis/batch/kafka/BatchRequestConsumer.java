package ru.itis.batch.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import ru.itis.batch.dto.BatchRequested;
import ru.itis.batch.service.BatchService;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class BatchRequestConsumer {

    private final BatchService batchService;

    @Value("${gateway.url}")
    private String gatewayUrl;

    @KafkaListener(topics = "batch.requested", groupId = "batch-processor-group")
    public void onBatchRequested(BatchRequested event) {
        UUID jobId = UUID.fromString(event.jobId());
        log.info("Received batch.requested: jobId={}, count={}", jobId, event.texts().size());

        batchService.updateJobStatus(jobId, "PROCESSING", 0);

        RestClient client = RestClient.builder().baseUrl(gatewayUrl).build();
        List<String> texts = event.texts();
        int processed = 0;

        for (int i = 0; i < texts.size(); i++) {
            String text = texts.get(i);
            try {
                Map<?, ?> response = client.post()
                        .uri("/api/v1/predict")
                        .header("Content-Type", "application/json")
                        .body(Map.of("text", text, "model", event.model(), "tenant", event.tenant()))
                        .retrieve()
                        .body(Map.class);

                String result = response != null ? (String) response.get("result") : "unknown";
                double confidence = response != null && response.get("confidence") != null
                        ? ((Number) response.get("confidence")).doubleValue() : 0.0;

                batchService.saveResult(jobId, i, text, result, confidence);
                processed++;
            } catch (Exception e) {
                log.error("Failed to process text {} for job {}", i, jobId, e);
            }
        }

        batchService.updateJobStatus(jobId, "COMPLETED", processed);
        log.info("Batch job completed: jobId={}, processed={}/{}", jobId, processed, texts.size());
    }
}
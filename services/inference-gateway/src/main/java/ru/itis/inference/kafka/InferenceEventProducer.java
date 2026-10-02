package ru.itis.inference.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class InferenceEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendInferenceEvent(String text,
                                   String model,
                                   String tenant,
                                   String category,
                                   String result,
                                   double confidence,
                                   int inputTokens,
                                   int outputTokens,
                                   long durationMs) {
        InferenceEvent event = new InferenceEvent(
                text, model, tenant, category, result, confidence,
                inputTokens, outputTokens, durationMs, System.currentTimeMillis()
        );
        kafkaTemplate.send("inference.completed", event);
        log.info("Sent to Kafka: {}", event);
    }

    public record InferenceEvent(
            String text,
            String model,
            String tenant,
            String category,
            String result,
            double confidence,
            int inputTokens,
            int outputTokens,
            long durationMs,
            long timestamp
    ) {}
}
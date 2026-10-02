package ru.itis.drift.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.itis.drift.dto.InferenceEvent;
import ru.itis.drift.service.DriftService;

@Slf4j
@Component
@RequiredArgsConstructor
public class InferenceEventConsumer {

    private final DriftService driftService;

    @KafkaListener(topics = "inference.completed", groupId = "drift-detector-group")
    public void onInferenceCompleted(InferenceEvent event) {
        driftService.recordEvent(event);
    }
}
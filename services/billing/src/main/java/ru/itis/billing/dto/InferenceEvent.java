package ru.itis.billing.dto;

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
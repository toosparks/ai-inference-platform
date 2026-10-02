package ru.itis.billing.dto;

public record BillingResponse(
        String tenant,
        String model,
        String category,
        long requestCount,
        long totalInputTokens,
        long totalOutputTokens,
        long totalDurationMs,
        double calculatedCost
) {}
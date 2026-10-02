package ru.itis.drift.dto;

import java.time.Instant;
import java.util.Map;

public record DriftReport(
        String tenant,
        String model,
        Map<String, Double> baselineDistribution,
        Map<String, Double> currentDistribution,
        double driftScore,
        boolean drifted,
        Instant detectedAt
) {}
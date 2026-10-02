package ru.itis.batch.dto;

import java.time.Instant;
import java.util.UUID;

public record BatchJobResponse(
        UUID jobId,
        String tenant,
        String model,
        String status,
        int totalCount,
        int processedCount,
        Instant createdAt,
        Instant completedAt
) {}
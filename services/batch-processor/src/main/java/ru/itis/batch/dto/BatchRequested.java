package ru.itis.batch.dto;

import java.util.List;

public record BatchRequested(
        String jobId,
        String model,
        String tenant,
        List<String> texts
) {}
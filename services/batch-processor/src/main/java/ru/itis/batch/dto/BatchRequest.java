package ru.itis.batch.dto;

import java.util.List;

public record BatchRequest(
        String model,
        String tenant,
        List<String> texts
) {}
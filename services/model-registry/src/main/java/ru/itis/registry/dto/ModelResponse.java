package ru.itis.registry.dto;

import java.time.Instant;

public record ModelResponse(
        Long id,
        String name,
        String version,
        String category,
        String url,
        String license,
        long sizeMb,
        String description,
        Instant createdAt
) {}
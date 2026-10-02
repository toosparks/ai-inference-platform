package ru.itis.inference.registry;

public record ModelInfo(
        Long id,
        String name,
        String version,
        String category,
        String url,
        String license,
        long sizeMb,
        String description
) {}
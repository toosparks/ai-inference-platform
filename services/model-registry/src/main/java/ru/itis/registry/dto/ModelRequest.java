package ru.itis.registry.dto;

public record ModelRequest(
        String name,
        String version,
        String category,
        String url,
        String license,
        long sizeMb,
        String description
) {}
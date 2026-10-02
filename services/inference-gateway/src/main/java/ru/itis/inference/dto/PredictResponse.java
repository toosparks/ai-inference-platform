package ru.itis.inference.dto;

public record PredictResponse(
        String result,
        double confidence,
        String model,
        boolean cached
) {
}
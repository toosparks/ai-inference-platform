package ru.itis.inference.dto;

public record PredictRequest(String text, String model, String tenant) {
}
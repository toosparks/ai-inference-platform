package ru.itis.inference.strategy;

import ru.itis.inference.registry.ModelInfo;

public interface ModelStrategy {

    String category();

    InferenceResult infer(ModelInfo model, String text);

    record InferenceResult(
            String result,
            double confidence,
            int inputTokens,
            int outputTokens
    ) {}
}
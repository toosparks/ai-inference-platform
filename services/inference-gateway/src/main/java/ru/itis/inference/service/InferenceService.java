package ru.itis.inference.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import ru.itis.inference.dto.PredictRequest;
import ru.itis.inference.dto.PredictResponse;
import ru.itis.inference.kafka.InferenceEventProducer;
import ru.itis.inference.registry.ModelInfo;
import ru.itis.inference.registry.RegistryClient;
import ru.itis.inference.strategy.ModelStrategy;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class InferenceService {

    private final StringRedisTemplate redis;
    private final InferenceEventProducer producer;
    private final RegistryClient registryClient;
    private final List<ModelStrategy> strategies;

    /** category → strategy */
    private Map<String, ModelStrategy> strategyMap;

    private Map<String, ModelStrategy> getStrategyMap() {
        if (strategyMap == null) {
            strategyMap = strategies.stream()
                    .collect(Collectors.toMap(ModelStrategy::category, Function.identity()));
        }
        return strategyMap;
    }

    public PredictResponse predict(PredictRequest request) {
        String modelName = request.model();
        String cacheKey = "inference:" + modelName + ":" + request.text().hashCode();

        // 1. Кэш
        String cached = redis.opsForValue().get(cacheKey);
        if (cached != null) {
            log.info("Cache HIT for key={}", cacheKey);
            return new PredictResponse(cached, 0.99, modelName, true);
        }

        ModelInfo model = registryClient.getModel(modelName);

        ModelStrategy strategy = getStrategyMap().get(model.category());
        if (strategy == null) {
            throw new RuntimeException("No strategy for category: " + model.category());
        }

        // 4. Инференс
        long start = System.currentTimeMillis();
        ModelStrategy.InferenceResult result = strategy.infer(model, request.text());
        long durationMs = System.currentTimeMillis() - start;

        log.info("Inference done: model={}, category={}, result={}, durationMs={}, tokens={}/{}",
                modelName, model.category(), result.result(), durationMs,
                result.inputTokens(), result.outputTokens());

        redis.opsForValue().set(cacheKey, result.result(), Duration.ofMinutes(5));

        producer.sendInferenceEvent(
                request.text(), modelName, request.tenant(),
                model.category(), result.result(), result.confidence(),
                result.inputTokens(), result.outputTokens(), durationMs
        );

        return new PredictResponse(result.result(), result.confidence(), modelName, false);
    }
}
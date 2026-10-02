package ru.itis.inference.strategy;

import ai.djl.huggingface.tokenizers.HuggingFaceTokenizer;
import ai.djl.inference.Predictor;
import ai.djl.modality.Classifications;
import ai.djl.repository.zoo.Criteria;
import ai.djl.repository.zoo.ZooModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.itis.inference.registry.ModelInfo;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class ClassificationStrategy implements ModelStrategy {

    private final Map<String, ZooModel<String, Classifications>> modelCache = new ConcurrentHashMap<>();
    private final HuggingFaceTokenizer tokenizer =
            HuggingFaceTokenizer.newInstance("distilbert-base-uncased");

    @Override
    public String category() {
        return "text_classification";
    }

    @Override
    public InferenceResult infer(ModelInfo model, String text) {
        ZooModel<String, Classifications> zooModel = modelCache
                .computeIfAbsent(model.name(), n -> loadModel(model.url()));

        try (Predictor<String, Classifications> predictor = zooModel.newPredictor()) {
            Classifications cls = predictor.predict(text);
            String result = cls.topK(1).get(0).getClassName();
            double confidence = cls.topK(1).get(0).getProbability();
            int inputTokens = tokenizer.encode(text).getIds().length;
            return new InferenceResult(result, confidence, inputTokens, 1);
        } catch (Exception e) {
            throw new RuntimeException("Classification failed for " + model.name(), e);
        }
    }

    private ZooModel<String, Classifications> loadModel(String url) {
        log.info("Loading classification model from {}", url);
        Criteria<String, Classifications> criteria = Criteria.builder()
                .setTypes(String.class, Classifications.class)
                .optModelUrls(url)
                .optEngine("PyTorch")
                .build();
        try {
            return criteria.loadModel();
        } catch (Exception e) {
            throw new RuntimeException("Failed to load model from " + url, e);
        }
    }
}
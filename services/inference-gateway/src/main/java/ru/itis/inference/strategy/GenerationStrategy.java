package ru.itis.inference.strategy;

import ai.djl.huggingface.tokenizers.Encoding;
import ai.djl.huggingface.tokenizers.HuggingFaceTokenizer;
import ai.djl.inference.Predictor;
import ai.djl.modality.nlp.generate.CausalLMOutput;
import ai.djl.modality.nlp.generate.SearchConfig;
import ai.djl.modality.nlp.generate.TextGenerator;
import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.NDList;
import ai.djl.ndarray.NDManager;
import ai.djl.repository.zoo.Criteria;
import ai.djl.repository.zoo.ZooModel;
import ai.djl.translate.DeferredTranslatorFactory;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.itis.inference.registry.ModelInfo;

@Slf4j
@Component
public class GenerationStrategy implements ModelStrategy {

    private ZooModel<NDList, CausalLMOutput> model;
    private HuggingFaceTokenizer tokenizer;

    @PostConstruct
    public void init() throws Exception {
        log.info("Loading GPT-2 model...");

        Criteria<NDList, CausalLMOutput> criteria = Criteria.builder()
                .setTypes(NDList.class, CausalLMOutput.class)
                .optModelUrls("https://djl-misc.s3.amazonaws.com/test/models/gpt2/gpt2_pt.zip")
                .optEngine("PyTorch")
                .optTranslatorFactory(new DeferredTranslatorFactory())
                .build();

        this.model = criteria.loadModel();
        this.tokenizer = HuggingFaceTokenizer.newInstance("gpt2");

        log.info("GPT-2 model loaded.");
    }

    @Override
    public String category() {
        return "text_generation";
    }

    @Override
    public InferenceResult infer(ModelInfo modelInfo, String text) {
        try (Predictor<NDList, CausalLMOutput> predictor = model.newPredictor();
             NDManager manager = model.getNDManager().newSubManager()) {

            SearchConfig config = new SearchConfig();
            config.setMaxSeqLength(60);

            TextGenerator generator = new TextGenerator(predictor, "greedy", config);
            Encoding encoding = tokenizer.encode(text);
            long[] inputIds = encoding.getIds();
            NDArray inputIdArray = manager.create(inputIds).expandDims(0);
            NDArray output = generator.generate(inputIdArray);
            long[] outputIds = output.toLongArray();
            String result = tokenizer.decode(outputIds);

            int inputTokens = inputIds.length;
            int outputTokens = outputIds.length;

            return new InferenceResult(result, 1.0, inputTokens, outputTokens);

        } catch (Exception e) {
            throw new RuntimeException("Generation failed for " + modelInfo.name(), e);
        }
    }
}
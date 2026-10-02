package ru.itis.inference.registry;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class RegistryClient {

    private final RestClient restClient;
    private final Map<String, ModelInfo> cache = new ConcurrentHashMap<>();

    public RegistryClient(@Value("${registry.url}") String registryUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(registryUrl)
                .build();
    }

    public ModelInfo getModel(String name) {
        // сначала кэш
        ModelInfo cached = cache.get(name);
        if (cached != null) {
            return cached;
        }

        // потом Registry
        try {
            List<ModelInfo> versions = restClient.get()
                    .uri("/api/v1/models/{name}", name)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});

            if (versions == null || versions.isEmpty()) {
                throw new RuntimeException("Model not found in registry: " + name);
            }

            // берём первую версию (по умолчанию)
            ModelInfo info = versions.get(0);
            cache.put(name, info);
            log.info("Fetched model from registry: {} → category={}, url={}",
                    name, info.category(), info.url());
            return info;

        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch model " + name + " from registry", e);
        }
    }
}
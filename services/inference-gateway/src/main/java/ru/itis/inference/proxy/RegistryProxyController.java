package ru.itis.inference.proxy;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;

@Slf4j
@RestController
@RequestMapping("/api/v1/models")
public class RegistryProxyController {

    private final RestClient client;

    public RegistryProxyController(@Value("${registry.url}") String url) {
        this.client = RestClient.builder().baseUrl(url).build();
    }

    @GetMapping("/health")
    public String health() {
        return client.get().uri("/api/v1/models/health").retrieve().body(String.class);
    }

    @GetMapping
    public ResponseEntity<String> getAll() {
        String body = client.get().uri("/api/v1/models").retrieve().body(String.class);
        return ResponseEntity.ok().header("Content-Type", "application/json").body(body);
    }

    @GetMapping("/{name}")
    public ResponseEntity<String> getByName(@PathVariable String name) {
        String body = client.get().uri("/api/v1/models/{n}", name).retrieve().body(String.class);
        return ResponseEntity.ok().header("Content-Type", "application/json").body(body);
    }

    @PostMapping
    public ResponseEntity<String> register(@RequestBody String body) {
        String result = client.post().uri("/api/v1/models")
                .header("Content-Type", "application/json")
                .body(body)
                .retrieve().body(String.class);
        return ResponseEntity.ok().header("Content-Type", "application/json").body(result);
    }
}
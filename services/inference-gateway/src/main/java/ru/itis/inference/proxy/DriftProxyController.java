package ru.itis.inference.proxy;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;

@Slf4j
@RestController
@RequestMapping("/api/v1/drift")
public class DriftProxyController {

    private final RestClient client;

    public DriftProxyController(@Value("${drift.url}") String url) {
        this.client = RestClient.builder().baseUrl(url).build();
    }

    @GetMapping("/health")
    public String health() {
        return client.get().uri("/api/v1/drift/health").retrieve().body(String.class);
    }

    @GetMapping("/{tenant}/{model}")
    public ResponseEntity<String> getReport(@PathVariable String tenant, @PathVariable String model) {
        String body = client.get().uri("/api/v1/drift/{t}/{m}", tenant, model).retrieve().body(String.class);
        return ResponseEntity.ok().header("Content-Type", "application/json").body(body);
    }

    @PostMapping("/analyze")
    public ResponseEntity<String> analyze() {
        String body = client.post().uri("/api/v1/drift/analyze").retrieve().body(String.class);
        return ResponseEntity.ok().header("Content-Type", "application/json").body(body);
    }

    @GetMapping("/{tenant}/{model}/count")
    public ResponseEntity<String> count(@PathVariable String tenant, @PathVariable String model,
                                        @RequestParam(defaultValue = "60") int minutes) {
        String body = client.get().uri("/api/v1/drift/{t}/{m}/count?minutes={min}", tenant, model, minutes)
                .retrieve().body(String.class);
        return ResponseEntity.ok().header("Content-Type", "application/json").body(body);
    }
}
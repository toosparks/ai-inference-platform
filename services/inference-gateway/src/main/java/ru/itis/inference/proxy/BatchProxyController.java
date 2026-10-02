package ru.itis.inference.proxy;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;

@Slf4j
@RestController
@RequestMapping("/api/v1/batch")
public class BatchProxyController {

    private final RestClient client;

    public BatchProxyController(@Value("${batch.url}") String url) {
        this.client = RestClient.builder().baseUrl(url).build();
    }

    @GetMapping("/health")
    public String health() {
        return client.get().uri("/api/v1/batch/health").retrieve().body(String.class);
    }

    @PostMapping
    public ResponseEntity<String> submit(@RequestBody String body) {
        String result = client.post().uri("/api/v1/batch")
                .header("Content-Type", "application/json")
                .body(body)
                .retrieve().body(String.class);
        return ResponseEntity.ok().header("Content-Type", "application/json").body(result);
    }

    @GetMapping("/{jobId}")
    public ResponseEntity<String> getJob(@PathVariable String jobId) {
        String body = client.get().uri("/api/v1/batch/{j}", jobId).retrieve().body(String.class);
        return ResponseEntity.ok().header("Content-Type", "application/json").body(body);
    }

    @GetMapping("/{jobId}/results")
    public ResponseEntity<String> getResults(@PathVariable String jobId) {
        String body = client.get().uri("/api/v1/batch/{j}/results", jobId).retrieve().body(String.class);
        return ResponseEntity.ok().header("Content-Type", "application/json").body(body);
    }
}
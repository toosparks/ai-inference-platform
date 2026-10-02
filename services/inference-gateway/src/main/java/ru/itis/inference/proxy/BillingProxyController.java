package ru.itis.inference.proxy;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;

@Slf4j
@RestController
@RequestMapping("/api/v1/billing")
public class BillingProxyController {

    private final RestClient client;

    public BillingProxyController(@Value("${billing.url}") String url) {
        this.client = RestClient.builder().baseUrl(url).build();
    }

    @GetMapping("/health")
    public String health() {
        return client.get().uri("/api/v1/billing/health").retrieve().body(String.class);
    }

    @GetMapping
    public ResponseEntity<String> getAll() {
        String body = client.get().uri("/api/v1/billing").retrieve().body(String.class);
        return ResponseEntity.ok().header("Content-Type", "application/json").body(body);
    }

    @GetMapping("/{tenant}")
    public ResponseEntity<String> getByTenant(@PathVariable String tenant) {
        String body = client.get().uri("/api/v1/billing/{t}", tenant).retrieve().body(String.class);
        return ResponseEntity.ok().header("Content-Type", "application/json").body(body);
    }
}
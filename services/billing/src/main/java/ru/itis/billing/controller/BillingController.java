package ru.itis.billing.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.itis.billing.dto.BillingResponse;
import ru.itis.billing.service.BillingService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/billing")
@RequiredArgsConstructor
public class BillingController {

    private final BillingService billingService;

    @GetMapping("/health")
    public String health() {
        return "OK";
    }

    @GetMapping
    public List<BillingResponse> getAll() {
        return billingService.getAll();
    }

    @GetMapping("/{tenant}")
    public List<BillingResponse> getByTenant(@PathVariable String tenant) {
        return billingService.getByTenant(tenant);
    }
}
package ru.itis.drift.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.itis.drift.dto.DriftReport;
import ru.itis.drift.repository.EventRepository;
import ru.itis.drift.service.DriftService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/drift")
@RequiredArgsConstructor
public class DriftController {

    private final DriftService driftService;
    private final EventRepository eventRepository;

    @GetMapping("/health")
    public String health() {
        return "OK";
    }

    /** Проверка дрифта для конкретной модели. */
    @GetMapping("/{tenant}/{model}")
    public DriftReport getForTenantModel(
            @PathVariable String tenant,
            @PathVariable String model) {
        return driftService.analyzeOne(tenant, model);
    }

    /** Запустить анализ по всем парам немедленно. */
    @PostMapping("/analyze")
    public List<DriftReport> analyzeNow() {
        return driftService.analyzeAll();
    }

    /** Сколько событий пришло за окно. */
    @GetMapping("/{tenant}/{model}/count")
    public Map<String, Object> countEvents(
            @PathVariable String tenant,
            @PathVariable String model,
            @RequestParam(defaultValue = "60") int minutes) {
        long count = eventRepository.countEvents(tenant, model, minutes);
        return Map.of("tenant", tenant, "model", model, "minutes", minutes, "count", count);
    }
}
package ru.itis.registry.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.itis.registry.dto.ModelRequest;
import ru.itis.registry.dto.ModelResponse;
import ru.itis.registry.service.ModelService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/models")
@RequiredArgsConstructor
public class ModelController {

    private final ModelService modelService;

    @GetMapping("/health")
    public String health() {
        return "OK";
    }

    @PostMapping
    public ModelResponse register(@RequestBody ModelRequest request) {
        return modelService.register(request);
    }

    @GetMapping
    public List<ModelResponse> getAll() {
        return modelService.getAll();
    }

    @GetMapping("/{name}")
    public List<ModelResponse> getByName(@PathVariable String name) {
        return modelService.getByName(name);
    }

    @GetMapping("/{name}/{version}")
    public ModelResponse getByNameAndVersion(
            @PathVariable String name,
            @PathVariable String version) {
        return modelService.getByNameAndVersion(name, version);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        modelService.delete(id);
    }
}
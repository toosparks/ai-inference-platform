package ru.itis.inference.controller;

import ai.djl.translate.TranslateException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.itis.inference.dto.PredictRequest;
import ru.itis.inference.dto.PredictResponse;
import ru.itis.inference.service.InferenceService;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class InferenceController {

    private final InferenceService inferenceService;

    @GetMapping("/health")
    public String health() {
        return "OK";
    }

    @PostMapping("/predict")
    public PredictResponse predict(@RequestBody PredictRequest request) throws TranslateException {
        return inferenceService.predict(request);
    }
}
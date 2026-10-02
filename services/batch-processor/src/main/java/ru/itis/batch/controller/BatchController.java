package ru.itis.batch.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.itis.batch.dto.BatchJobResponse;
import ru.itis.batch.dto.BatchRequest;
import ru.itis.batch.entity.BatchResult;
import ru.itis.batch.service.BatchService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/batch")
@RequiredArgsConstructor
public class BatchController {

    private final BatchService batchService;

    @GetMapping("/health")
    public String health() {
        return "OK";
    }

    @PostMapping
    public BatchJobResponse submit(@RequestBody BatchRequest request) {
        return batchService.submit(request);
    }

    @GetMapping("/{jobId}")
    public BatchJobResponse getJob(@PathVariable UUID jobId) {
        return batchService.getJob(jobId);
    }

    @GetMapping("/{jobId}/results")
    public List<BatchResult> getResults(@PathVariable UUID jobId) {
        return batchService.getResults(jobId);
    }
}
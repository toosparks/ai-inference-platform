package ru.itis.batch.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.itis.batch.dto.BatchJobResponse;
import ru.itis.batch.dto.BatchRequest;
import ru.itis.batch.entity.BatchJob;
import ru.itis.batch.entity.BatchResult;
import ru.itis.batch.kafka.BatchEventProducer;
import ru.itis.batch.repository.BatchJobRepository;
import ru.itis.batch.repository.BatchResultRepository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class BatchService {

    private final BatchJobRepository jobRepository;
    private final BatchResultRepository resultRepository;
    private final BatchEventProducer producer;

    public BatchJobResponse submit(BatchRequest request) {
        UUID jobId = UUID.randomUUID();
        BatchJob job = new BatchJob(jobId, request.tenant(), request.model(), request.texts().size());
        jobRepository.save(job);

        producer.sendBatchRequested(jobId, request.model(), request.tenant(), request.texts());

        log.info("Submitted batch job: id={}, texts={}", jobId, request.texts().size());
        return toResponse(job);
    }

    public BatchJobResponse getJob(UUID jobId) {
        return jobRepository.findById(jobId)
                .map(this::toResponse)
                .orElseThrow(() -> new RuntimeException("Job not found: " + jobId));
    }

    public List<BatchResult> getResults(UUID jobId) {
        return resultRepository.findByKeyJobId(jobId);
    }

    @Transactional
    public void saveResult(UUID jobId, int index, String text, String result, double confidence) {
        resultRepository.save(new BatchResult(jobId, index, text, result, confidence));
    }

    @Transactional
    public void updateJobStatus(UUID jobId, String status, int processedCount) {
        jobRepository.findById(jobId).ifPresent(job -> {
            job.setStatus(status);
            job.setProcessedCount(processedCount);
            if ("COMPLETED".equals(status) || "FAILED".equals(status)) {
                job.setCompletedAt(Instant.now());
            }
            jobRepository.save(job);
        });
    }

    private BatchJobResponse toResponse(BatchJob j) {
        return new BatchJobResponse(
                j.getJobId(), j.getTenant(), j.getModel(), j.getStatus(),
                j.getTotalCount(), j.getProcessedCount(),
                j.getCreatedAt(), j.getCompletedAt()
        );
    }
}
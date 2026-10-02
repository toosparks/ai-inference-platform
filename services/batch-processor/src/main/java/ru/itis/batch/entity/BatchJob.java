package ru.itis.batch.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.time.Instant;
import java.util.UUID;

@Table("batch_jobs")
@Getter
@Setter
public class BatchJob {

    @PrimaryKey
    private UUID jobId;

    @Column("tenant")
    private String tenant;

    @Column("model")
    private String model;

    @Column("status")
    private String status;   // PENDING, PROCESSING, COMPLETED, FAILED

    @Column("total_count")
    private int totalCount;

    @Column("processed_count")
    private int processedCount;

    @Column("created_at")
    private Instant createdAt;

    @Column("completed_at")
    private Instant completedAt;

    public BatchJob() {}

    public BatchJob(UUID jobId, String tenant, String model, int totalCount) {
        this.jobId = jobId;
        this.tenant = tenant;
        this.model = model;
        this.totalCount = totalCount;
        this.processedCount = 0;
        this.status = "PENDING";
        this.createdAt = Instant.now();
    }
}
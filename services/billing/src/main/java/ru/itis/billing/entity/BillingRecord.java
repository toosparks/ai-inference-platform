package ru.itis.billing.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "billing_records",
        uniqueConstraints = @UniqueConstraint(columnNames = {"tenant", "model"}))
@Getter
@Setter
public class BillingRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String tenant;

    @Column(nullable = false)
    private String model;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false)
    private long requestCount;

    @Column(nullable = false)
    private long totalInputTokens;

    @Column(nullable = false)
    private long totalOutputTokens;

    @Column(nullable = false)
    private long totalDurationMs;

    public BillingRecord() {}

    public BillingRecord(String tenant, String model, String category) {
        this.tenant = tenant;
        this.model = model;
        this.category = category;
        this.requestCount = 0;
        this.totalInputTokens = 0;
        this.totalOutputTokens = 0;
        this.totalDurationMs = 0;
    }

    public void addUsage(int inputTokens, int outputTokens, long durationMs) {
        this.requestCount++;
        this.totalInputTokens += inputTokens;
        this.totalOutputTokens += outputTokens;
        this.totalDurationMs += durationMs;
    }
}
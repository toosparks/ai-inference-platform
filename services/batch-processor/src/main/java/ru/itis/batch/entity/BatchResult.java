package ru.itis.batch.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.*;

import java.io.Serializable;
import java.util.UUID;

@Table("batch_results")
@Getter
@Setter
public class BatchResult {

    @PrimaryKeyClass
    @Getter
    @Setter
    public static class Key implements Serializable {
        @PrimaryKeyColumn(name = "job_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
        private UUID jobId;

        @PrimaryKeyColumn(name = "text_index", ordinal = 1, type = PrimaryKeyType.CLUSTERED)
        private int textIndex;

        public Key() {}

        public Key(UUID jobId, int textIndex) {
            this.jobId = jobId;
            this.textIndex = textIndex;
        }
    }

    @PrimaryKey
    private Key key;

    @Column("text")
    private String text;

    @Column("result")
    private String result;

    @Column("confidence")
    private double confidence;

    public BatchResult() {}

    public BatchResult(UUID jobId, int textIndex, String text, String result, double confidence) {
        this.key = new Key(jobId, textIndex);
        this.text = text;
        this.result = result;
        this.confidence = confidence;
    }
}
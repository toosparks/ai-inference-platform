CREATE DATABASE IF NOT EXISTS aiplatform;

CREATE TABLE IF NOT EXISTS aiplatform.inference_events (
                                                           tenant        String,
                                                           model         String,
                                                           category      String,
                                                           result        String,
                                                           confidence    Float64,
                                                           input_tokens  Int32,
                                                           output_tokens Int32,
                                                           duration_ms   Int64,
                                                           event_time    DateTime64(3, 'UTC')
    ) ENGINE = MergeTree()
    ORDER BY (tenant, model, event_time);
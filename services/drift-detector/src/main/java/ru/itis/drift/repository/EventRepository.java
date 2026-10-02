package ru.itis.drift.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.itis.drift.dto.InferenceEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Repository
@RequiredArgsConstructor
public class EventRepository {

    private final JdbcTemplate jdbcTemplate;

    public void save(InferenceEvent e) {
        jdbcTemplate.update(
                "INSERT INTO aiplatform.inference_events " +
                        "(tenant, model, category, result, confidence, input_tokens, output_tokens, duration_ms, event_time) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, fromUnixTimestamp64Milli(?))",
                e.tenant(), e.model(), e.category(), e.result(), e.confidence(),
                e.inputTokens(), e.outputTokens(), e.durationMs(), e.timestamp()
        );
    }

    /** Распределение результатов модели за окно (минуты). */
    public Map<String, Double> getDistribution(String tenant, String model, int windowMinutes) {
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                "SELECT result, count() AS cnt FROM aiplatform.inference_events " +
                        "WHERE tenant = ? AND model = ? AND event_time >= now() - INTERVAL ? MINUTE " +
                        "GROUP BY result",
                tenant, model, windowMinutes
        );

        long total = 0;
        Map<String, Long> counts = new HashMap<>();
        for (Map<String, Object> row : rows) {
            String result = (String) row.get("result");
            long cnt = ((Number) row.get("cnt")).longValue();
            counts.put(result, cnt);
            total += cnt;
        }

        Map<String, Double> distribution = new HashMap<>();
        if (total == 0) return distribution;
        for (Map.Entry<String, Long> entry : counts.entrySet()) {
            distribution.put(entry.getKey(), (double) entry.getValue() / total);
        }
        return distribution;
    }

    /** Список пар (tenant, model), по которым есть события. */
    public List<Map<String, Object>> getActiveTenantModelPairs(int windowMinutes) {
        return jdbcTemplate.queryForList(
                "SELECT DISTINCT tenant, model FROM aiplatform.inference_events " +
                        "WHERE event_time >= now() - INTERVAL ? MINUTE",
                windowMinutes
        );
    }

    public long countEvents(String tenant, String model, int windowMinutes) {
        Long count = jdbcTemplate.queryForObject(
                "SELECT count() FROM aiplatform.inference_events " +
                        "WHERE tenant = ? AND model = ? AND event_time >= now() - INTERVAL ? MINUTE",
                Long.class,
                tenant, model, windowMinutes
        );
        return count != null ? count : 0L;
    }
}
package ru.itis.drift.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.itis.drift.dto.DriftReport;
import ru.itis.drift.service.DriftService;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DriftScheduler {

    private final DriftService driftService;

    @Scheduled(fixedRate = 60000)
    public void runDriftAnalysis() {
        try {
            List<DriftReport> reports = driftService.analyzeAll();
            if (!reports.isEmpty()) {
                log.info("Drift analysis done: {} pairs checked, {} alerts",
                        reports.size(),
                        reports.stream().filter(DriftReport::drifted).count());
            }
        } catch (Exception e) {
            log.error("Scheduled drift analysis failed", e);
        }
    }
}
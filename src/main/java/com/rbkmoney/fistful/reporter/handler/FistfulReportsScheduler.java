package com.rbkmoney.fistful.reporter.handler;

import com.rbkmoney.fistful.reporter.component.ReportGenerator;
import com.rbkmoney.fistful.reporter.domain.tables.pojos.Report;
import com.rbkmoney.fistful.reporter.service.ReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class FistfulReportsScheduler {

    private final ReportService reportService;
    private final ReportGenerator reportGenerator;

    @Value("${reporting.pollingEnable:true}")
    private boolean pollingEnabled;

    @Scheduled(fixedDelayString = "${reporting.pollingDelay:3000}")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void processPendingReports() {
        if (pollingEnabled) {
            List<Report> reports = reportService.getPendingReports();
            if (!reports.isEmpty()) {
                log.debug("Trying to process {} pending reports", reports.size());
                reports.forEach(reportGenerator::generateReportFile);
            }
        }
    }
}

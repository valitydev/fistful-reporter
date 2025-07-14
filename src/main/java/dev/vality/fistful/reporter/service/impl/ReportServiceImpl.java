package dev.vality.fistful.reporter.service.impl;

import dev.vality.dao.DaoException;
import dev.vality.fistful.reporter.config.properties.ReportingProperties;
import dev.vality.fistful.reporter.dao.ReportDao;
import dev.vality.fistful.reporter.domain.enums.ReportStatus;
import dev.vality.fistful.reporter.domain.tables.pojos.Report;
import dev.vality.fistful.reporter.exception.ReportNotFoundException;
import dev.vality.fistful.reporter.exception.StorageException;
import dev.vality.fistful.reporter.service.ReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final ReportingProperties reportingProperties;
    private final ReportDao reportDao;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public long createReport(String partyId, Instant fromTime, Instant toTime, String reportType) {
        return createReport(
                partyId,
                fromTime,
                toTime,
                reportType,
                reportingProperties.getDefaultTimeZone(),
                Instant.now().truncatedTo(ChronoUnit.MICROS));
    }

    private long createReport(
            String partyId,
            Instant fromTime,
            Instant toTime,
            String reportType,
            ZoneId timezone,
            Instant createdAt) {
        try {
            log.info(
                    "Trying to create report, partyId={}, reportType={}, fromTime={}, toTime={}",
                    partyId, reportType, fromTime, toTime
            );
            Report report = new Report();
            report.setPartyId(partyId);
            report.setFromTime(LocalDateTime.ofInstant(fromTime, ZoneOffset.UTC));
            report.setToTime(LocalDateTime.ofInstant(toTime, ZoneOffset.UTC));
            report.setType(reportType);
            report.setTimezone(timezone.getId());
            report.setCreatedAt(LocalDateTime.ofInstant(createdAt, ZoneOffset.UTC));
            long reportId = reportDao.save(report);
            log.info(
                    "Report has been successfully created, " +
                            "reportId={}, shopId={}, reportType={}, fromTime={}, toTime={}",
                    reportId, partyId, reportType, fromTime, toTime
            );
            return reportId;
        } catch (DaoException ex) {
            throw new StorageException(
                    String.format(
                            "Failed to save report in storage, " +
                                    "partyId=%s, fromTime=%s, toTime=%s, reportType=%s",
                            partyId, fromTime, toTime, reportType
                    ),
                    ex
            );
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public List<Report> getReportsByRange(
            String partyId,
            Instant fromTime,
            Instant toTime,
            List<String> reportTypes) {
        try {
            log.info("Trying to get reports by range, partyId={}", partyId);
            List<Report> reportsByRange = reportDao.getReportsByRange(
                    partyId,
                    LocalDateTime.ofInstant(fromTime, ZoneOffset.UTC),
                    LocalDateTime.ofInstant(toTime, ZoneOffset.UTC),
                    reportTypes
            );
            log.info("Reports by range has been found, partyId={}", partyId);
            return reportsByRange;
        } catch (DaoException ex) {
            throw new StorageException(
                    String.format(
                            "Failed to get reports by range, " +
                                    "partyId=%s, contractId=%s, fromTime=%s, toTime=%s, reportTypes=%s",
                            partyId, fromTime, toTime, reportTypes
                    ),
                    ex
            );
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public List<Report> getReportsByRangeNotCancelled(
            String partyId,
            Instant fromTime, Instant toTime,
            List<String> reportTypes) {
        return getReportsByRange(partyId, fromTime, toTime, reportTypes).stream()
                .filter(report -> report.getStatus() != ReportStatus.cancelled)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Report getReport(String partyId, long reportId) {
        try {
            log.info("Trying to get report, reportId={}, partyId={}", reportId, partyId);
            Report report = reportDao.getReport(reportId, partyId);
            if (report == null) {
                throw new ReportNotFoundException(String.format("Report not found, " +
                        "partyId=%s, reportId=%d", partyId, reportId));
            }
            log.info("Report has been found, reportId={}, partyId={}", reportId, partyId);
            return report;
        } catch (DaoException ex) {
            throw new StorageException(
                    String.format(
                            "Failed to get report from storage, partyId=%s, reportId=%d",
                            partyId, reportId
                    ),
                    ex
            );
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void cancelReport(String partyId, long reportId) {
        log.info("Trying to cancel report, reportId={}", reportId);
        Report report = getReport(partyId, reportId);
        changeReportStatus(report, ReportStatus.cancelled);
        log.info("Report has been cancelled, reportId={}", reportId);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void changeReportStatus(Report report, ReportStatus reportStatus) {
        try {
            log.info("Trying to change report status, reportId={}, reportStatus={}", report.getId(), reportStatus);
            reportDao.changeReportStatus(report.getId(), reportStatus);
            log.info("Report status has been successfully changed, " +
                    "reportId={}, reportStatus={}", report.getId(), reportStatus);
        } catch (DaoException ex) {
            throw new StorageException(
                    String.format("Failed to change report status, " +
                            "reportId=%d, reportStatus=%s", report.getId(), reportStatus),
                    ex);
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public List<Report> getPendingReports() {
        try {
            log.info("Trying to get reports with status, reportStatus=pending");
            List<Report> pendingReports = reportDao.getPendingReports();
            String reportIds = pendingReports.stream()
                    .map(Report::getId)
                    .map(String::valueOf)
                    .collect(Collectors.joining(", ", "[", "]"));
            log.info("{} reports has been found, " +
                    "reportStatus=pending, reportIds={}", pendingReports.size(), reportIds);
            return pendingReports;
        } catch (DaoException ex) {
            throw new StorageException("Failed to get pending reports", ex);
        }
    }
}

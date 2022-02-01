package dev.vality.fistful.reporter.service;

import dev.vality.fistful.reporter.domain.enums.ReportStatus;
import dev.vality.fistful.reporter.domain.tables.pojos.Report;

import java.time.Instant;
import java.util.List;

public interface ReportService {

    long createReport(String partyId, String contractId, Instant fromTime, Instant toTime, String reportType);

    List<Report> getReportsByRange(
            String partyId,
            String contractId,
            Instant fromTime,
            Instant toTime,
            List<String> reportTypes);

    List<Report> getReportsByRangeNotCancelled(
            String partyId,
            String contractId,
            Instant fromTime,
            Instant toTime,
            List<String> reportTypes);

    Report getReport(String partyId, String contractId, long reportId);

    void cancelReport(String partyId, String shopId, long reportId);

    void changeReportStatus(Report report, ReportStatus reportStatus);

    List<Report> getPendingReports();

}

package dev.vality.fistful.reporter.dao;

import dev.vality.dao.GenericDao;
import dev.vality.fistful.reporter.domain.enums.ReportStatus;
import dev.vality.fistful.reporter.domain.tables.pojos.Report;

import java.time.LocalDateTime;
import java.util.List;

public interface ReportDao extends GenericDao {

    long save(Report report);

    List<Report> getReportsByRange(
            String partyId,
            String contractId,
            LocalDateTime fromTime,
            LocalDateTime toTime,
            List<String> reportTypes);

    Report getReport(long reportId, String partyId, String contractId);

    void changeReportStatus(Long reportId, ReportStatus reportStatus);

    List<Report> getPendingReports();
}

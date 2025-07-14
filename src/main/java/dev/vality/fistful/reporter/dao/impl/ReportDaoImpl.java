package dev.vality.fistful.reporter.dao.impl;

import com.zaxxer.hikari.HikariDataSource;
import dev.vality.dao.impl.AbstractGenericDao;
import dev.vality.fistful.reporter.dao.ReportDao;
import dev.vality.fistful.reporter.dao.mapper.RecordRowMapper;
import dev.vality.fistful.reporter.domain.enums.ReportStatus;
import dev.vality.fistful.reporter.domain.tables.pojos.Report;
import dev.vality.fistful.reporter.domain.tables.records.ReportRecord;
import org.jooq.Condition;
import org.jooq.Query;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

import static dev.vality.fistful.reporter.domain.tables.Report.REPORT;

@Component
public class ReportDaoImpl extends AbstractGenericDao implements ReportDao {

    private final RecordRowMapper<Report> reportRowMapper;

    public ReportDaoImpl(HikariDataSource dataSource) {
        super(dataSource);
        reportRowMapper = new RecordRowMapper<>(REPORT, Report.class);
    }

    @Override
    public long save(Report report) {
        ReportRecord record = getDslContext().newRecord(REPORT, report);
        Query query = getDslContext().insertInto(REPORT).set(record).returning(REPORT.ID);

        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        executeOne(query, keyHolder);
        return keyHolder.getKey().longValue();
    }

    @Override
    public List<Report> getReportsByRange(
            String partyId,
            LocalDateTime fromTime,
            LocalDateTime toTime,
            List<String> reportTypes) {
        Condition condition = REPORT.PARTY_ID.eq(partyId)
                .and(REPORT.FROM_TIME.ge(fromTime))
                .and(REPORT.TO_TIME.le(toTime));

        if (!reportTypes.isEmpty()) {
            condition = condition.and(REPORT.TYPE.in(reportTypes));
        }

        Query query = getDslContext().selectFrom(REPORT).where(condition);

        return fetch(query, reportRowMapper);
    }

    @Override
    public Report getReport(long reportId, String partyId) {
        Condition condition = REPORT.ID.eq(reportId)
                .and(REPORT.PARTY_ID.eq(partyId));
        Query query = getDslContext().selectFrom(REPORT).where(condition);

        return fetchOne(query, reportRowMapper);
    }

    @Override
    public void changeReportStatus(Long reportId, ReportStatus reportStatus) {
        Condition condition = REPORT.ID.eq(reportId);
        Query query = getDslContext().update(REPORT).set(REPORT.STATUS, reportStatus).where(condition);

        executeOne(query);
    }

    @Override
    public List<Report> getPendingReports() {
        Condition condition = REPORT.STATUS.eq(ReportStatus.pending);
        Query query = getDslContext().selectFrom(REPORT).where(condition).forUpdate();

        return fetch(query, reportRowMapper);
    }
}

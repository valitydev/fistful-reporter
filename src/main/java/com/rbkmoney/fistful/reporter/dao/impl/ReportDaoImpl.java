package com.rbkmoney.fistful.reporter.dao.impl;

import com.rbkmoney.fistful.reporter.dao.ReportDao;
import com.rbkmoney.fistful.reporter.dao.mapper.RecordRowMapper;
import com.rbkmoney.fistful.reporter.domain.enums.ReportStatus;
import com.rbkmoney.fistful.reporter.domain.tables.pojos.Report;
import com.rbkmoney.fistful.reporter.exception.DaoException;
import org.jooq.Condition;
import org.jooq.Query;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.util.List;

import static com.rbkmoney.fistful.reporter.domain.tables.Report.REPORT;

@Component
public class ReportDaoImpl extends AbstractGenericDao implements ReportDao {

    private final RecordRowMapper<Report> reportRowMapper;

    public ReportDaoImpl(DataSource dataSource) {
        super(dataSource);
        reportRowMapper = new RecordRowMapper<>(REPORT, Report.class);
    }

    @Override
    public List<Report> getReportsByRange(String partyId, String contractId, LocalDateTime fromTime, LocalDateTime toTime, List<String> reportTypes) throws DaoException {
        Condition condition = REPORT.PARTY_ID.eq(partyId)
                .and(REPORT.CONTRACT_ID.eq(contractId))
                .and(REPORT.FROM_TIME.ge(fromTime))
                .and(REPORT.TO_TIME.le(toTime));

        if (!reportTypes.isEmpty()) {
            condition = condition.and(REPORT.TYPE.in(reportTypes));
        }

        Query query = getDslContext().selectFrom(REPORT).where(condition);

        return fetch(query, reportRowMapper);
    }

    @Override
    public Report getReport(String partyId, String contractId, long reportId) throws DaoException {
        Query query = getDslContext().selectFrom(REPORT).where(
                REPORT.ID.eq(reportId)
                        .and(REPORT.PARTY_ID.eq(partyId))
                        .and(REPORT.CONTRACT_ID.eq(contractId))
        );
        return fetchOne(query, reportRowMapper);
    }

    @Override
    public long createReport(String partyId, String contractId, LocalDateTime fromTime, LocalDateTime toTime, String reportType, String timezone, LocalDateTime createdAt) throws DaoException {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();

        Query query = getDslContext().insertInto(REPORT)
                .set(REPORT.PARTY_ID, partyId)
                .set(REPORT.CONTRACT_ID, contractId)
                .set(REPORT.FROM_TIME, fromTime)
                .set(REPORT.TO_TIME, toTime)
                .set(REPORT.CREATED_AT, createdAt)
                .set(REPORT.TYPE, reportType)
                .set(REPORT.TIMEZONE, timezone)
                .returning(REPORT.ID);

        executeOne(query, keyHolder);
        return keyHolder.getKey().longValue();
    }

    @Override
    public void changeReportStatus(Long reportId, ReportStatus reportStatus) throws DaoException {
        Query query = getDslContext().update(REPORT)
                .set(REPORT.STATUS, reportStatus)
                .where(REPORT.ID.eq(reportId));

        executeOne(query);
    }
}

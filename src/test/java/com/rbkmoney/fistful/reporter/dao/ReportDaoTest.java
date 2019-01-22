package com.rbkmoney.fistful.reporter.dao;

import com.rbkmoney.fistful.reporter.AbstractIntegrationTest;
import com.rbkmoney.fistful.reporter.domain.enums.ReportStatus;
import com.rbkmoney.fistful.reporter.domain.tables.pojos.Report;
import com.rbkmoney.fistful.reporter.exception.DaoException;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static io.github.benas.randombeans.api.EnhancedRandom.randomListOf;
import static org.junit.Assert.assertEquals;

public class ReportDaoTest extends AbstractIntegrationTest {

    @Autowired
    private ReportDao reportDao;

    private static final String PARTY_ID = "0";
    private static final String CONTRACT_ID = "0";

    @Test
    public void test() throws DaoException {
        LocalDateTime toTime = LocalDateTime.now();
        LocalDateTime fromTime = LocalDateTime.now().minusHours(1);
        Report report = random(Report.class);
        configReport(report, fromTime, toTime);
        long id = reportDao.save(report);
        Report expectedReport = reportDao.getReport(id, report.getPartyId(), report.getContractId());
        assertEquals(expectedReport, report);

        List<Report> reports = randomListOf(4, Report.class);
        for (Report r : reports) {
            configReport(r, fromTime, toTime);
            reportDao.save(r);
        }
        assertEquals(reportDao.getPendingReports().size(), 5);

        reportDao.changeReportStatus(id, ReportStatus.created);
        assertEquals(reportDao.getPendingReports().size(), 4);

        assertEquals(reportDao.getReportsByRange(PARTY_ID, CONTRACT_ID, fromTime, toTime, Collections.emptyList()).size(), 5);

        assertEquals(reportDao.getReportsByRange(PARTY_ID, CONTRACT_ID, fromTime.plusMinutes(1), toTime, Collections.emptyList()).size(), 0);

    }

    private void configReport(Report r, LocalDateTime fromTime, LocalDateTime toTime) {
        r.setStatus(ReportStatus.pending);
        r.setFromTime(fromTime);
        r.setToTime(toTime);
        r.setPartyId(PARTY_ID);
        r.setContractId(CONTRACT_ID);
    }
}

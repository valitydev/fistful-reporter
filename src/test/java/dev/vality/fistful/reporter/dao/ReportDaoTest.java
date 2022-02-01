package dev.vality.fistful.reporter.dao;

import dev.vality.fistful.reporter.config.PostgresqlSpringBootITest;
import dev.vality.fistful.reporter.domain.enums.ReportStatus;
import dev.vality.fistful.reporter.domain.tables.pojos.Report;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static dev.vality.fistful.reporter.data.TestData.contractId;
import static dev.vality.fistful.reporter.data.TestData.partyId;
import static dev.vality.testcontainers.annotations.util.RandomBeans.random;
import static dev.vality.testcontainers.annotations.util.RandomBeans.randomListOf;
import static dev.vality.testcontainers.annotations.util.ValuesGenerator.getFromTime;
import static dev.vality.testcontainers.annotations.util.ValuesGenerator.getToTime;
import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.assertEquals;

@PostgresqlSpringBootITest
public class ReportDaoTest {

    @Autowired
    private ReportDao reportDao;

    @Test
    public void reportDaoTest() {
        Report report = random(Report.class);
        configReport(report, getFromTime().truncatedTo(ChronoUnit.MICROS), getToTime().truncatedTo(ChronoUnit.MICROS));
        long id = reportDao.save(report);
        Report expectedReport = reportDao.getReport(id, report.getPartyId(), report.getContractId());
        assertEquals(report, expectedReport);

        List<Report> reports = randomListOf(4, Report.class);
        for (Report r : reports) {
            configReport(r, getFromTime().truncatedTo(ChronoUnit.MICROS), getToTime().truncatedTo(ChronoUnit.MICROS));
            reportDao.save(r);
        }
        assertEquals(5, reportDao.getPendingReports().size());

        reportDao.changeReportStatus(id, ReportStatus.created);
        assertEquals(4, reportDao.getPendingReports().size());

        assertEquals(5, reportDao.getReportsByRange(
                partyId,
                contractId,
                getFromTime().truncatedTo(ChronoUnit.MICROS),
                getToTime().truncatedTo(ChronoUnit.MICROS),
                emptyList()).size());

        assertEquals(0, reportDao.getReportsByRange(
                partyId,
                contractId,
                getFromTime().plusMinutes(1).truncatedTo(ChronoUnit.MICROS),
                getToTime().truncatedTo(ChronoUnit.MICROS),
                emptyList()).size());
    }

    private void configReport(Report r, LocalDateTime fromTime, LocalDateTime toTime) {
        r.setStatus(ReportStatus.pending);
        r.setFromTime(fromTime);
        r.setToTime(toTime);
        r.setPartyId(partyId);
        r.setContractId(contractId);
    }
}

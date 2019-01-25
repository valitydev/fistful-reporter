package com.rbkmoney.fistful.reporter.service.impl;

import com.rbkmoney.fistful.reporter.AbstractIntegrationTest;
import com.rbkmoney.fistful.reporter.domain.enums.ReportStatus;
import com.rbkmoney.fistful.reporter.domain.tables.pojos.Report;
import com.rbkmoney.fistful.reporter.service.ReportService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.ZoneOffset;
import java.util.List;

import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;
import static java.util.stream.IntStream.range;
import static org.junit.Assert.assertEquals;

public class ReportServiceImplTest extends AbstractIntegrationTest {

    private static final String PARTY_ID = generateString();
    private static final String CONTRACT_ID = generateString();

    @Autowired
    private ReportService reportService;

    @Test
    public void test() {
        List<Long> reportIds = range(0, 5)
                .mapToLong(i -> createReport("withdrawalRegistry"))
                .boxed()
                .collect(toList());
        reportIds.add(createReport("anotherReportType"));

        assertEquals(
                reportService.getReportsByRange(
                        PARTY_ID,
                        CONTRACT_ID,
                        fromTime.toInstant(ZoneOffset.UTC),
                        toTime.toInstant(ZoneOffset.UTC),
                        singletonList("withdrawalRegistry")
                )
                        .size(),
                5
        );

        Long reportId = reportIds.get(0);
        Report report = reportService.getReport(PARTY_ID, CONTRACT_ID, reportId);
        assertEquals(report.getToTime(), toTime);

        reportService.cancelReport(PARTY_ID, CONTRACT_ID, reportId);

        assertEquals(
                reportService.getReportsByRangeNotCancelled(
                        PARTY_ID,
                        CONTRACT_ID,
                        fromTime.toInstant(ZoneOffset.UTC),
                        toTime.toInstant(ZoneOffset.UTC),
                        singletonList("withdrawalRegistry")
                )
                        .size(),
                4
        );

        reportService.changeReportStatus(report, ReportStatus.created);

        assertEquals(
                reportService.getReportsByRangeNotCancelled(
                        PARTY_ID,
                        CONTRACT_ID,
                        fromTime.toInstant(ZoneOffset.UTC),
                        toTime.toInstant(ZoneOffset.UTC),
                        singletonList("withdrawalRegistry")
                )
                        .size(),
                5
        );

        assertEquals(reportService.getPendingReports().size(), 5);
    }

    private long createReport(String reportType) {
        return reportService.createReport(
                PARTY_ID,
                CONTRACT_ID,
                fromTime.toInstant(ZoneOffset.UTC),
                toTime.toInstant(ZoneOffset.UTC),
                reportType
        );
    }
}

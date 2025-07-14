package dev.vality.fistful.reporter.service;

import dev.vality.fistful.reporter.config.PostgresqlSpringBootITest;
import dev.vality.fistful.reporter.domain.enums.ReportStatus;
import dev.vality.fistful.reporter.domain.tables.pojos.Report;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static dev.vality.fistful.reporter.data.TestData.partyId;
import static dev.vality.testcontainers.annotations.util.ValuesGenerator.getFromTime;
import static dev.vality.testcontainers.annotations.util.ValuesGenerator.getToTime;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;
import static java.util.stream.IntStream.range;
import static org.junit.jupiter.api.Assertions.assertEquals;

@PostgresqlSpringBootITest
public class ReportServiceTest {

    @Autowired
    private ReportService reportService;

    @Test
    public void reportServiceTest() {
        List<Long> reportIds = range(0, 5)
                .mapToLong(i -> createReport("withdrawalRegistry"))
                .boxed()
                .collect(toList());
        reportIds.add(createReport("anotherReportType"));

        assertEquals(
                5,
                reportService.getReportsByRange(
                                partyId,
                                getFromTime().toInstant(ZoneOffset.UTC).truncatedTo(ChronoUnit.MICROS),
                                getToTime().toInstant(ZoneOffset.UTC).truncatedTo(ChronoUnit.MICROS),
                                singletonList("withdrawalRegistry")
                        )
                        .size()
        );

        Long reportId = reportIds.get(0);
        Report report = reportService.getReport(partyId, reportId);
        assertEquals(getToTime().truncatedTo(ChronoUnit.MICROS), report.getToTime().truncatedTo(ChronoUnit.MICROS));

        reportService.cancelReport(partyId, reportId);

        assertEquals(
                4,
                reportService.getReportsByRangeNotCancelled(
                                partyId,
                                getFromTime().toInstant(ZoneOffset.UTC).truncatedTo(ChronoUnit.MICROS),
                                getToTime().toInstant(ZoneOffset.UTC).truncatedTo(ChronoUnit.MICROS),
                                singletonList("withdrawalRegistry")
                        )
                        .size()
        );

        reportService.changeReportStatus(report, ReportStatus.created);

        assertEquals(
                5,
                reportService.getReportsByRangeNotCancelled(
                                partyId,
                                getFromTime().toInstant(ZoneOffset.UTC).truncatedTo(ChronoUnit.MICROS),
                                getToTime().toInstant(ZoneOffset.UTC).truncatedTo(ChronoUnit.MICROS),
                                singletonList("withdrawalRegistry")
                        )
                        .size()
        );

        assertEquals(5, reportService.getPendingReports().size());
    }

    private long createReport(String reportType) {
        return reportService.createReport(
                partyId,
                getFromTime().toInstant(ZoneOffset.UTC).truncatedTo(ChronoUnit.MICROS),
                getToTime().toInstant(ZoneOffset.UTC).truncatedTo(ChronoUnit.MICROS),
                reportType
        );
    }
}

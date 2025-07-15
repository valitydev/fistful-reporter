package dev.vality.fistful.reporter.service;

import dev.vality.fistful.reporter.config.PostgresqlSpringBootITest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.ZoneOffset;

import static dev.vality.fistful.reporter.data.TestData.partyId;
import static dev.vality.testcontainers.annotations.util.ValuesGenerator.getFromTime;
import static dev.vality.testcontainers.annotations.util.ValuesGenerator.getToTime;
import static java.util.stream.IntStream.range;
import static org.junit.jupiter.api.Assertions.assertEquals;

@PostgresqlSpringBootITest
public class FileInfoServiceTest {

    @Autowired
    private FileInfoService fileInfoService;

    @Autowired
    private ReportService reportService;

    @Test
    public void fileInfoServiceTest() {
        long reportId = reportService.createReport(
                partyId,
                getFromTime().toInstant(ZoneOffset.UTC),
                getToTime().toInstant(ZoneOffset.UTC),
                "withdrawalRegistry"
        );

        range(0, 4).forEach(i -> fileInfoService.save(reportId, String.valueOf(i)));

        assertEquals(4, fileInfoService.getFileDataIds(reportId).size());
        assertEquals(0, fileInfoService.getFileDataIds(reportId + 1).size());
    }
}

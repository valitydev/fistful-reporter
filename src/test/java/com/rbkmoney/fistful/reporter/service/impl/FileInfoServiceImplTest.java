package com.rbkmoney.fistful.reporter.service.impl;

import com.rbkmoney.fistful.reporter.AbstractIntegrationTest;
import com.rbkmoney.fistful.reporter.service.FileInfoService;
import com.rbkmoney.fistful.reporter.service.ReportService;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.ZoneOffset;

import static java.lang.String.valueOf;
import static java.util.stream.IntStream.range;
import static org.junit.Assert.assertEquals;

public class FileInfoServiceImplTest extends AbstractIntegrationTest {

    private static final int COUNT_ELEMENTS = 4;
    private static final String PARTY_ID = generateString();
    private static final String CONTRACT_ID = generateString();

    @Autowired
    private FileInfoService fileInfoService;

    @Autowired
    private ReportService reportService;

    private long reportId;

    @Before
    public void setUp() throws Exception {
        reportId = reportService.createReport(
                PARTY_ID,
                CONTRACT_ID,
                fromTime.toInstant(ZoneOffset.UTC),
                toTime.toInstant(ZoneOffset.UTC),
                "withdrawalRegistry"
        );
    }

    @Test
    public void test() {
        range(0, COUNT_ELEMENTS)
                .forEach(i -> fileInfoService.save(reportId, valueOf(i)));
        assertEquals(fileInfoService.getFileDataIds(reportId).size(), COUNT_ELEMENTS);
        assertEquals(fileInfoService.getFileDataIds(reportId + 1).size(), 0);
    }
}

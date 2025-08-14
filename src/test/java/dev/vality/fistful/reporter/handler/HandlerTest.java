package dev.vality.fistful.reporter.handler;

import dev.vality.fistful.reporter.*;
import dev.vality.fistful.reporter.config.PostgresqlSpringBootITest;
import dev.vality.fistful.reporter.config.testconfiguration.WithdrawalTestDao;
import dev.vality.fistful.reporter.generator.ReportGenerator;
import dev.vality.fistful.reporter.service.FileStorageService;
import dev.vality.fistful.reporter.service.ReportService;
import org.apache.thrift.TException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.UUID;

import static dev.vality.fistful.reporter.data.TestData.partyId;
import static dev.vality.geck.common.util.TypeUtil.temporalToString;
import static dev.vality.testcontainers.annotations.util.ValuesGenerator.getFromTime;
import static dev.vality.testcontainers.annotations.util.ValuesGenerator.getToTime;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@PostgresqlSpringBootITest
public class HandlerTest {

    @MockitoBean
    private FileStorageService fileStorageService;

    @Autowired
    private WithdrawalTestDao withdrawalTestDao;

    @Autowired
    private ReportService reportService;

    @Autowired
    private ReportGenerator reportGenerator;

    @Autowired
    private ReportingSrv.Iface reportHandler;

    private ReportRequest request;

    @BeforeEach
    public void setUp() {
        ReportTimeRange reportTimeRange = new ReportTimeRange(
                temporalToString(getFromTime()),
                temporalToString(getToTime())
        );
        request = new ReportRequest(partyId, reportTimeRange);
    }

    @Test
    public void exceptionArgTest() throws TException {
        assertThrows(
                InvalidRequest.class,
                () -> reportHandler.generateReport(request, "kek"));
    }

    @Test
    public void fistfulReporterTest() throws TException {
        when(fileStorageService.saveFile(any())).thenReturn(UUID.randomUUID().toString());

        withdrawalTestDao.saveWithdrawalsDependencies(5);

        long reportId = reportHandler.generateReport(request, "withdrawalRegistry");

        schedulerEmulation();

        Report report = reportHandler.getReport(partyId, reportId);

        assertEquals(ReportStatus.created, report.getStatus());
        assertEquals(1, report.getFileDataIds().size());
        verify(fileStorageService, times(1)).saveFile(any());
    }

    private void schedulerEmulation() {
        var pendingReports = reportService.getPendingReports();
        reportGenerator.generateReportFile(pendingReports.get(0));
    }
}

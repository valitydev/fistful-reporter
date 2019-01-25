package com.rbkmoney.fistful.reporter;

import com.palantir.docker.compose.configuration.DockerComposeFiles;
import com.palantir.docker.compose.configuration.ProjectName;
import com.palantir.docker.compose.connection.DockerMachine;
import com.palantir.docker.compose.connection.State;
import com.palantir.docker.compose.execution.DefaultDockerCompose;
import com.palantir.docker.compose.execution.DockerCompose;
import com.rbkmoney.damsel.domain.Contract;
import com.rbkmoney.file.storage.FileStorageSrv;
import com.rbkmoney.fistful.reporter.component.ReportGenerator;
import com.rbkmoney.fistful.reporter.service.PartyManagementService;
import com.rbkmoney.fistful.reporter.service.ReportService;
import com.rbkmoney.fistful.reporter.service.impl.WithdrawalRegistryTemplateServiceImpl;
import com.rbkmoney.woody.thrift.impl.http.THSpawnClientBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.thrift.TException;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.Resource;
import org.testcontainers.shaded.org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;

import static com.rbkmoney.geck.common.util.TypeUtil.temporalToString;
import static java.nio.file.Files.*;
import static java.time.LocalDateTime.now;
import static java.time.ZoneId.systemDefault;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.when;

@Slf4j
public class FistfulReporterIntegrationTest extends AbstractWithdrawalTest {

    private static final int TIMEOUT = 555000;

    @MockBean
    private PartyManagementService partyManagementService;

    @Autowired
    private HttpClient httpClient;

    @Autowired
    private ReportService reportService;

    @Autowired
    private WithdrawalRegistryTemplateServiceImpl withdrawalRegistryTemplateService;

    @Autowired
    private ReportGenerator reportGenerator;

    @Value("${fileStorage.healthCheckUrl}")
    private Resource healthCheckUrl;

    private ReportingSrv.Iface reportClient;
    private FileStorageSrv.Iface fileStorageClient;
    private ReportRequest request;
    private Path reportFile;
    private DockerCompose dockerCompose;

    @Before
    public void before() throws URISyntaxException, IOException {
        when(partyManagementService.getContract(anyString(), anyString())).thenReturn(new Contract());
        reportClient = new THSpawnClientBuilder()
                .withAddress(new URI("http://localhost:" + port + "/fistful/reports"))
                .withNetworkTimeout(TIMEOUT)
                .build(ReportingSrv.Iface.class);
        fileStorageClient = new THSpawnClientBuilder()
                .withAddress(new URI("http://localhost:42826/file_storage"))
                .withNetworkTimeout(TIMEOUT)
                .build(FileStorageSrv.Iface.class);
        ReportTimeRange reportTimeRange = new ReportTimeRange(
                temporalToString(fromTime),
                temporalToString(toTime)
        );
        request = new ReportRequest(partyId, contractId, reportTimeRange);
        reportFile = createTempFile("test", ".xlsx");
    }

    @Test(expected = InvalidRequest.class)
    public void exceptionArgTest() throws TException {
        reportClient.generateReport(request, "kek");
    }

    // comment ignore at local dev
    @Ignore
    @Test
    public void test() throws TException, IOException, InterruptedException {
        upConteiners();

        try {
            long reportId = reportClient.generateReport(request, "withdrawalRegistry");
            prepareForMainAssert();
            serverSideInitLogic();
            clientSideLogic(reportId);
        } finally {
            deleteIfExists(reportFile);
        }

        downConteiners();
    }

    private void upConteiners() throws IOException, InterruptedException {
        dockerCompose = new DefaultDockerCompose(
                DockerComposeFiles.from("src/test/resources/docker-compose.yml"),
                DockerMachine.localMachine().build(),
                ProjectName.random()
        );
        dockerCompose.up();

        waitingUpFileStorageContainer();

        assertEquals(dockerCompose.state("file-storage-test").Up, State.Up);
    }

    private void waitingUpFileStorageContainer() {
        boolean flag = true;
        while (flag) {
            try {
                Thread.sleep(2000);
                log.info("Waiting file storage up");
                HttpResponse httpResponse = httpClient.execute(new HttpGet(healthCheckUrl.getURI()));
                if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                    flag = false;
                }
            } catch (Exception ignored) {
            }
        }
    }

    private void prepareForMainAssert() throws IOException {
        report.setTimezone("Europe/Moscow");
        withdrawalRegistryTemplateService.processReportFileByTemplate(report, newOutputStream(reportFile));
    }

    private void serverSideInitLogic() {
        List<com.rbkmoney.fistful.reporter.domain.tables.pojos.Report> pendingReports = reportService.getPendingReports();
        assertEquals(pendingReports.size(), 1);
        reportGenerator.generateReportFile(pendingReports.get(0));
    }

    private void clientSideLogic(long reportId) throws TException, IOException {
        Report report = reportClient.getReport(partyId, contractId, reportId);
        assertEquals(report.getStatus(), ReportStatus.created);
        assertEquals(report.getFileDataIds().size(), 1);
        String downloadUrl = fileStorageClient.generateDownloadUrl(
                report.getFileDataIds().get(0),
                generateCurrentTimePlusDay().toString()
        );

        if (downloadUrl.contains("ceph-test:80")) {
            downloadUrl = downloadUrl.replaceAll("ceph-test:80", "localhost:42827");
        }
        HttpResponse responseGet = httpClient.execute(new HttpGet(downloadUrl));
        InputStream content = responseGet.getEntity().getContent();
        assertEquals(getContent(newInputStream(reportFile)).substring(0, 5), getContent(content).substring(0, 5));
    }

    private void downConteiners() throws IOException, InterruptedException {
        dockerCompose.down();
    }

    @After
    public void tearDown() throws Exception {
        deleteIfExists(reportFile);
    }

    @Override
    protected int getExpectedSize() {
        return 5;
    }

    private Instant generateCurrentTimePlusDay() {
        return now().plusDays(1).toInstant(getZoneOffset());
    }

    private ZoneOffset getZoneOffset() {
        return systemDefault().getRules().getOffset(now());
    }

    private String getContent(InputStream content) throws IOException {
        return IOUtils.toString(content, StandardCharsets.UTF_8);
    }
}

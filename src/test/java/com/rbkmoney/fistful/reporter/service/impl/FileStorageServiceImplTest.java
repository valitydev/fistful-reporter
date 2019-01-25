package com.rbkmoney.fistful.reporter.service.impl;

import com.palantir.docker.compose.configuration.DockerComposeFiles;
import com.palantir.docker.compose.configuration.ProjectName;
import com.palantir.docker.compose.connection.DockerMachine;
import com.palantir.docker.compose.connection.State;
import com.palantir.docker.compose.execution.DefaultDockerCompose;
import com.palantir.docker.compose.execution.DockerCompose;
import com.rbkmoney.file.storage.FileStorageSrv;
import com.rbkmoney.fistful.reporter.AbstractIntegrationTest;
import com.rbkmoney.fistful.reporter.service.FileStorageService;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.thrift.TException;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.testcontainers.shaded.org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.ZoneOffset;

import static java.nio.file.Files.newInputStream;
import static java.time.LocalDateTime.now;
import static java.time.ZoneOffset.systemDefault;
import static java.util.Objects.requireNonNull;
import static org.junit.Assert.assertEquals;

@Slf4j
public class FileStorageServiceImplTest extends AbstractIntegrationTest {

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private HttpClient httpClient;

    @Autowired
    private FileStorageSrv.Iface client;

    @Value("${fileStorage.healthCheckUrl}")
    private Resource healthCheckUrl;

    // comment ignore at local dev
    @Ignore
    @Test
    public void test() throws URISyntaxException, IOException, TException, InterruptedException {
        DockerCompose dockerCompose = new DefaultDockerCompose(
                DockerComposeFiles.from("src/test/resources/docker-compose.yml"),
                DockerMachine.localMachine().build(),
                ProjectName.random()
        );
        dockerCompose.up();

        assertEquals(dockerCompose.state("file-storage-test").Up, State.Up);

        waitingUpFileStorageContainer();

        Path file = getFileFromResources();
        String fileDataId = fileStorageService.saveFile(file);
        String downloadUrl = client.generateDownloadUrl(fileDataId, generateCurrentTimePlusDay().toString());

        if (downloadUrl.contains("ceph-test:80")) {
            downloadUrl = downloadUrl.replaceAll("ceph-test:80", "localhost:42827");
        }
        HttpResponse responseGet = httpClient.execute(new HttpGet(downloadUrl));
        InputStream content = responseGet.getEntity().getContent();
        assertEquals(getContent(newInputStream(file)), getContent(content));

        dockerCompose.down();
    }

    private Path getFileFromResources() throws URISyntaxException {
        ClassLoader classLoader = this.getClass().getClassLoader();

        URL url = requireNonNull(classLoader.getResource("respect"));
        return Paths.get(url.toURI());
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
}

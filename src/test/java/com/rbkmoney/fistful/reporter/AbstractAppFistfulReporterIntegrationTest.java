package com.rbkmoney.fistful.reporter;

import com.rbkmoney.TestContainers;
import com.rbkmoney.TestContainersBuilder;
import com.rbkmoney.fistful.reporter.utils.AbstractWithdrawalTestUtils;
import com.rbkmoney.fistful.reporter.utils.FistfulReporterTestPropertyValuesBuilder;
import com.rbkmoney.woody.thrift.impl.http.THSpawnClientBuilder;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;

import static java.nio.file.Files.createTempFile;
import static java.nio.file.Files.deleteIfExists;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
@ContextConfiguration(classes = FistfulReporterApplication.class, initializers = AbstractAppFistfulReporterIntegrationTest.Initializer.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public abstract class AbstractAppFistfulReporterIntegrationTest extends AbstractWithdrawalTestUtils {

    private static final int TIMEOUT = 555000;

    private static TestContainers testContainers = TestContainersBuilder.builder(false)
            .addPostgreSQLTestContainer()
            .addCephTestContainer()
            .addFileStorageTestContainer()
            .build();

    protected static Path reportFile;

    @BeforeClass
    public static void beforeClass() throws IOException {
        testContainers.startTestContainers();
        reportFile = createTempFile("test", ".xlsx");
    }

    @AfterClass
    public static void afterClass() throws IOException {
        deleteIfExists(reportFile);
        testContainers.stopTestContainers();
    }

    @TestConfiguration
    public static class TestContextConfiguration {

        @Value("${local.server.port}")
        protected int port;

        @Bean
        public ReportingSrv.Iface reportClient() throws URISyntaxException {
            return new THSpawnClientBuilder()
                    .withAddress(new URI("http://localhost:" + port + "/fistful/reports"))
                    .withNetworkTimeout(TIMEOUT)
                    .build(ReportingSrv.Iface.class);
        }
    }

    public static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

        @Override
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            FistfulReporterTestPropertyValuesBuilder.build(testContainers).applyTo(configurableApplicationContext);
        }
    }
}

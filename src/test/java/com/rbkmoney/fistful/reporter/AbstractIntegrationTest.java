package com.rbkmoney.fistful.reporter;

import com.rbkmoney.geck.common.util.TypeUtil;
import org.junit.ClassRule;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.PostgreSQLContainer;

import java.time.Duration;
import java.time.LocalDateTime;

import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
@TestPropertySource(
        properties = {
                "eventStock.pollingEnable=false",
                "reporting.pollingEnable=false",
                "fileStorage.url=http://localhost:42826/file_storage",
                "fileStorage.healthCheckUrl=http://localhost:42826/actuator/health"
        }
)
@ContextConfiguration(classes = FistfulReporterApplication.class, initializers = AbstractIntegrationTest.Initializer.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public abstract class AbstractIntegrationTest {

    protected LocalDateTime fromTime = LocalDateTime.now().minusHours(3);
    protected LocalDateTime toTime = LocalDateTime.now().minusHours(1);
    protected LocalDateTime inFromToPeriodTime = LocalDateTime.now().minusHours(2);

    @Value("${local.server.port}")
    protected int port;

    @Autowired
    protected JdbcTemplate jdbcTemplate;

    @ClassRule
    public static PostgreSQLContainer postgres = new PostgreSQLContainer<>("postgres:9.6")
            .withStartupTimeout(Duration.ofMinutes(5));

    public static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

        @Override
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            TestPropertyValues.of(
                    "spring.datasource.url=" + postgres.getJdbcUrl(),
                    "spring.datasource.username=" + postgres.getUsername(),
                    "spring.datasource.password=" + postgres.getPassword(),
                    "flyway.url=" + postgres.getJdbcUrl(),
                    "flyway.user=" + postgres.getUsername(),
                    "flyway.password=" + postgres.getPassword()
            )
                    .applyTo(configurableApplicationContext);
        }
    }

    protected static String generateDate() {
        return TypeUtil.temporalToString(LocalDateTime.now());
    }

    protected static Long generateLong() {
        return random(Long.class);
    }

    protected static Integer generateInt() {
        return random(Integer.class);
    }

    protected static String generateString() {
        return random(String.class);
    }
}

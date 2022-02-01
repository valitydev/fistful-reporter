package dev.vality.fistful.reporter.handler;

import dev.vality.fistful.reporter.config.PostgresqlSpringBootITest;
import dev.vality.fistful.reporter.dao.DestinationDao;
import dev.vality.fistful.reporter.dao.mapper.RecordRowMapper;
import dev.vality.fistful.reporter.domain.tables.pojos.Destination;
import dev.vality.fistful.reporter.handler.destination.DestinationStatusChangedHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import static dev.vality.fistful.reporter.domain.tables.Destination.DESTINATION;
import static dev.vality.fistful.reporter.util.handler.DestinationHandlerTestUtil.createMachineEvent;
import static dev.vality.fistful.reporter.util.handler.DestinationHandlerTestUtil.createStatusChanged;
import static dev.vality.testcontainers.annotations.util.RandomBeans.random;
import static org.junit.jupiter.api.Assertions.assertEquals;

@PostgresqlSpringBootITest
public class DestinationHandlerTest {

    @Autowired
    private DestinationStatusChangedHandler destinationStatusChangedHandler;

    @Autowired
    private DestinationDao destinationDao;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    Destination destination = random(Destination.class);
    String sqlStatement = "select * from fr.destination where id='" + destination.getId() + "';";

    @BeforeEach
    public void setUp() {
        destination.setCurrent(true);
        destinationDao.save(destination);
    }

    @Test
    public void destinationStatusChangedHandlerTest() {
        destinationStatusChangedHandler.handle(
                createStatusChanged(),
                createMachineEvent(destination.getDestinationId()));
        assertEquals(2L, destinationDao.get(destination.getDestinationId()).getEventId().longValue());
        assertEquals(
                false,
                jdbcTemplate.queryForObject(sqlStatement,
                        new RecordRowMapper<>(DESTINATION, Destination.class)).getCurrent()
        );
    }
}

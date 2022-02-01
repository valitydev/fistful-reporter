package dev.vality.fistful.reporter.handler.destination;

import dev.vality.fistful.reporter.config.PostgresqlSpringBootITest;
import dev.vality.fistful.reporter.dao.mapper.RecordRowMapper;
import dev.vality.fistful.reporter.domain.tables.pojos.Destination;
import dev.vality.fistful.reporter.util.handler.DestinationHandlerTestUtil;
import dev.vality.testcontainers.annotations.util.RandomBeans;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import static dev.vality.fistful.reporter.domain.tables.Destination.DESTINATION;
import static dev.vality.fistful.reporter.util.handler.DestinationHandlerTestUtil.createCreated;
import static dev.vality.fistful.reporter.util.handler.DestinationHandlerTestUtil.createCreatedMachineEvent;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@PostgresqlSpringBootITest
public class DestinationCreatedDigitalWalletHandlerTest {

    @Autowired
    private DestinationCreatedHandler destinationCreatedHandler;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    public void destinationCreatedHandlerTest() {
        Destination destination = RandomBeans.random(Destination.class);
        destination.setCurrent(true);
        String sqlStatement = "select * from fr.destination LIMIT 1;";
        dev.vality.fistful.base.Resource fistfulResource = new dev.vality.fistful.base.Resource();
        fistfulResource.setDigitalWallet(DestinationHandlerTestUtil.createResourceDigitalWallet());
        dev.vality.fistful.destination.Destination fistfulDestination
                = DestinationHandlerTestUtil.createFistfulDestination(fistfulResource);

        destinationCreatedHandler.handle(
                createCreated(fistfulDestination),
                createCreatedMachineEvent(destination.getDestinationId(), fistfulDestination));

        Destination destinationResult = jdbcTemplate.queryForObject(sqlStatement,
                new RecordRowMapper<>(DESTINATION, Destination.class));
        assertEquals(true, destinationResult.getCurrent());

        assertNotNull(destinationResult.getDigitalWalletId());
        assertNotNull(destinationResult.getDigitalWalletProvider());
    }
}

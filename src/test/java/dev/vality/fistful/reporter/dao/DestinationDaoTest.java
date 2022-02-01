package dev.vality.fistful.reporter.dao;

import dev.vality.fistful.reporter.config.PostgresqlSpringBootITest;
import dev.vality.fistful.reporter.domain.tables.pojos.Destination;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static dev.vality.testcontainers.annotations.util.RandomBeans.random;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@PostgresqlSpringBootITest
public class DestinationDaoTest {

    @Autowired
    private DestinationDao destinationDao;

    @Test
    public void destinationDaoTest() {
        Destination destination = random(Destination.class);
        destination.setCurrent(true);
        Long id = destinationDao.save(destination).get();
        destination.setId(id);
        assertEquals(destination, destinationDao.get(destination.getDestinationId()));
        destinationDao.updateNotCurrent(destination.getId());
        assertNull(destinationDao.get(destination.getDestinationId()));
    }
}

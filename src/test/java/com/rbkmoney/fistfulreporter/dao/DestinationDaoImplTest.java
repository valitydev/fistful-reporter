package com.rbkmoney.fistfulreporter.dao;

import com.rbkmoney.fistfulreporter.AbstractIntegrationTest;
import com.rbkmoney.fistfulreporter.domain.tables.pojos.Destination;
import com.rbkmoney.fistfulreporter.exception.DaoException;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class DestinationDaoImplTest extends AbstractIntegrationTest {

    @Autowired
    private DestinationDao destinationDao;

    @Test
    public void saveAndGetTest() throws DaoException {
        Destination destination = random(Destination.class);
        destination.setCurrent(true);
        Long id = destinationDao.save(destination);
        destination.setId(id);
        assertEquals(destination, destinationDao.get(destination.getDestinationId()));
        destinationDao.updateNotCurrent(destination.getDestinationId());
        assertNull(destinationDao.get(destination.getDestinationId()));
    }
}

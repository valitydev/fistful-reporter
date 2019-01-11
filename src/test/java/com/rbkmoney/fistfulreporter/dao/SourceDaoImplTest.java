package com.rbkmoney.fistfulreporter.dao;

import com.rbkmoney.fistfulreporter.AbstractIntegrationTest;
import com.rbkmoney.fistfulreporter.domain.tables.pojos.Source;
import com.rbkmoney.fistfulreporter.exception.DaoException;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class SourceDaoImplTest extends AbstractIntegrationTest {

    @Autowired
    private SourceDao sourceDao;

    @Test
    public void saveAndGetTest() throws DaoException {
        Source source = random(Source.class);
        source.setCurrent(true);
        Long id = sourceDao.save(source);
        source.setId(id);
        assertEquals(source, sourceDao.get(source.getSourceId()));
        sourceDao.updateNotCurrent(source.getSourceId());
        assertNull(sourceDao.get(source.getSourceId()));
    }
}

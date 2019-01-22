package com.rbkmoney.fistful.reporter.dao;

import com.rbkmoney.fistful.reporter.AbstractIntegrationTest;
import com.rbkmoney.fistful.reporter.domain.tables.pojos.Source;
import com.rbkmoney.fistful.reporter.exception.DaoException;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class SourceDaoTest extends AbstractIntegrationTest {

    @Autowired
    private SourceDao sourceDao;

    @Test
    public void test() throws DaoException {
        Source source = random(Source.class);
        source.setCurrent(true);
        Long id = sourceDao.save(source);
        source.setId(id);
        assertEquals(source, sourceDao.get(source.getSourceId()));
        sourceDao.updateNotCurrent(source.getSourceId());
        assertNull(sourceDao.get(source.getSourceId()));
    }
}

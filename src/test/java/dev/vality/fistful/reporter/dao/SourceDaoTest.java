package dev.vality.fistful.reporter.dao;

import dev.vality.fistful.reporter.config.PostgresqlSpringBootITest;
import dev.vality.fistful.reporter.domain.tables.pojos.Source;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static dev.vality.testcontainers.annotations.util.RandomBeans.random;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@PostgresqlSpringBootITest
public class SourceDaoTest {

    @Autowired
    private SourceDao sourceDao;

    @Test
    public void sourceDaoTest() {
        Source source = random(Source.class);
        source.setCurrent(true);
        Long id = sourceDao.save(source).get();
        source.setId(id);
        assertEquals(source, sourceDao.get(source.getSourceId()));
        sourceDao.updateNotCurrent(source.getId());
        assertNull(sourceDao.get(source.getSourceId()));
    }
}

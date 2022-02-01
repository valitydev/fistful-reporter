package dev.vality.fistful.reporter.dao;

import dev.vality.fistful.reporter.config.PostgresqlSpringBootITest;
import dev.vality.fistful.reporter.domain.tables.pojos.Identity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static dev.vality.testcontainers.annotations.util.RandomBeans.random;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@PostgresqlSpringBootITest
public class IdentityDaoTest {

    @Autowired
    private IdentityDao identityDao;

    @Test
    public void identityDaoTest() {
        Identity identity = random(Identity.class);
        identity.setCurrent(true);
        Long id = identityDao.save(identity).get();
        identity.setId(id);
        assertEquals(identity, identityDao.get(identity.getIdentityId()));
        identityDao.updateNotCurrent(identity.getId());
        assertNull(identityDao.get(identity.getIdentityId()));
    }
}

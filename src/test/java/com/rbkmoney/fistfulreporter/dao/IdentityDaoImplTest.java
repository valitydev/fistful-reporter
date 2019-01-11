package com.rbkmoney.fistfulreporter.dao;

import com.rbkmoney.fistfulreporter.AbstractIntegrationTest;
import com.rbkmoney.fistfulreporter.domain.tables.pojos.Identity;
import com.rbkmoney.fistfulreporter.exception.DaoException;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class IdentityDaoImplTest extends AbstractIntegrationTest {

    @Autowired
    private IdentityDao identityDao;

    @Test
    public void test() throws DaoException {
        Identity identity = random(Identity.class);
        identity.setCurrent(true);
        Long id = identityDao.save(identity);
        identity.setId(id);
        assertEquals(identity, identityDao.get(identity.getIdentityId()));
        identityDao.updateNotCurrent(identity.getIdentityId());
        assertNull(identityDao.get(identity.getIdentityId()));
    }
}

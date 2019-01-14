package com.rbkmoney.fistful.reporter.dao;

import com.rbkmoney.fistful.reporter.AbstractIntegrationTest;
import com.rbkmoney.fistful.reporter.domain.tables.pojos.Identity;
import com.rbkmoney.fistful.reporter.exception.DaoException;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static io.github.benas.randombeans.api.EnhancedRandom.random;
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
        Assert.assertEquals(identity, identityDao.get(identity.getIdentityId()));
        identityDao.updateNotCurrent(identity.getIdentityId());
        assertNull(identityDao.get(identity.getIdentityId()));
    }
}

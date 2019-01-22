package com.rbkmoney.fistful.reporter.dao;

import com.rbkmoney.fistful.reporter.AbstractIntegrationTest;
import com.rbkmoney.fistful.reporter.domain.tables.pojos.Wallet;
import com.rbkmoney.fistful.reporter.exception.DaoException;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static org.junit.Assert.assertNull;

public class WalletDaoTest extends AbstractIntegrationTest {

    @Autowired
    private WalletDao walletDao;

    @Test
    public void test() throws DaoException {
        Wallet wallet = random(Wallet.class);
        wallet.setCurrent(true);
        Long id = walletDao.save(wallet);
        wallet.setId(id);
        Assert.assertEquals(wallet, walletDao.get(wallet.getWalletId()));
        walletDao.updateNotCurrent(wallet.getWalletId());
        assertNull(walletDao.get(wallet.getWalletId()));
    }
}

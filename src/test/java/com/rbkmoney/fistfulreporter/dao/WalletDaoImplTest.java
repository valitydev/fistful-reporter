package com.rbkmoney.fistfulreporter.dao;

import com.rbkmoney.fistfulreporter.AbstractIntegrationTest;
import com.rbkmoney.fistfulreporter.domain.tables.pojos.Wallet;
import com.rbkmoney.fistfulreporter.exception.DaoException;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class WalletDaoImplTest extends AbstractIntegrationTest {

    @Autowired
    private WalletDao walletDao;

    @Test
    public void test() throws DaoException {
        Wallet wallet = random(Wallet.class);
        wallet.setCurrent(true);
        Long id = walletDao.save(wallet);
        wallet.setId(id);
        assertEquals(wallet, walletDao.get(wallet.getWalletId()));
        walletDao.updateNotCurrent(wallet.getWalletId());
        assertNull(walletDao.get(wallet.getWalletId()));
    }
}

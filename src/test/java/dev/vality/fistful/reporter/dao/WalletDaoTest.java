package dev.vality.fistful.reporter.dao;

import dev.vality.fistful.reporter.config.PostgresqlSpringBootITest;
import dev.vality.fistful.reporter.domain.tables.pojos.Wallet;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static dev.vality.testcontainers.annotations.util.RandomBeans.random;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@PostgresqlSpringBootITest
public class WalletDaoTest {

    @Autowired
    private WalletDao walletDao;

    @Test
    public void walletDaoTest() {
        Wallet wallet = random(Wallet.class);
        wallet.setCurrent(true);
        Long id = walletDao.save(wallet).get();
        wallet.setId(id);
        assertEquals(wallet, walletDao.get(wallet.getWalletId()));
        walletDao.updateNotCurrent(wallet.getId());
        assertNull(walletDao.get(wallet.getWalletId()));
    }
}

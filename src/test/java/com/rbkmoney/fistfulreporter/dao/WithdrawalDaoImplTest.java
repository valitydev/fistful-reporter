package com.rbkmoney.fistfulreporter.dao;

import com.rbkmoney.fistfulreporter.AbstractIntegrationTest;
import com.rbkmoney.fistfulreporter.domain.tables.pojos.Withdrawal;
import com.rbkmoney.fistfulreporter.exception.DaoException;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class WithdrawalDaoImplTest extends AbstractIntegrationTest {

    @Autowired
    private WithdrawalDao withdrawalDao;

    @Test
    public void test() throws DaoException {
        Withdrawal withdrawal = random(Withdrawal.class);
        withdrawal.setCurrent(true);
        Long id = withdrawalDao.save(withdrawal);
        withdrawal.setId(id);
        assertEquals(withdrawal, withdrawalDao.get(withdrawal.getWithdrawalId()));
        withdrawalDao.updateNotCurrent(withdrawal.getWithdrawalId());
        assertNull(withdrawalDao.get(withdrawal.getWithdrawalId()));
    }
}

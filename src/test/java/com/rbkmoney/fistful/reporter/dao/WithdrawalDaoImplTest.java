package com.rbkmoney.fistful.reporter.dao;

import com.rbkmoney.fistful.reporter.AbstractIntegrationTest;
import com.rbkmoney.fistful.reporter.domain.tables.pojos.Withdrawal;
import com.rbkmoney.fistful.reporter.exception.DaoException;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static io.github.benas.randombeans.api.EnhancedRandom.random;
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
        Assert.assertEquals(withdrawal, withdrawalDao.get(withdrawal.getWithdrawalId()));
        withdrawalDao.updateNotCurrent(withdrawal.getWithdrawalId());
        assertNull(withdrawalDao.get(withdrawal.getWithdrawalId()));
    }
}

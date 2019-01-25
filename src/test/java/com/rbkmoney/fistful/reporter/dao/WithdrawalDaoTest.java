package com.rbkmoney.fistful.reporter.dao;

import com.rbkmoney.fistful.reporter.AbstractWithdrawalTest;
import com.rbkmoney.fistful.reporter.domain.tables.pojos.Withdrawal;
import com.rbkmoney.fistful.reporter.exception.DaoException;
import org.junit.Test;

import java.util.List;

import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class WithdrawalDaoTest extends AbstractWithdrawalTest {

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

    @Test
    public void takeSucceededWithdrawalsTest() throws DaoException {
        List<Withdrawal> withdrawalsByReport = withdrawalDao.getSucceededWithdrawalsByReport(report, 0L, 1000);
        assertEquals(getExpectedSize(), withdrawalsByReport.size());
    }

    @Override
    protected int getExpectedSize() {
        return 20;
    }
}

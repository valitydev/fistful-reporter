package com.rbkmoney.fistful.reporter.dao;

import com.rbkmoney.fistful.reporter.AbstractIntegrationTest;
import com.rbkmoney.fistful.reporter.domain.tables.pojos.Deposit;
import com.rbkmoney.fistful.reporter.exception.DaoException;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class DepositDaoTest extends AbstractIntegrationTest {

    @Autowired
    private DepositDao depositDao;

    @Test
    public void test() throws DaoException {
        Deposit deposit = random(Deposit.class);
        deposit.setCurrent(true);
        Long id = depositDao.save(deposit);
        deposit.setId(id);
        assertEquals(deposit, depositDao.get(deposit.getDepositId()));
        depositDao.updateNotCurrent(deposit.getDepositId());
        assertNull(depositDao.get(deposit.getDepositId()));
    }
}

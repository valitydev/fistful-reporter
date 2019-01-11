package com.rbkmoney.fistfulreporter.dao;

import com.rbkmoney.fistfulreporter.AbstractIntegrationTest;
import com.rbkmoney.fistfulreporter.domain.tables.pojos.Deposit;
import com.rbkmoney.fistfulreporter.exception.DaoException;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class DepositImplTest extends AbstractIntegrationTest {

    @Autowired
    private DepositDao depositDao;

    @Test
    public void saveAndGetTest() throws DaoException {
        Deposit deposit = random(Deposit.class);
        deposit.setCurrent(true);
        Long id = depositDao.save(deposit);
        deposit.setId(id);
        assertEquals(deposit, depositDao.get(deposit.getDepositId()));
        depositDao.updateNotCurrent(deposit.getDepositId());
        assertNull(depositDao.get(deposit.getDepositId()));
    }
}

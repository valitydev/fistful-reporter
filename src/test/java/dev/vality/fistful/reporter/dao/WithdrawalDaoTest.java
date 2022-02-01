package dev.vality.fistful.reporter.dao;

import dev.vality.fistful.reporter.config.PostgresqlSpringBootITest;
import dev.vality.fistful.reporter.config.testconfiguration.WithdrawalTestDao;
import dev.vality.fistful.reporter.domain.tables.pojos.Withdrawal;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static dev.vality.fistful.reporter.data.TestData.createReport;
import static dev.vality.testcontainers.annotations.util.RandomBeans.random;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@PostgresqlSpringBootITest
public class WithdrawalDaoTest {

    @Autowired
    private WithdrawalDao withdrawalDao;

    @Autowired
    private WithdrawalTestDao withdrawalTestDao;

    @Test
    public void withdrawalDaoTest() {
        Withdrawal withdrawal = random(Withdrawal.class);
        withdrawal.setCurrent(true);
        Long id = withdrawalDao.save(withdrawal).get();
        withdrawal.setId(id);
        assertEquals(withdrawal, withdrawalDao.get(withdrawal.getWithdrawalId()));
        withdrawalDao.updateNotCurrent(withdrawal.getId());
        assertNull(withdrawalDao.get(withdrawal.getWithdrawalId()));
    }

    @Test
    public void takeSucceededWithdrawalsTest() {
        int expectedSize = 20;
        withdrawalTestDao.saveWithdrawalsDependencies(expectedSize);
        List<Withdrawal> withdrawalsByReport = withdrawalDao.getSucceededWithdrawals(createReport(), 0L, 1000);
        assertEquals(expectedSize, withdrawalsByReport.size());
    }
}

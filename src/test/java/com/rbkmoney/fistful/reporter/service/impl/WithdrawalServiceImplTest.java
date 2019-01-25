package com.rbkmoney.fistful.reporter.service.impl;

import com.rbkmoney.fistful.reporter.AbstractWithdrawalTest;
import com.rbkmoney.fistful.reporter.domain.tables.pojos.Withdrawal;
import com.rbkmoney.fistful.reporter.service.WithdrawalService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class WithdrawalServiceImplTest extends AbstractWithdrawalTest {

    @Autowired
    private WithdrawalService withdrawalService;

    @Test
    public void test() {
        List<Withdrawal> withdrawals = withdrawalService.getSucceededWithdrawalsByReport(report);

        assertEquals(getExpectedSize(), withdrawals.size());
    }

    @Override
    protected int getExpectedSize() {
        return 2002;
    }
}

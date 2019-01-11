package com.rbkmoney.fistfulreporter.service.impl;

import com.rbkmoney.fistful.base.Cash;
import com.rbkmoney.fistful.cashflow.*;
import com.rbkmoney.fistful.withdrawal.*;
import com.rbkmoney.fistfulreporter.AbstractIntegrationTest;
import com.rbkmoney.fistfulreporter.domain.tables.pojos.Withdrawal;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

import java.util.Arrays;
import java.util.List;

import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static org.junit.Assert.assertEquals;

public class WithdrawalEventServiceTest extends AbstractIntegrationTest {

    @Autowired
    private WithdrawalEventService eventService;

    @Test
    public void test() {
        List<Change> changes = Arrays.asList(
                createCreatedChange(),
                createStatusChangedChange(),
                createTransferCreatedChange(),
                createTransferStatusChangedChange(),
                createRouteChangedChange()
        );

        String withdrawalId = generateString();

        Event event = new Event(generateInt(), generateDate(), changes);

        SinkEvent sinkEvent = new SinkEvent(
                generateLong(),
                generateDate(),
                withdrawalId,
                event
        );

        eventService.processSinkEvent(sinkEvent);

        List<Withdrawal> withdrawals = jdbcTemplate.query(
                "SELECT * FROM fr.withdrawal AS withdrawal WHERE withdrawal.withdrawal_id = ?",
                new Object[]{withdrawalId},
                new BeanPropertyRowMapper<>(Withdrawal.class)
        );
        assertEquals(5, withdrawals.size());

        withdrawals = jdbcTemplate.query(
                "SELECT * FROM fr.withdrawal AS withdrawal WHERE withdrawal.withdrawal_id = ? AND withdrawal.current",
                new Object[]{withdrawalId},
                new BeanPropertyRowMapper<>(Withdrawal.class)
        );
        assertEquals(1, withdrawals.size());
    }

    private Change createCreatedChange() {
        return Change.created(random(com.rbkmoney.fistful.withdrawal.Withdrawal.class));
    }

    private Change createStatusChangedChange() {
        return Change.status_changed(WithdrawalStatus.failed(new WithdrawalFailed()));
    }

    private Change createTransferCreatedChange() {
        return Change.transfer(
                TransferChange.created(
                        new Transfer(
                                new FinalCashFlow(
                                        Arrays.asList(
                                                new FinalCashFlowPosting(
                                                        new FinalCashFlowAccount(
                                                                CashFlowAccount.merchant(MerchantCashFlowAccount.payout),
                                                                generateString()
                                                        ),
                                                        new FinalCashFlowAccount(
                                                                CashFlowAccount.provider(ProviderCashFlowAccount.settlement),
                                                                generateString()
                                                        ),
                                                        random(Cash.class)
                                                )
                                        )
                                )
                        )
                )
        );
    }

    private Change createTransferStatusChangedChange() {
        return Change.transfer(
                TransferChange.status_changed(
                        TransferStatus.cancelled(new TransferCancelled())
                )
        );
    }

    private Change createRouteChangedChange() {
        return Change.route(new RouteChange(generateString()));
    }
}
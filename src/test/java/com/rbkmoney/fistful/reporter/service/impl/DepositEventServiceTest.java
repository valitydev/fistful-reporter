package com.rbkmoney.fistful.reporter.service.impl;

import com.rbkmoney.fistful.base.Cash;
import com.rbkmoney.fistful.cashflow.*;
import com.rbkmoney.fistful.deposit.*;
import com.rbkmoney.fistful.reporter.AbstractIntegrationTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

import java.util.List;

import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;

public class DepositEventServiceTest extends AbstractIntegrationTest {

    @Autowired
    private DepositEventService eventService;

    @Test
    public void test() {
        List<Change> changes = asList(
                createCreatedChange(),
                createStatusChangedChange(),
                createTransferCreatedChange(),
                createTransferStatusChangedChange()
        );

        String depositId = generateString();

        Event event = new Event(generateInt(), generateDate(), changes);

        SinkEvent sinkEvent = new SinkEvent(
                generateLong(),
                generateDate(),
                depositId,
                event
        );

        eventService.processSinkEvent(sinkEvent);

        List<com.rbkmoney.fistful.reporter.domain.tables.pojos.Deposit> deposits = jdbcTemplate.query(
                "SELECT * FROM fr.deposit AS deposit WHERE deposit.deposit_id = ?",
                new Object[]{depositId},
                new BeanPropertyRowMapper<>(com.rbkmoney.fistful.reporter.domain.tables.pojos.Deposit.class)
        );
        assertEquals(4, deposits.size());

        deposits = jdbcTemplate.query(
                "SELECT * FROM fr.deposit AS deposit WHERE deposit.deposit_id = ? AND deposit.current",
                new Object[]{depositId},
                new BeanPropertyRowMapper<>(com.rbkmoney.fistful.reporter.domain.tables.pojos.Deposit.class)
        );
        assertEquals(1, deposits.size());
    }

    private Change createCreatedChange() {
        return Change.created(random(com.rbkmoney.fistful.deposit.Deposit.class));
    }

    private Change createStatusChangedChange() {
        return Change.status_changed(DepositStatus.succeeded(new DepositSucceeded()));
    }

    private Change createTransferStatusChangedChange() {
        return Change.transfer(TransferChange.status_changed(TransferStatus.committed(new TransferCommitted())));
    }

    private Change createTransferCreatedChange() {
        return Change.transfer(
                TransferChange.created(
                        new Transfer(
                                new FinalCashFlow(
                                        singletonList(
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
}

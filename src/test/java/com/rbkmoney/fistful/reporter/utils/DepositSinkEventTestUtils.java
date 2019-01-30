package com.rbkmoney.fistful.reporter.utils;

import com.rbkmoney.AbstractTestUtils;
import com.rbkmoney.fistful.base.Cash;
import com.rbkmoney.fistful.cashflow.*;
import com.rbkmoney.fistful.deposit.*;

import java.util.List;

import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;

public class DepositSinkEventTestUtils extends AbstractTestUtils {

    public static SinkEvent create(String depositId) {
        List<Change> changes = asList(
                createCreatedChange(),
                createStatusChangedChange(),
                createTransferCreatedChange(),
                createTransferStatusChangedChange()
        );
        Event event = new Event(generateInt(), generateDate(), changes);

        return new SinkEvent(
                generateLong(),
                generateDate(),
                depositId,
                event
        );
    }

    private static Change createCreatedChange() {
        return Change.created(random(com.rbkmoney.fistful.deposit.Deposit.class));
    }

    private static Change createStatusChangedChange() {
        return Change.status_changed(DepositStatus.succeeded(new DepositSucceeded()));
    }

    private static Change createTransferStatusChangedChange() {
        return Change.transfer(TransferChange.status_changed(TransferStatus.committed(new TransferCommitted())));
    }

    private static Change createTransferCreatedChange() {
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

package com.rbkmoney.fistful.reporter.utils;

import com.rbkmoney.AbstractTestUtils;
import com.rbkmoney.fistful.base.Cash;
import com.rbkmoney.fistful.cashflow.*;
import com.rbkmoney.fistful.withdrawal.*;

import java.util.List;

import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static java.util.Arrays.asList;

public class WithdrawalSinkEventTestUtils extends AbstractTestUtils {

    public static SinkEvent create(String withdrawalId) {
        List<Change> changes = asList(
                createCreatedChange(),
                createStatusChangedChange(),
                createTransferCreatedChange(),
                createTransferStatusChangedChange(),
                createRouteChangedChange()
        );

        Event event = new Event(generateInt(), generateDate(), changes);

        return new SinkEvent(
                generateLong(),
                generateDate(),
                withdrawalId,
                event
        );
    }

    private static Change createCreatedChange() {
        return Change.created(random(com.rbkmoney.fistful.withdrawal.Withdrawal.class));
    }

    private static Change createStatusChangedChange() {
        return Change.status_changed(WithdrawalStatus.failed(new WithdrawalFailed()));
    }

    private static Change createTransferCreatedChange() {
        return Change.transfer(
                TransferChange.created(
                        new Transfer(
                                new FinalCashFlow(
                                        asList(
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

    private static Change createTransferStatusChangedChange() {
        return Change.transfer(
                TransferChange.status_changed(
                        TransferStatus.cancelled(new TransferCancelled())
                )
        );
    }

    private static Change createRouteChangedChange() {
        return Change.route(new RouteChange(generateString()));
    }
}

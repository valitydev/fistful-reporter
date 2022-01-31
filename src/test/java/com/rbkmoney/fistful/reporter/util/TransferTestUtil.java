package com.rbkmoney.fistful.reporter.util;

import dev.vality.fistful.base.Cash;
import dev.vality.fistful.cashflow.*;
import dev.vality.fistful.transfer.Change;
import dev.vality.fistful.transfer.Committed;
import dev.vality.fistful.transfer.CreatedChange;
import dev.vality.fistful.transfer.Transfer;

import static com.rbkmoney.testcontainers.annotations.util.ValuesGenerator.generateString;
import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static java.util.Collections.singletonList;

public class TransferTestUtil {

    public static dev.vality.fistful.transfer.Change getCommitedPayload() {
        return new dev.vality.fistful.transfer.Change(
                dev.vality.fistful.transfer.Change.status_changed(
                        new dev.vality.fistful.transfer.StatusChange(
                                dev.vality.fistful.transfer.Status.committed(new Committed())
                        )
                )
        );
    }

    public static Change getCashFlowPayload() {
        return new Change(
                Change.created(
                        new CreatedChange(
                                new Transfer()
                                        .setCashflow(getFinalCashFlow())
                        )
                )
        );
    }

    private static FinalCashFlow getFinalCashFlow() {
        return new FinalCashFlow(
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
        );
    }
}

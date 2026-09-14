package dev.vality.fistful.reporter.util.handler;

import dev.vality.fistful.base.Cash;
import dev.vality.fistful.base.CurrencyRef;
import dev.vality.fistful.cashflow.CashFlowAccount;
import dev.vality.fistful.cashflow.FinalCashFlow;
import dev.vality.fistful.cashflow.FinalCashFlowAccount;
import dev.vality.fistful.cashflow.FinalCashFlowPosting;
import dev.vality.fistful.cashflow.SystemCashFlowAccount;
import dev.vality.fistful.cashflow.WalletCashFlowAccount;
import dev.vality.fistful.withdrawal.AdjustmentChange;
import dev.vality.fistful.withdrawal.BodyChange;
import dev.vality.fistful.withdrawal.Change;
import dev.vality.fistful.withdrawal.StatusChange;
import dev.vality.fistful.withdrawal.TimestampedChange;
import dev.vality.fistful.withdrawal.TransferChange;
import dev.vality.fistful.withdrawal.adjustment.Adjustment;
import dev.vality.fistful.withdrawal.adjustment.BodyChangePlan;
import dev.vality.fistful.withdrawal.adjustment.CashFlowChangePlan;
import dev.vality.fistful.withdrawal.adjustment.ChangesPlan;
import dev.vality.fistful.withdrawal.adjustment.CreatedChange;
import dev.vality.fistful.withdrawal.status.Status;
import dev.vality.fistful.withdrawal.status.Succeeded;
import dev.vality.kafka.common.serialization.ThriftSerializer;
import dev.vality.machinegun.eventsink.MachineEvent;
import dev.vality.machinegun.msgpack.Value;

import static dev.vality.fistful.reporter.util.TransferTestUtil.getCashFlowPayload;
import static dev.vality.fistful.reporter.util.TransferTestUtil.getCommitedPayload;
import static java.util.Collections.singletonList;

public class WithdrawalHandlerTestUtil {

    public static MachineEvent createMachineEvent(String id) {
        return createMachineEvent(id, 2L);
    }

    public static MachineEvent createMachineEvent(String id, long eventId) {
        return new MachineEvent()
                .setEventId(eventId)
                .setSourceId(id)
                .setSourceNs("2")
                .setCreatedAt("2021-05-31T06:12:27Z")
                .setData(Value.bin(new ThriftSerializer<>().serialize("", createStatusChanged())));
    }

    public static TimestampedChange createStatusChanged() {
        return new TimestampedChange()
                .setOccuredAt("2021-05-31T06:12:27Z")
                .setChange(Change.status_changed(
                        new StatusChange().setStatus(
                                Status.succeeded(new Succeeded()))));
    }

    public static TimestampedChange createTransferChanged() {
        return new TimestampedChange()
                .setOccuredAt("2021-05-31T06:12:27Z")
                .setChange(Change.transfer(new TransferChange(getCommitedPayload())));
    }

    public static TimestampedChange createTransferCreated() {
        return new TimestampedChange()
                .setOccuredAt("2021-05-31T06:12:27Z")
                .setChange(Change.transfer(new TransferChange(getCashFlowPayload())));
    }

    public static TimestampedChange createBodyChanged() {
        Cash oldBody = new Cash(100L, new CurrencyRef("RUB"));
        Cash newBody = new Cash(75L, new CurrencyRef("USD"));
        return new TimestampedChange()
                .setOccuredAt("2021-05-31T06:12:27Z")
                .setChange(Change.body_changed(new BodyChange(oldBody, newBody)));
    }

    public static TimestampedChange createAdjustmentCreated() {
        Cash body = new Cash(20000L, new CurrencyRef("RUB"));
        FinalCashFlow cashFlow = new FinalCashFlow(singletonList(
                new FinalCashFlowPosting(
                        new FinalCashFlowAccount(CashFlowAccount.wallet(WalletCashFlowAccount.sender_settlement)),
                        new FinalCashFlowAccount(CashFlowAccount.system(SystemCashFlowAccount.settlement)),
                        new Cash(2000L, new CurrencyRef("RUB")))));
        ChangesPlan plan = new ChangesPlan()
                .setNewCashFlow(new CashFlowChangePlan().setNewCashFlow(cashFlow))
                .setNewBody(new BodyChangePlan(body));
        Adjustment adjustment = new Adjustment().setChangesPlan(plan);
        return new TimestampedChange()
                .setOccuredAt("2021-05-31T06:12:27Z")
                .setChange(Change.adjustment(new AdjustmentChange()
                        .setId("adjustment-1")
                        .setPayload(dev.vality.fistful.withdrawal.adjustment.Change.created(
                                new CreatedChange(adjustment)))));
    }

    public static TimestampedChange createAdjustmentSucceeded() {
        return new TimestampedChange()
                .setOccuredAt("2021-05-31T06:12:28Z")
                .setChange(Change.adjustment(new AdjustmentChange()
                        .setId("adjustment-1")
                        .setPayload(dev.vality.fistful.withdrawal.adjustment.Change.status_changed(
                                new dev.vality.fistful.withdrawal.adjustment.StatusChange(
                                        dev.vality.fistful.withdrawal.adjustment.Status.succeeded(
                                                new dev.vality.fistful.withdrawal.adjustment.Succeeded()))))));
    }

    public static TimestampedChange createAdjustmentBodyChanged() {
        Cash oldBody = new Cash(10000L, new CurrencyRef("RUB"));
        Cash newBody = new Cash(20000L, new CurrencyRef("RUB"));
        return new TimestampedChange()
                .setOccuredAt("2021-05-31T06:12:29Z")
                .setChange(Change.body_changed(new BodyChange(oldBody, newBody)));
    }
}

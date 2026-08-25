package dev.vality.fistful.reporter.util.handler;

import dev.vality.fistful.base.Cash;
import dev.vality.fistful.base.CurrencyRef;
import dev.vality.fistful.withdrawal.BodyChange;
import dev.vality.fistful.withdrawal.Change;
import dev.vality.fistful.withdrawal.StatusChange;
import dev.vality.fistful.withdrawal.TimestampedChange;
import dev.vality.fistful.withdrawal.TransferChange;
import dev.vality.fistful.withdrawal.status.Status;
import dev.vality.fistful.withdrawal.status.Succeeded;
import dev.vality.kafka.common.serialization.ThriftSerializer;
import dev.vality.machinegun.eventsink.MachineEvent;
import dev.vality.machinegun.msgpack.Value;

import static dev.vality.fistful.reporter.util.TransferTestUtil.getCashFlowPayload;
import static dev.vality.fistful.reporter.util.TransferTestUtil.getCommitedPayload;

public class WithdrawalHandlerTestUtil {

    public static MachineEvent createMachineEvent(String id) {
        return new MachineEvent()
                .setEventId(2L)
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
}

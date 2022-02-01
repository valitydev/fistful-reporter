package dev.vality.fistful.reporter.util.handler;

import dev.vality.fistful.account.Account;
import dev.vality.fistful.wallet.AccountChange;
import dev.vality.fistful.wallet.Change;
import dev.vality.fistful.wallet.TimestampedChange;
import dev.vality.kafka.common.serialization.ThriftSerializer;
import dev.vality.machinegun.eventsink.MachineEvent;
import dev.vality.machinegun.msgpack.Value;

public class WalletHandlerTestUtil {

    public static MachineEvent createMachineEvent(String id, Account account) {
        return new MachineEvent()
                .setEventId(2L)
                .setSourceId(id)
                .setSourceNs("2")
                .setCreatedAt("2021-05-31T06:12:27Z")
                .setData(Value.bin(new ThriftSerializer<>().serialize("", createAccountCreated(account))));
    }

    public static TimestampedChange createAccountCreated(Account account) {
        return new TimestampedChange()
                .setOccuredAt("2021-05-31T06:12:27Z")
                .setChange(Change.account(AccountChange.created(account)));
    }
}

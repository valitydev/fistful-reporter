package com.rbkmoney.fistful.reporter.util.handler;

import dev.vality.fistful.account.Account;
import dev.vality.fistful.wallet.AccountChange;
import dev.vality.fistful.wallet.Change;
import dev.vality.fistful.wallet.TimestampedChange;
import com.rbkmoney.kafka.common.serialization.ThriftSerializer;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.machinegun.msgpack.Value;

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

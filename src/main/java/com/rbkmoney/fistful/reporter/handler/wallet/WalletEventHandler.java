package com.rbkmoney.fistful.reporter.handler.wallet;

import com.rbkmoney.fistful.reporter.handler.EventHandler;
import com.rbkmoney.fistful.wallet.TimestampedChange;
import com.rbkmoney.machinegun.eventsink.MachineEvent;

public interface WalletEventHandler extends EventHandler<TimestampedChange, MachineEvent> {
}

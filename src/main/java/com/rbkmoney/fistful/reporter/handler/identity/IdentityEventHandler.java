package com.rbkmoney.fistful.reporter.handler.identity;

import dev.vality.fistful.identity.TimestampedChange;
import com.rbkmoney.fistful.reporter.handler.EventHandler;
import com.rbkmoney.machinegun.eventsink.MachineEvent;

public interface IdentityEventHandler extends EventHandler<TimestampedChange, MachineEvent> {
}

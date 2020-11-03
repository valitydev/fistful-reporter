package com.rbkmoney.fistful.reporter.handler.destination;

import com.rbkmoney.fistful.destination.TimestampedChange;
import com.rbkmoney.fistful.reporter.handler.EventHandler;
import com.rbkmoney.machinegun.eventsink.MachineEvent;

public interface DestinationEventHandler extends EventHandler<TimestampedChange, MachineEvent> {
}

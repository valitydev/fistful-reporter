package com.rbkmoney.fistful.reporter.handler.source;

import com.rbkmoney.fistful.reporter.handler.EventHandler;
import dev.vality.fistful.source.TimestampedChange;
import com.rbkmoney.machinegun.eventsink.MachineEvent;

public interface SourceEventHandler extends EventHandler<TimestampedChange, MachineEvent> {
}

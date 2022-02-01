package dev.vality.fistful.reporter.handler.source;

import dev.vality.fistful.reporter.handler.EventHandler;
import dev.vality.fistful.source.TimestampedChange;
import dev.vality.machinegun.eventsink.MachineEvent;

public interface SourceEventHandler extends EventHandler<TimestampedChange, MachineEvent> {
}

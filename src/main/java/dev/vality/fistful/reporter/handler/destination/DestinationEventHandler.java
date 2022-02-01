package dev.vality.fistful.reporter.handler.destination;

import dev.vality.fistful.destination.TimestampedChange;
import dev.vality.fistful.reporter.handler.EventHandler;
import dev.vality.machinegun.eventsink.MachineEvent;

public interface DestinationEventHandler extends EventHandler<TimestampedChange, MachineEvent> {
}

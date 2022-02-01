package dev.vality.fistful.reporter.handler.identity;

import dev.vality.fistful.identity.TimestampedChange;
import dev.vality.fistful.reporter.handler.EventHandler;
import dev.vality.machinegun.eventsink.MachineEvent;

public interface IdentityEventHandler extends EventHandler<TimestampedChange, MachineEvent> {
}

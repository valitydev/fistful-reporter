package dev.vality.fistful.reporter.handler.withdrawal;

import dev.vality.fistful.reporter.domain.enums.FistfulCashFlowChangeType;
import dev.vality.fistful.reporter.domain.enums.WithdrawalEventType;
import dev.vality.fistful.reporter.domain.tables.pojos.FistfulCashFlow;
import dev.vality.fistful.reporter.handler.EventHandler;
import dev.vality.fistful.withdrawal.TimestampedChange;
import dev.vality.geck.common.util.TypeUtil;
import dev.vality.machinegun.eventsink.MachineEvent;

import java.util.List;

public interface WithdrawalEventHandler extends EventHandler<TimestampedChange, MachineEvent> {

    default void fillCashFlows(
            List<FistfulCashFlow> cashFlows,
            MachineEvent event,
            WithdrawalEventType withdrawalEventType,
            long id,
            TimestampedChange change) {
        cashFlows.forEach(
                pcf -> {
                    pcf.setId(null);
                    pcf.setEventId(event.getEventId());
                    pcf.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
                    pcf.setSourceId(event.getSourceId());
                    pcf.setEventOccuredAt(TypeUtil.stringToLocalDateTime(change.getOccuredAt()));
                    pcf.setEventType(withdrawalEventType.toString());
                    pcf.setObjId(id);
                    pcf.setObjType(FistfulCashFlowChangeType.withdrawal);
                }
        );
    }
}

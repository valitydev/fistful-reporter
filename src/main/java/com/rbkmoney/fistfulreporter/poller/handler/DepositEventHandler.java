package com.rbkmoney.fistfulreporter.poller.handler;

import com.rbkmoney.fistful.deposit.Change;
import com.rbkmoney.fistful.deposit.SinkEvent;
import com.rbkmoney.fistfulreporter.domain.enums.DepositEventType;
import com.rbkmoney.fistfulreporter.domain.enums.FistfulCashFlowChangeType;
import com.rbkmoney.fistfulreporter.domain.tables.pojos.FistfulCashFlow;
import com.rbkmoney.geck.common.util.TypeUtil;

import java.util.List;

public interface DepositEventHandler extends EventHandler<Change, SinkEvent> {

    default void fillCashFlows(List<FistfulCashFlow> cashFlows, SinkEvent event, DepositEventType withdrawalEventType, long id) {
        cashFlows.forEach(
                pcf -> {
                    pcf.setId(null);

                    pcf.setEventId(event.getId());
                    pcf.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
                    pcf.setSourceId(event.getSource());
                    pcf.setSequenceId(event.getPayload().getSequence());
                    pcf.setEventOccuredAt(TypeUtil.stringToLocalDateTime(event.getPayload().getOccuredAt()));
                    pcf.setEventType(withdrawalEventType.toString());
                    pcf.setObjId(id);
                    pcf.setObjType(FistfulCashFlowChangeType.deposit);
                }
        );
    }
}

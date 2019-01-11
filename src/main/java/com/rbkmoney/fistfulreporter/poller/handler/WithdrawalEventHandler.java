package com.rbkmoney.fistfulreporter.poller.handler;

import com.rbkmoney.fistful.withdrawal.Change;
import com.rbkmoney.fistful.withdrawal.SinkEvent;
import com.rbkmoney.fistfulreporter.domain.enums.FistfulCashFlowChangeType;
import com.rbkmoney.fistfulreporter.domain.enums.WithdrawalEventType;
import com.rbkmoney.fistfulreporter.domain.tables.pojos.FistfulCashFlow;
import com.rbkmoney.geck.common.util.TypeUtil;

import java.util.List;

public interface WithdrawalEventHandler extends EventHandler<Change, SinkEvent> {

    default void fillCashFlows(List<FistfulCashFlow> cashFlows, SinkEvent event, WithdrawalEventType withdrawalEventType, long id) {
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
                    pcf.setObjType(FistfulCashFlowChangeType.withdrawal);
                }
        );
    }
}

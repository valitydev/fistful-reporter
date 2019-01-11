package com.rbkmoney.fistfulreporter.poller.handler.impl;

import com.rbkmoney.fistful.deposit.Change;
import com.rbkmoney.fistful.deposit.SinkEvent;
import com.rbkmoney.fistfulreporter.dao.DepositDao;
import com.rbkmoney.fistfulreporter.dao.FistfulCashFlowDao;
import com.rbkmoney.fistfulreporter.domain.enums.DepositEventType;
import com.rbkmoney.fistfulreporter.domain.enums.DepositStatus;
import com.rbkmoney.fistfulreporter.domain.enums.FistfulCashFlowChangeType;
import com.rbkmoney.fistfulreporter.domain.tables.pojos.Deposit;
import com.rbkmoney.fistfulreporter.domain.tables.pojos.FistfulCashFlow;
import com.rbkmoney.fistfulreporter.exception.DaoException;
import com.rbkmoney.fistfulreporter.exception.StorageException;
import com.rbkmoney.fistfulreporter.poller.handler.DepositEventHandler;
import com.rbkmoney.geck.common.util.TBaseUtil;
import com.rbkmoney.geck.common.util.TypeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class DepositStatusChangedHandler implements DepositEventHandler {

    private final DepositDao depositDao;
    private final FistfulCashFlowDao fistfulCashFlowDao;

    @Override
    public boolean accept(Change change) {
        return change.isSetStatusChanged();
    }

    @Override
    public void handle(Change change, SinkEvent event) {
        try {
            log.info("Start deposit status changed handling, eventId={}, depositId={}, status={}", event.getId(), event.getSource(), change.getStatusChanged());
            Deposit deposit = depositDao.get(event.getSource());

            long sourceId = deposit.getId();

            deposit.setId(null);
            deposit.setWtime(null);

            deposit.setEventId(event.getId());
            deposit.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
            deposit.setDepositId(event.getSource());
            deposit.setSequenceId(event.getPayload().getSequence());
            deposit.setEventOccuredAt(TypeUtil.stringToLocalDateTime(event.getPayload().getOccuredAt()));
            deposit.setEventType(DepositEventType.DEPOSIT_STATUS_CHANGED);
            deposit.setDepositStatus(TBaseUtil.unionFieldToEnum(change.getStatusChanged(), DepositStatus.class));

            depositDao.updateNotCurrent(event.getSource());
            long id = depositDao.save(deposit);

            List<FistfulCashFlow> cashFlows = fistfulCashFlowDao.getByObjId(sourceId, FistfulCashFlowChangeType.deposit);
            fillCashFlows(cashFlows, event, DepositEventType.DEPOSIT_STATUS_CHANGED, id);
            fistfulCashFlowDao.save(cashFlows);
            log.info("Deposit status have been changed, eventId={}, depositId={}, status={}", event.getId(), event.getSource(), change.getStatusChanged());
        } catch (DaoException e) {
            throw new StorageException(e);
        }
    }
}

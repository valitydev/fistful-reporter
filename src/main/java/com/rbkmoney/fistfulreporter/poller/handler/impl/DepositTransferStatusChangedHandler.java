package com.rbkmoney.fistfulreporter.poller.handler.impl;

import com.rbkmoney.fistful.deposit.Change;
import com.rbkmoney.fistful.deposit.SinkEvent;
import com.rbkmoney.fistfulreporter.dao.DepositDao;
import com.rbkmoney.fistfulreporter.dao.FistfulCashFlowDao;
import com.rbkmoney.fistfulreporter.domain.enums.DepositEventType;
import com.rbkmoney.fistfulreporter.domain.enums.DepositTransferStatus;
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
public class DepositTransferStatusChangedHandler implements DepositEventHandler {

    private final DepositDao depositDao;
    private final FistfulCashFlowDao fistfulCashFlowDao;

    @Override
    public boolean accept(Change change) {
        return change.isSetTransfer() && change.getTransfer().isSetStatusChanged();
    }

    @Override
    public void handle(Change change, SinkEvent event) {
        try {
            log.info("Start deposit transfer status changed handling, eventId={}, depositId={}, transferChange={}", event.getId(), event.getSource(), change.getTransfer());
            Deposit deposit = depositDao.get(event.getSource());

            long sourceId = deposit.getId();

            deposit.setId(null);
            deposit.setWtime(null);

            deposit.setEventId(event.getId());
            deposit.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
            deposit.setDepositId(event.getSource());
            deposit.setSequenceId(event.getPayload().getSequence());
            deposit.setEventOccuredAt(TypeUtil.stringToLocalDateTime(event.getPayload().getOccuredAt()));
            deposit.setEventType(DepositEventType.DEPOSIT_TRANSFER_STATUS_CHANGED);
            deposit.setDepositTransferStatus(TBaseUtil.unionFieldToEnum(change.getTransfer().getStatusChanged(), DepositTransferStatus.class));

            depositDao.updateNotCurrent(event.getSource());
            long id = depositDao.save(deposit);

            List<FistfulCashFlow> cashFlows = fistfulCashFlowDao.getByObjId(sourceId, FistfulCashFlowChangeType.deposit);
            fillCashFlows(cashFlows, event, DepositEventType.DEPOSIT_TRANSFER_STATUS_CHANGED, id);
            fistfulCashFlowDao.save(cashFlows);
            log.info("Withdrawal deposit status have been changed, eventId={}, depositId={}, transferChange={}", event.getId(), event.getSource(), change.getTransfer());
        } catch (DaoException e) {
            throw new StorageException(e);
        }
    }
}

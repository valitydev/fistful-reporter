package com.rbkmoney.fistful.reporter.poller.handler.impl;

import com.rbkmoney.fistful.deposit.Change;
import com.rbkmoney.fistful.deposit.SinkEvent;
import com.rbkmoney.fistful.reporter.dao.DepositDao;
import com.rbkmoney.fistful.reporter.dao.FistfulCashFlowDao;
import com.rbkmoney.fistful.reporter.domain.enums.DepositEventType;
import com.rbkmoney.fistful.reporter.domain.enums.DepositTransferStatus;
import com.rbkmoney.fistful.reporter.domain.enums.FistfulCashFlowChangeType;
import com.rbkmoney.fistful.reporter.domain.tables.pojos.Deposit;
import com.rbkmoney.fistful.reporter.domain.tables.pojos.FistfulCashFlow;
import com.rbkmoney.fistful.reporter.exception.DaoException;
import com.rbkmoney.fistful.reporter.exception.StorageException;
import com.rbkmoney.fistful.reporter.poller.handler.DepositEventHandler;
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

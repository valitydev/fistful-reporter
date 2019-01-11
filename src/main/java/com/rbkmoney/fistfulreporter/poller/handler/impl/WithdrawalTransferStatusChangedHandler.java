package com.rbkmoney.fistfulreporter.poller.handler.impl;

import com.rbkmoney.fistful.withdrawal.Change;
import com.rbkmoney.fistful.withdrawal.SinkEvent;
import com.rbkmoney.fistfulreporter.dao.FistfulCashFlowDao;
import com.rbkmoney.fistfulreporter.dao.WithdrawalDao;
import com.rbkmoney.fistfulreporter.domain.enums.FistfulCashFlowChangeType;
import com.rbkmoney.fistfulreporter.domain.enums.WithdrawalEventType;
import com.rbkmoney.fistfulreporter.domain.enums.WithdrawalTransferStatus;
import com.rbkmoney.fistfulreporter.domain.tables.pojos.FistfulCashFlow;
import com.rbkmoney.fistfulreporter.domain.tables.pojos.Withdrawal;
import com.rbkmoney.fistfulreporter.exception.DaoException;
import com.rbkmoney.fistfulreporter.exception.StorageException;
import com.rbkmoney.fistfulreporter.poller.handler.WithdrawalEventHandler;
import com.rbkmoney.geck.common.util.TBaseUtil;
import com.rbkmoney.geck.common.util.TypeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class WithdrawalTransferStatusChangedHandler implements WithdrawalEventHandler {

    private final WithdrawalDao withdrawalDao;
    private final FistfulCashFlowDao fistfulCashFlowDao;

    @Override
    public boolean accept(Change change) {
        return change.isSetTransfer() && change.getTransfer().isSetStatusChanged();
    }

    @Override
    public void handle(Change change, SinkEvent event) {
        try {
            log.info("Start withdrawal transfer status changed handling, eventId={}, walletId={}, transferChange={}", event.getId(), event.getSource(), change.getTransfer());
            Withdrawal withdrawal = withdrawalDao.get(event.getSource());

            long sourceId = withdrawal.getId();

            withdrawal.setId(null);
            withdrawal.setWtime(null);

            withdrawal.setEventId(event.getId());
            withdrawal.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
            withdrawal.setWithdrawalId(event.getSource());
            withdrawal.setSequenceId(event.getPayload().getSequence());
            withdrawal.setEventOccuredAt(TypeUtil.stringToLocalDateTime(event.getPayload().getOccuredAt()));
            withdrawal.setEventType(WithdrawalEventType.WITHDRAWAL_TRANSFER_STATUS_CHANGED);
            withdrawal.setWithdrawalTransferStatus(TBaseUtil.unionFieldToEnum(change.getTransfer().getStatusChanged(), WithdrawalTransferStatus.class));

            withdrawalDao.updateNotCurrent(event.getSource());
            long id = withdrawalDao.save(withdrawal);

            List<FistfulCashFlow> cashFlows = fistfulCashFlowDao.getByObjId(sourceId, FistfulCashFlowChangeType.withdrawal);
            fillCashFlows(cashFlows, event, WithdrawalEventType.WITHDRAWAL_TRANSFER_STATUS_CHANGED, id);
            fistfulCashFlowDao.save(cashFlows);
            log.info("Withdrawal transfer status have been changed, eventId={}, walletId={}, transferChange={}", event.getId(), event.getSource(), change.getTransfer());
        } catch (DaoException e) {
            throw new StorageException(e);
        }
    }
}

package com.rbkmoney.fistfulreporter.poller.handler.impl;

import com.rbkmoney.fistful.withdrawal.Change;
import com.rbkmoney.fistful.withdrawal.SinkEvent;
import com.rbkmoney.fistfulreporter.dao.FistfulCashFlowDao;
import com.rbkmoney.fistfulreporter.dao.WithdrawalDao;
import com.rbkmoney.fistfulreporter.domain.enums.FistfulCashFlowChangeType;
import com.rbkmoney.fistfulreporter.domain.enums.WithdrawalEventType;
import com.rbkmoney.fistfulreporter.domain.tables.pojos.FistfulCashFlow;
import com.rbkmoney.fistfulreporter.domain.tables.pojos.Withdrawal;
import com.rbkmoney.fistfulreporter.exception.DaoException;
import com.rbkmoney.fistfulreporter.exception.StorageException;
import com.rbkmoney.fistfulreporter.poller.handler.WithdrawalEventHandler;
import com.rbkmoney.geck.common.util.TypeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class WithdrawalRouteChangeHandler implements WithdrawalEventHandler {

    private final WithdrawalDao withdrawalDao;
    private final FistfulCashFlowDao fistfulCashFlowDao;

    @Override
    public boolean accept(Change change) {
        return change.isSetRoute();
    }

    @Override
    public void handle(Change change, SinkEvent event) {
        try {
            log.info("Start withdrawal provider id changed handling, eventId={}, walletId={}, providerId={}", event.getId(), event.getSource(), change.getRoute().getId());
            Withdrawal withdrawal = withdrawalDao.get(event.getSource());

            long sourceId = withdrawal.getId();

            withdrawal.setId(null);
            withdrawal.setWtime(null);

            withdrawal.setEventId(event.getId());
            withdrawal.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
            withdrawal.setWithdrawalId(event.getSource());
            withdrawal.setSequenceId(event.getPayload().getSequence());
            withdrawal.setEventOccuredAt(TypeUtil.stringToLocalDateTime(event.getPayload().getOccuredAt()));
            withdrawal.setEventType(WithdrawalEventType.WITHDRAWAL_ROUTE_CHANGED);
            withdrawal.setProviderId(change.getRoute().getId());

            withdrawalDao.updateNotCurrent(event.getSource());
            long id = withdrawalDao.save(withdrawal);

            List<FistfulCashFlow> cashFlows = fistfulCashFlowDao.getByObjId(sourceId, FistfulCashFlowChangeType.withdrawal);
            fillCashFlows(cashFlows, event, WithdrawalEventType.WITHDRAWAL_ROUTE_CHANGED, id);

            fistfulCashFlowDao.save(cashFlows);
            log.info("Withdrawal provider id have been changed, eventId={}, walletId={}, providerId={}", event.getId(), event.getSource(), change.getRoute().getId());
        } catch (DaoException e) {
            throw new StorageException(e);
        }
    }
}

package com.rbkmoney.fistfulreporter.poller.handler.impl;

import com.rbkmoney.fistful.base.Cash;
import com.rbkmoney.fistful.withdrawal.Change;
import com.rbkmoney.fistful.withdrawal.SinkEvent;
import com.rbkmoney.fistfulreporter.dao.WithdrawalDao;
import com.rbkmoney.fistfulreporter.domain.enums.WithdrawalEventType;
import com.rbkmoney.fistfulreporter.domain.enums.WithdrawalStatus;
import com.rbkmoney.fistfulreporter.domain.tables.pojos.Withdrawal;
import com.rbkmoney.fistfulreporter.exception.DaoException;
import com.rbkmoney.fistfulreporter.exception.StorageException;
import com.rbkmoney.fistfulreporter.poller.handler.WithdrawalEventHandler;
import com.rbkmoney.geck.common.util.TypeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class WithdrawalCreatedHandler implements WithdrawalEventHandler {

    private final WithdrawalDao withdrawalDao;

    @Override
    public boolean accept(Change change) {
        return change.isSetCreated();
    }

    @Override
    public void handle(Change change, SinkEvent event) {
        try {
            log.info("Start withdrawal created handling, eventId={}, walletId={}", event.getId(), event.getSource());
            Withdrawal withdrawal = new Withdrawal();

            withdrawal.setEventId(event.getId());
            withdrawal.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
            withdrawal.setWithdrawalId(event.getSource());
            withdrawal.setSequenceId(event.getPayload().getSequence());
            withdrawal.setEventOccuredAt(TypeUtil.stringToLocalDateTime(event.getPayload().getOccuredAt()));
            withdrawal.setEventType(WithdrawalEventType.WITHDRAWAL_CREATED);
            withdrawal.setWalletId(change.getCreated().getSource());
            withdrawal.setDestinationId(change.getCreated().getDestination());
            withdrawal.setWithdrawalStatus(WithdrawalStatus.pending);

            Cash cash = change.getCreated().getBody();
            withdrawal.setAmount(cash.getAmount());
            withdrawal.setCurrencyCode(cash.getCurrency().getSymbolicCode());

            withdrawalDao.save(withdrawal);
            log.info("Withdrawal have been saved, eventId={}, walletId={}", event.getId(), event.getSource());
        } catch (DaoException e) {
            throw new StorageException(e);
        }
    }
}

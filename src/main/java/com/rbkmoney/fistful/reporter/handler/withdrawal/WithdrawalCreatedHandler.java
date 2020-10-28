package com.rbkmoney.fistful.reporter.handler.withdrawal;

import com.rbkmoney.dao.DaoException;
import com.rbkmoney.fistful.base.Cash;
import com.rbkmoney.fistful.reporter.dao.WalletDao;
import com.rbkmoney.fistful.reporter.dao.WithdrawalDao;
import com.rbkmoney.fistful.reporter.domain.enums.WithdrawalEventType;
import com.rbkmoney.fistful.reporter.domain.enums.WithdrawalStatus;
import com.rbkmoney.fistful.reporter.domain.tables.pojos.Wallet;
import com.rbkmoney.fistful.reporter.domain.tables.pojos.Withdrawal;
import com.rbkmoney.fistful.reporter.exception.SinkEventNotFoundException;
import com.rbkmoney.fistful.reporter.exception.StorageException;
import com.rbkmoney.fistful.withdrawal.TimestampedChange;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class WithdrawalCreatedHandler implements WithdrawalEventHandler {

    private final WithdrawalDao withdrawalDao;
    private final WalletDao walletDao;

    @Override
    public boolean accept(TimestampedChange change) {
        return change.getChange().isSetCreated() && change.getChange().getCreated().isSetWithdrawal();
    }

    @Override
    public void handle(TimestampedChange change, MachineEvent event) {
        try {
            var withdrawalDamsel = change.getChange().getCreated().getWithdrawal();

            log.info("Start withdrawal created handling, eventId={}, walletId={}", event.getEventId(), event.getSourceId());

            Wallet wallet = getWallet(event, withdrawalDamsel.getWalletId());

            Withdrawal withdrawal = new Withdrawal();
            withdrawal.setExternalId(withdrawalDamsel.getExternalId());
            withdrawal.setEventId(event.getEventId());
            withdrawal.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
            withdrawal.setWithdrawalId(event.getSourceId());
            withdrawal.setEventOccuredAt(TypeUtil.stringToLocalDateTime(change.getOccuredAt()));
            withdrawal.setEventType(WithdrawalEventType.WITHDRAWAL_CREATED);
            withdrawal.setWalletId(withdrawalDamsel.getWalletId());
            withdrawal.setDestinationId(withdrawalDamsel.getDestinationId());
            withdrawal.setWithdrawalStatus(WithdrawalStatus.pending);

            withdrawal.setPartyId(wallet.getPartyId());
            withdrawal.setPartyContractId(wallet.getPartyContractId());
            withdrawal.setIdentityId(wallet.getIdentityId());

            Cash cash = withdrawalDamsel.getBody();
            withdrawal.setAmount(cash.getAmount());
            withdrawal.setCurrencyCode(cash.getCurrency().getSymbolicCode());

            withdrawalDao.updateNotCurrent(event.getSourceId());
            withdrawalDao.save(withdrawal);
            log.info("Withdrawal has been saved, eventId={}, walletId={}", event.getEventId(), event.getSourceId());
        } catch (DaoException e) {
            throw new StorageException(e);
        }
    }

    private Wallet getWallet(MachineEvent event, String walletId) {
        Wallet wallet = walletDao.get(walletId);
        if (wallet == null) {
            throw new SinkEventNotFoundException(String.format("Wallet not found, destinationId='%s', walletId='%s'", event.getSourceId(), walletId));
        }
        return wallet;
    }
}

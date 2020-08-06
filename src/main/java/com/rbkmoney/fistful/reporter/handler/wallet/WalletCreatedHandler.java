package com.rbkmoney.fistful.reporter.handler.wallet;

import com.rbkmoney.dao.DaoException;
import com.rbkmoney.fistful.reporter.dao.WalletDao;
import com.rbkmoney.fistful.reporter.domain.enums.WalletEventType;
import com.rbkmoney.fistful.reporter.domain.tables.pojos.Wallet;
import com.rbkmoney.fistful.reporter.exception.StorageException;
import com.rbkmoney.fistful.wallet.TimestampedChange;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class WalletCreatedHandler implements WalletEventHandler {

    private final WalletDao walletDao;

    @Override
    public boolean accept(TimestampedChange change) {
        return change.getChange().isSetCreated();
    }

    @Override
    public void handle(TimestampedChange change, MachineEvent event) {
        try {
            log.info("Start wallet created handling, eventId={}, walletId={}", event.getEventId(), event.getSourceId());
            Wallet wallet = new Wallet();

            wallet.setEventId(event.getEventId());
            wallet.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
            wallet.setWalletId(event.getSourceId());
            wallet.setEventOccuredAt(TypeUtil.stringToLocalDateTime(change.getOccuredAt()));
            wallet.setEventType(WalletEventType.WALLET_CREATED);
            wallet.setWalletName(change.getChange().getCreated().getName());

            walletDao.updateNotCurrent(event.getSourceId());
            walletDao.save(wallet);
            log.info("Wallet have been saved, eventId={}, walletId={}", event.getEventId(), event.getSourceId());
        } catch (DaoException e) {
            throw new StorageException(e);
        }
    }
}

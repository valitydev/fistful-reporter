package com.rbkmoney.fistfulreporter.poller.handler.impl;

import com.rbkmoney.fistful.wallet.Change;
import com.rbkmoney.fistful.wallet.SinkEvent;
import com.rbkmoney.fistfulreporter.dao.WalletDao;
import com.rbkmoney.fistfulreporter.domain.enums.WalletEventType;
import com.rbkmoney.fistfulreporter.domain.tables.pojos.Wallet;
import com.rbkmoney.fistfulreporter.exception.DaoException;
import com.rbkmoney.fistfulreporter.exception.StorageException;
import com.rbkmoney.fistfulreporter.poller.handler.WalletEventHandler;
import com.rbkmoney.geck.common.util.TypeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class WalletCreatedHandler implements WalletEventHandler {

    private final WalletDao walletDao;

    @Override
    public boolean accept(Change change) {
        return change.isSetCreated();
    }

    @Override
    public void handle(Change change, SinkEvent event) {
        try {
            log.info("Start wallet created handling, eventId={}, walletId={}", event.getId(), event.getSource());
            Wallet wallet = new Wallet();

            wallet.setEventId(event.getId());
            wallet.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
            wallet.setWalletId(event.getSource());
            wallet.setSequenceId(event.getPayload().getSequence());
            wallet.setEventOccuredAt(TypeUtil.stringToLocalDateTime(event.getPayload().getOccuredAt()));
            wallet.setEventType(WalletEventType.WALLET_CREATED);
            wallet.setWalletName(change.getCreated().getName());

            walletDao.save(wallet);
            log.info("Wallet have been saved, eventId={}, walletId={}", event.getId(), event.getSource());
        } catch (DaoException e) {
            throw new StorageException(e);
        }
    }
}

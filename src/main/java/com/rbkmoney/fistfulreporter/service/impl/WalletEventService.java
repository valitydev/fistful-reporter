package com.rbkmoney.fistfulreporter.service.impl;

import com.rbkmoney.fistful.wallet.Change;
import com.rbkmoney.fistful.wallet.SinkEvent;
import com.rbkmoney.fistfulreporter.dao.WalletDao;
import com.rbkmoney.fistfulreporter.exception.DaoException;
import com.rbkmoney.fistfulreporter.exception.StorageException;
import com.rbkmoney.fistfulreporter.poller.handler.WalletEventHandler;
import com.rbkmoney.fistfulreporter.service.EventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class WalletEventService implements EventService<SinkEvent> {

    private final List<WalletEventHandler> eventHandlers;
    private final WalletDao walletDao;

    @Override
    public Optional<Long> getLastEventId() {
        try {
            Optional<Long> lastEventId = walletDao.getLastEventId();
            log.info("Last wallet eventId = {}", lastEventId);
            return lastEventId;
        } catch (DaoException e) {
            throw new StorageException("Failed to get last wallet event id", e);
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void processSinkEvent(SinkEvent event) throws StorageException {
        for (Change change : event.getPayload().getChanges()) {
            for (WalletEventHandler identityEventHandler : eventHandlers) {
                if (identityEventHandler.accept(change)) {
                    identityEventHandler.handle(change, event);
                }
            }
        }
    }
}

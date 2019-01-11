package com.rbkmoney.fistfulreporter.service.impl;

import com.rbkmoney.fistful.identity.Change;
import com.rbkmoney.fistful.identity.SinkEvent;
import com.rbkmoney.fistfulreporter.dao.IdentityDao;
import com.rbkmoney.fistfulreporter.exception.DaoException;
import com.rbkmoney.fistfulreporter.exception.StorageException;
import com.rbkmoney.fistfulreporter.poller.handler.IdentityEventHandler;
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
public class IdentityEventService implements EventService<SinkEvent> {

    private final List<IdentityEventHandler> eventHandlers;
    private final IdentityDao identityDao;

    @Override
    public Optional<Long> getLastEventId() {
        try {
            Optional<Long> lastEventId = identityDao.getLastEventId();
            log.info("Last identity eventId = {}", lastEventId);
            return lastEventId;
        } catch (DaoException e) {
            throw new StorageException("Failed to get last identity event id", e);
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void processSinkEvent(SinkEvent event) {
        for (Change change : event.getPayload().getChanges()) {
            for (IdentityEventHandler identityEventHandler : eventHandlers) {
                if (identityEventHandler.accept(change)) {
                    identityEventHandler.handle(change, event);
                }
            }
        }
    }
}

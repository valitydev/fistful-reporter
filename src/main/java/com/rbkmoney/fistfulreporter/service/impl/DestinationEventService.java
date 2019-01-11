package com.rbkmoney.fistfulreporter.service.impl;

import com.rbkmoney.fistful.destination.Change;
import com.rbkmoney.fistful.destination.SinkEvent;
import com.rbkmoney.fistfulreporter.dao.DestinationDao;
import com.rbkmoney.fistfulreporter.exception.DaoException;
import com.rbkmoney.fistfulreporter.exception.StorageException;
import com.rbkmoney.fistfulreporter.poller.handler.DestinationEventHandler;
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
public class DestinationEventService implements EventService<SinkEvent> {

    private final List<DestinationEventHandler> eventHandlers;
    private final DestinationDao destinationDao;

    @Override
    public Optional<Long> getLastEventId() {
        try {
            Optional<Long> lastEventId = destinationDao.getLastEventId();
            log.info("Last destination eventId = {}", lastEventId);
            return lastEventId;
        } catch (DaoException e) {
            throw new StorageException("Failed to get last destination event id", e);
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void processSinkEvent(SinkEvent event) {
        for (Change change : event.getPayload().getChanges()) {
            for (DestinationEventHandler identityEventHandler : eventHandlers) {
                if (identityEventHandler.accept(change)) {
                    identityEventHandler.handle(change, event);
                }
            }
        }
    }
}

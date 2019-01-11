package com.rbkmoney.fistfulreporter.service.impl;

import com.rbkmoney.fistful.source.Change;
import com.rbkmoney.fistful.source.SinkEvent;
import com.rbkmoney.fistfulreporter.dao.SourceDao;
import com.rbkmoney.fistfulreporter.exception.DaoException;
import com.rbkmoney.fistfulreporter.exception.StorageException;
import com.rbkmoney.fistfulreporter.poller.handler.SourceEventHandler;
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
public class SourceEventService implements EventService<SinkEvent> {

    private final List<SourceEventHandler> eventHandlers;
    private final SourceDao sourceDao;

    @Override
    public Optional<Long> getLastEventId() {
        try {
            Optional<Long> lastEventId = sourceDao.getLastEventId();
            log.info("Last source eventId = {}", lastEventId);
            return lastEventId;
        } catch (DaoException e) {
            throw new StorageException("Failed to get last source event id", e);
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void processSinkEvent(SinkEvent event) throws StorageException {
        for (Change change : event.getPayload().getChanges()) {
            for (SourceEventHandler identityEventHandler : eventHandlers) {
                if (identityEventHandler.accept(change)) {
                    identityEventHandler.handle(change, event);
                }
            }
        }
    }
}

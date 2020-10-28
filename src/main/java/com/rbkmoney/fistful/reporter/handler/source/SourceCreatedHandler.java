package com.rbkmoney.fistful.reporter.handler.source;

import com.rbkmoney.dao.DaoException;
import com.rbkmoney.fistful.reporter.dao.SourceDao;
import com.rbkmoney.fistful.reporter.domain.enums.SourceEventType;
import com.rbkmoney.fistful.reporter.domain.enums.SourceStatus;
import com.rbkmoney.fistful.reporter.domain.tables.pojos.Source;
import com.rbkmoney.fistful.reporter.exception.StorageException;
import com.rbkmoney.fistful.source.Internal;
import com.rbkmoney.fistful.source.Resource;
import com.rbkmoney.fistful.source.TimestampedChange;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SourceCreatedHandler implements SourceEventHandler {

    private final SourceDao sourceDao;

    @Override
    public boolean accept(TimestampedChange change) {
        return change.getChange().isSetCreated();
    }

    @Override
    public void handle(TimestampedChange change, MachineEvent event) {
        try {
            log.info("Start source created handling, eventId={}, sourceId={}", event.getEventId(), event.getSourceId());
            Source source = new Source();

            source.setEventId(event.getEventId());
            source.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
            source.setSourceId(event.getSourceId());
            source.setEventOccuredAt(TypeUtil.stringToLocalDateTime(change.getOccuredAt()));
            source.setEventType(SourceEventType.SOURCE_CREATED);
            source.setSourceName(change.getChange().getCreated().getName());
            source.setSourceStatus(SourceStatus.unauthorized);

            Resource resource = change.getChange().getCreated().getResource();
            if (resource.isSetInternal()) {
                Internal internal = resource.getInternal();
                source.setResourceInternalDetails(internal.getDetails());
            }

            sourceDao.updateNotCurrent(event.getSourceId());
            sourceDao.save(source);
            log.info("Source has been saved, eventId={}, sourceId={}", event.getEventId(), event.getSourceId());
        } catch (DaoException e) {
            throw new StorageException(e);
        }
    }
}

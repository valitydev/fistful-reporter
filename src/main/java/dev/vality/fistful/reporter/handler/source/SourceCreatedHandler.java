package dev.vality.fistful.reporter.handler.source;

import dev.vality.dao.DaoException;
import dev.vality.fistful.reporter.dao.SourceDao;
import dev.vality.fistful.reporter.domain.enums.SourceEventType;
import dev.vality.fistful.reporter.domain.enums.SourceStatus;
import dev.vality.fistful.reporter.domain.tables.pojos.Source;
import dev.vality.fistful.reporter.exception.StorageException;
import dev.vality.fistful.source.Internal;
import dev.vality.fistful.source.Resource;
import dev.vality.fistful.source.TimestampedChange;
import dev.vality.geck.common.util.TypeUtil;
import dev.vality.machinegun.eventsink.MachineEvent;
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

            sourceDao.save(source).ifPresentOrElse(
                    dbContractId -> log.info("Source created has been saved, eventId={}, sourceId={}",
                            event.getEventId(), event.getSourceId()),
                    () -> log.info("Source created bound duplicated, eventId={}, sourceId={}",
                            event.getEventId(), event.getSourceId()));
        } catch (DaoException e) {
            throw new StorageException(e);
        }
    }
}

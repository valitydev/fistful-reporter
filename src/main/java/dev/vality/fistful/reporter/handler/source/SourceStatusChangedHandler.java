package dev.vality.fistful.reporter.handler.source;

import dev.vality.dao.DaoException;
import dev.vality.fistful.reporter.dao.SourceDao;
import dev.vality.fistful.reporter.domain.enums.SourceEventType;
import dev.vality.fistful.reporter.domain.enums.SourceStatus;
import dev.vality.fistful.reporter.domain.tables.pojos.Source;
import dev.vality.fistful.reporter.exception.StorageException;
import dev.vality.fistful.source.Status;
import dev.vality.fistful.source.TimestampedChange;
import dev.vality.geck.common.util.TBaseUtil;
import dev.vality.geck.common.util.TypeUtil;
import dev.vality.machinegun.eventsink.MachineEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SourceStatusChangedHandler implements SourceEventHandler {

    private final SourceDao sourceDao;

    @Override
    public boolean accept(TimestampedChange change) {
        return change.getChange().isSetStatus() && change.getChange().getStatus().isSetStatus();
    }

    @Override
    public void handle(TimestampedChange change, MachineEvent event) {
        try {
            Status status = change.getChange().getStatus().getStatus();
            log.info("Start source status changed handling, eventId={}, sourceId={}, status={}",
                    event.getEventId(), event.getSourceId(), status);
            Source oldSource = sourceDao.get(event.getSourceId());
            Source updatedSource = update(oldSource, change, event, status);
            sourceDao.save(updatedSource).ifPresentOrElse(
                    id -> {
                        sourceDao.updateNotCurrent(oldSource.getId());
                        log.info("Source status have been changed, eventId={}, sourceId={}, status={}",
                                event.getEventId(), event.getSourceId(), status);
                    },
                    () -> log.info("Source status bound duplicated, eventId={}, sourceId={}, status={}",
                            event.getEventId(), event.getSourceId(), status));
        } catch (DaoException e) {
            throw new StorageException(e);
        }
    }

    private Source update(
            Source oldSource,
            TimestampedChange change,
            MachineEvent event,
            Status status) {
        Source source = new Source(oldSource);
        source.setId(null);
        source.setWtime(null);
        source.setEventId(event.getEventId());
        source.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
        source.setSourceId(event.getSourceId());
        source.setEventOccuredAt(TypeUtil.stringToLocalDateTime(change.getOccuredAt()));
        source.setEventType(SourceEventType.SOURCE_STATUS_CHANGED);
        source.setSourceStatus(TBaseUtil.unionFieldToEnum(status, SourceStatus.class));
        return source;
    }
}

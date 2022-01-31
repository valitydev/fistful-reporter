package com.rbkmoney.fistful.reporter.handler.destination;

import com.rbkmoney.dao.DaoException;
import dev.vality.fistful.destination.Status;
import dev.vality.fistful.destination.TimestampedChange;
import com.rbkmoney.fistful.reporter.dao.DestinationDao;
import com.rbkmoney.fistful.reporter.domain.enums.DestinationEventType;
import com.rbkmoney.fistful.reporter.domain.enums.DestinationStatus;
import com.rbkmoney.fistful.reporter.domain.tables.pojos.Destination;
import com.rbkmoney.fistful.reporter.exception.StorageException;
import com.rbkmoney.geck.common.util.TBaseUtil;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class DestinationStatusChangedHandler implements DestinationEventHandler {

    private final DestinationDao destinationDao;

    @Override
    public boolean accept(TimestampedChange change) {
        return change.getChange().isSetStatus() && change.getChange().getStatus().isSetChanged();
    }

    @Override
    public void handle(TimestampedChange change, MachineEvent event) {
        try {
            Status status = change.getChange().getStatus().getChanged();
            log.info("Start destination status changed handling, eventId={}, destinationId={}, status={}",
                    event.getEventId(), event.getSourceId(), status);
            Destination oldDestination = destinationDao.get(event.getSourceId());
            Destination updatedDestination = update(oldDestination, change, event, status);
            destinationDao.save(updatedDestination).ifPresentOrElse(
                    id -> {
                        destinationDao.updateNotCurrent(oldDestination.getId());
                        log.info("Destination status has been changed, eventId={}, destinationId={}, status={}",
                                event.getEventId(), event.getSourceId(), status);
                    },
                    () -> log.info("Destination status changed bound duplicated, " +
                                    "eventId={}, destinationId={}, status={}",
                            event.getEventId(), event.getSourceId(), status));
        } catch (DaoException e) {
            throw new StorageException(e);
        }
    }

    private Destination update(
            Destination oldDestination,
            TimestampedChange change,
            MachineEvent event,
            Status status) {
        Destination destination = new Destination(oldDestination);
        destination.setId(null);
        destination.setWtime(null);
        destination.setEventId(event.getEventId());
        destination.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
        destination.setDestinationId(event.getSourceId());
        destination.setEventOccuredAt(TypeUtil.stringToLocalDateTime(change.getOccuredAt()));
        destination.setEventType(DestinationEventType.DESTINATION_STATUS_CHANGED);
        destination.setDestinationStatus(TBaseUtil.unionFieldToEnum(status, DestinationStatus.class));
        return destination;
    }
}

package dev.vality.fistful.reporter.handler.identity;

import dev.vality.dao.DaoException;
import dev.vality.fistful.identity.TimestampedChange;
import dev.vality.fistful.reporter.dao.IdentityDao;
import dev.vality.fistful.reporter.domain.enums.IdentityEventType;
import dev.vality.fistful.reporter.domain.tables.pojos.Identity;
import dev.vality.fistful.reporter.exception.StorageException;
import dev.vality.geck.common.util.TypeUtil;
import dev.vality.machinegun.eventsink.MachineEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class IdentityLevelChangedHandler implements IdentityEventHandler {

    private final IdentityDao identityDao;

    @Override
    public boolean accept(TimestampedChange change) {
        return change.getChange().isSetLevelChanged();
    }

    @Override
    public void handle(TimestampedChange change, MachineEvent event) {
        try {
            log.info("Start identity level changed handling, eventId={}, identityId={}, level={}",
                    event.getEventId(), event.getSourceId(), change.getChange().getLevelChanged());
            Identity oldIdentity = identityDao.get(event.getSourceId());
            Identity updatedIdentity = update(oldIdentity, change, event);
            identityDao.save(updatedIdentity).ifPresentOrElse(
                    id -> {
                        identityDao.updateNotCurrent(oldIdentity.getId());
                        log.info("Identity level has been changed, eventId={}, identityId={}, level={}",
                                event.getEventId(), event.getSourceId(), change.getChange().getLevelChanged());
                    },
                    () -> log.info("Identity level bound duplicated, eventId={}, identityId={}, level={}",
                            event.getEventId(), event.getSourceId(), change.getChange().getLevelChanged())
            );
        } catch (DaoException e) {
            throw new StorageException(e);
        }
    }

    private Identity update(
            Identity oldIdentity,
            TimestampedChange change,
            MachineEvent event) {
        Identity identity = new Identity(oldIdentity);
        identity.setId(null);
        identity.setWtime(null);
        identity.setEventId(event.getEventId());
        identity.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
        identity.setIdentityId(event.getSourceId());
        identity.setEventOccuredAt(TypeUtil.stringToLocalDateTime(change.getOccuredAt()));
        identity.setEventType(IdentityEventType.IDENTITY_LEVEL_CHANGED);
        identity.setIdentityLevelId(change.getChange().getLevelChanged());
        return identity;
    }

}

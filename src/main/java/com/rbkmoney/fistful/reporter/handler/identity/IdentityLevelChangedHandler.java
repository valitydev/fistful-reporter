package com.rbkmoney.fistful.reporter.poller.impl;

import com.rbkmoney.dao.DaoException;
import com.rbkmoney.fistful.identity.TimestampedChange;
import com.rbkmoney.fistful.reporter.dao.IdentityDao;
import com.rbkmoney.fistful.reporter.domain.enums.IdentityEventType;
import com.rbkmoney.fistful.reporter.domain.tables.pojos.Identity;
import com.rbkmoney.fistful.reporter.exception.StorageException;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
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
            log.info("Start identity level changed handling, eventId={}, identityId={}, level={}", event.getEventId(), event.getSourceId(), change.getChange().getLevelChanged());
            Identity identity = identityDao.get(event.getSourceId());

            identity.setId(null);
            identity.setWtime(null);

            identity.setEventId(event.getEventId());
            identity.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
            identity.setIdentityId(event.getSourceId());
            identity.setEventOccuredAt(TypeUtil.stringToLocalDateTime(change.getOccuredAt()));
            identity.setEventType(IdentityEventType.IDENTITY_LEVEL_CHANGED);
            identity.setIdentityLevelId(change.getChange().getLevelChanged());

            identityDao.updateNotCurrent(event.getSourceId());
            identityDao.save(identity);
            log.info("Identity level has been changed, eventId={}, identityId={}, level={}", event.getId(), event.getSource(), change.getLevelChanged());
        } catch (DaoException e) {
            throw new StorageException(e);
        }
    }

}

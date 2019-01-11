package com.rbkmoney.fistfulreporter.poller.handler.impl;

import com.rbkmoney.fistful.identity.Change;
import com.rbkmoney.fistful.identity.SinkEvent;
import com.rbkmoney.fistfulreporter.dao.IdentityDao;
import com.rbkmoney.fistfulreporter.domain.enums.IdentityEventType;
import com.rbkmoney.fistfulreporter.domain.tables.pojos.Identity;
import com.rbkmoney.fistfulreporter.exception.DaoException;
import com.rbkmoney.fistfulreporter.exception.StorageException;
import com.rbkmoney.fistfulreporter.poller.handler.IdentityEventHandler;
import com.rbkmoney.geck.common.util.TypeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class IdentityLevelChangedHandler implements IdentityEventHandler {

    private final IdentityDao identityDao;

    @Override
    public boolean accept(Change change) {
        return change.isSetLevelChanged();
    }

    @Override
    public void handle(Change change, SinkEvent event) {
        try {
            log.info("Start identity level changed handling, eventId={}, identityId={}, level={}", event.getId(), event.getSource(), change.getLevelChanged());
            Identity identity = identityDao.get(event.getSource());

            identity.setId(null);
            identity.setWtime(null);

            identity.setEventId(event.getId());
            identity.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
            identity.setIdentityId(event.getSource());
            identity.setSequenceId(event.getPayload().getSequence());
            identity.setEventOccuredAt(TypeUtil.stringToLocalDateTime(event.getPayload().getOccuredAt()));
            identity.setEventType(IdentityEventType.IDENTITY_LEVEL_CHANGED);
            identity.setIdentityLevelId(change.getLevelChanged());

            identityDao.updateNotCurrent(event.getSource());
            identityDao.save(identity);
            log.info("Identity level have been changed, eventId={}, identityId={}, level={}", event.getId(), event.getSource(), change.getLevelChanged());
        } catch (DaoException e) {
            throw new StorageException(e);
        }
    }

}

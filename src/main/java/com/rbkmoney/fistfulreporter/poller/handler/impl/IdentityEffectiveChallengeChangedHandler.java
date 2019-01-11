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
public class IdentityEffectiveChallengeChangedHandler implements IdentityEventHandler {

    private final IdentityDao identityDao;

    @Override
    public boolean accept(Change change) {
        return change.isSetEffectiveChallengeChanged();
    }

    @Override
    public void handle(Change change, SinkEvent event) {
        try {
            log.info("Start effective identity challenge changed handling, eventId={}, identityId={}, effectiveChallengeId={}", event.getId(), event.getSource(), change.getEffectiveChallengeChanged());
            Identity identity = identityDao.get(event.getSource());

            identity.setId(null);
            identity.setWtime(null);

            identity.setEventId(event.getId());
            identity.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
            identity.setIdentityId(event.getSource());
            identity.setSequenceId(event.getPayload().getSequence());
            identity.setEventOccuredAt(TypeUtil.stringToLocalDateTime(event.getPayload().getOccuredAt()));
            identity.setEventType(IdentityEventType.IDENTITY_EFFECTIVE_CHALLENGE_CHANGED);
            identity.setIdentityEffectiveChalengeId(change.getEffectiveChallengeChanged());

            identityDao.updateNotCurrent(event.getSource());
            identityDao.save(identity);
            log.info("Effective identity challenge have been changed, eventId={}, identityId={}, effectiveChallengeId={}", event.getId(), event.getSource(), change.getEffectiveChallengeChanged());
        } catch (DaoException e) {
            throw new StorageException(e);
        }
    }
}

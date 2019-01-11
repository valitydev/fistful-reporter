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
public class IdentityCreatedHandler implements IdentityEventHandler {

    private final IdentityDao identityDao;

    @Override
    public boolean accept(Change change) {
        return change.isSetCreated();
    }

    @Override
    public void handle(Change change, SinkEvent event) {
        try {
            log.info("Start identity created handling, eventId={}, identityId={}", event.getId(), event.getSource());
            Identity identity = new Identity();

            identity.setEventId(event.getId());
            identity.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
            identity.setIdentityId(event.getSource());
            identity.setSequenceId(event.getPayload().getSequence());
            identity.setEventOccuredAt(TypeUtil.stringToLocalDateTime(event.getPayload().getOccuredAt()));
            identity.setEventType(IdentityEventType.IDENTITY_CREATED);
            identity.setPartyId(change.getCreated().getParty());
            identity.setIdentityProviderId(change.getCreated().getProvider());
            identity.setIdentityClassId(change.getCreated().getCls());
            identity.setPartyContractId(change.getCreated().getContract());

            identityDao.save(identity);
            log.info("Identity haven been saved, eventId={}, identityId={}", event.getId(), event.getSource());
        } catch (DaoException e) {
            throw new StorageException(e);
        }
    }
}

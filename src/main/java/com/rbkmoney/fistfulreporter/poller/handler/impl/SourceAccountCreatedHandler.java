package com.rbkmoney.fistfulreporter.poller.handler.impl;

import com.rbkmoney.fistful.account.Account;
import com.rbkmoney.fistful.source.Change;
import com.rbkmoney.fistful.source.SinkEvent;
import com.rbkmoney.fistfulreporter.dao.IdentityDao;
import com.rbkmoney.fistfulreporter.dao.SourceDao;
import com.rbkmoney.fistfulreporter.domain.enums.SourceEventType;
import com.rbkmoney.fistfulreporter.domain.tables.pojos.Identity;
import com.rbkmoney.fistfulreporter.domain.tables.pojos.Source;
import com.rbkmoney.fistfulreporter.exception.DaoException;
import com.rbkmoney.fistfulreporter.exception.NotFoundException;
import com.rbkmoney.fistfulreporter.exception.StorageException;
import com.rbkmoney.fistfulreporter.poller.handler.SourceEventHandler;
import com.rbkmoney.geck.common.util.TypeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class SourceAccountCreatedHandler implements SourceEventHandler {

    private final SourceDao sourceDao;
    private final IdentityDao identityDao;

    @Override
    public boolean accept(Change change) {
        return change.isSetAccount() && change.getAccount().isSetCreated();
    }

    @Override
    public void handle(Change change, SinkEvent event) {
        try {
            Account account = change.getAccount().getCreated();
            log.info("Start source account created handling, eventId={}, sourceId={}, identityId={}", event.getId(), event.getSource(), account.getIdentity());
            Source source = getSource(event);
            Identity identity = getIdentity(event, account);

            source.setId(null);
            source.setWtime(null);

            source.setEventId(event.getId());
            source.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
            source.setSourceId(event.getSource());
            source.setSequenceId(event.getPayload().getSequence());
            source.setEventOccuredAt(TypeUtil.stringToLocalDateTime(event.getPayload().getOccuredAt()));
            source.setEventType(SourceEventType.SOURCE_ACCOUNT_CREATED);
            source.setAccountId(account.getId());
            source.setIdentityId(account.getIdentity());
            source.setAccounterAccountId(account.getAccounterAccountId());
            source.setCurrencyCode(account.getCurrency().getSymbolicCode());
            source.setPartyId(identity.getPartyId());

            sourceDao.updateNotCurrent(event.getSource());
            sourceDao.save(source);
            log.info("Source account have been saved, eventId={}, sourceId={}, identityId={}", event.getId(), event.getSource(), account.getIdentity());
        } catch (DaoException e) {
            throw new StorageException(e);
        }
    }

    private Source getSource(SinkEvent event) throws DaoException {
        Source source = sourceDao.get(event.getSource());
        if (source == null) {
            throw new NotFoundException(String.format("Source not found, walletId='%s'", event.getSource()));
        }
        return source;
    }

    private Identity getIdentity(SinkEvent event, Account account) throws DaoException {
        Identity identity = identityDao.get(account.getIdentity());
        if (identity == null) {
            throw new NotFoundException(String.format("Identity not found, walletId='%s'", event.getSource()));
        }
        return identity;
    }
}

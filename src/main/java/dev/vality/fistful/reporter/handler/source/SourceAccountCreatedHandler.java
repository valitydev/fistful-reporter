package dev.vality.fistful.reporter.handler.source;

import dev.vality.dao.DaoException;
import dev.vality.fistful.account.Account;
import dev.vality.fistful.reporter.dao.SourceDao;
import dev.vality.fistful.reporter.domain.enums.SourceEventType;
import dev.vality.fistful.reporter.domain.tables.pojos.Source;
import dev.vality.fistful.reporter.exception.SinkEventNotFoundException;
import dev.vality.fistful.reporter.exception.StorageException;
import dev.vality.fistful.source.TimestampedChange;
import dev.vality.geck.common.util.TypeUtil;
import dev.vality.machinegun.eventsink.MachineEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SourceAccountCreatedHandler implements SourceEventHandler {

    private final SourceDao sourceDao;

    @Override
    public boolean accept(TimestampedChange change) {
        return change.getChange().isSetAccount() && change.getChange().getAccount().isSetCreated();
    }

    @Override
    public void handle(TimestampedChange change, MachineEvent event) {
        try {
            Account account = change.getChange().getAccount().getCreated();
            log.info("Start source account created handling, eventId={}, sourceId={}, partyId={}",
                    event.getEventId(), event.getSourceId(), account.getPartyId());
            Source oldSource = getSource(event);
            Source updatedSource = update(oldSource, change, event, account);
            sourceDao.save(updatedSource).ifPresentOrElse(
                    id -> {
                        sourceDao.updateNotCurrent(oldSource.getId());
                        log.info("Source account have been changed, eventId={}, sourceId={}, partyId={}",
                                event.getEventId(), event.getSourceId(), account.getPartyId());
                    },
                    () -> log.info("Source account bound duplicated, eventId={}, sourceId={}, partyId={}",
                            event.getEventId(), event.getSourceId(), account.getPartyId()));
        } catch (DaoException e) {
            throw new StorageException(e);
        }
    }

    private Source update(
            Source oldSource,
            TimestampedChange change,
            MachineEvent event,
            Account account) {
        Source source = new Source(oldSource);
        source.setId(null);
        source.setWtime(null);
        source.setEventId(event.getEventId());
        source.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
        source.setSourceId(event.getSourceId());
        source.setEventOccuredAt(TypeUtil.stringToLocalDateTime(change.getOccuredAt()));
        source.setEventType(SourceEventType.SOURCE_ACCOUNT_CREATED);
        source.setAccountId(String.valueOf(account.getAccountId()));
        source.setCurrencyCode(account.getCurrency().getSymbolicCode());
        source.setPartyId(account.getPartyId());
        return source;
    }

    private Source getSource(MachineEvent event) {
        Source source = sourceDao.get(event.getSourceId());
        if (source == null) {
            throw new SinkEventNotFoundException(String.format("Source not found, sourceId='%s'", event.getSourceId()));
        }
        return source;
    }
}

package com.rbkmoney.fistfulreporter.poller.handler.impl;

import com.rbkmoney.fistful.source.Change;
import com.rbkmoney.fistful.source.SinkEvent;
import com.rbkmoney.fistful.source.Status;
import com.rbkmoney.fistfulreporter.dao.SourceDao;
import com.rbkmoney.fistfulreporter.domain.enums.SourceEventType;
import com.rbkmoney.fistfulreporter.domain.enums.SourceStatus;
import com.rbkmoney.fistfulreporter.domain.tables.pojos.Source;
import com.rbkmoney.fistfulreporter.exception.DaoException;
import com.rbkmoney.fistfulreporter.exception.StorageException;
import com.rbkmoney.fistfulreporter.poller.handler.SourceEventHandler;
import com.rbkmoney.geck.common.util.TBaseUtil;
import com.rbkmoney.geck.common.util.TypeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class SourceStatusChangedHandler implements SourceEventHandler {

    private final SourceDao sourceDao;

    @Override
    public boolean accept(Change change) {
        return change.isSetStatus() && change.getStatus().isSetChanged();
    }

    @Override
    public void handle(Change change, SinkEvent event) {
        try {
            Status status = change.getStatus().getChanged();
            log.info("Start source status changed handling, eventId={}, sourceId={}, status={}", event.getId(), event.getSource(), status);

            Source source = sourceDao.get(event.getSource());

            source.setId(null);
            source.setWtime(null);

            source.setEventId(event.getId());
            source.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
            source.setSourceId(event.getSource());
            source.setSequenceId(event.getPayload().getSequence());
            source.setEventOccuredAt(TypeUtil.stringToLocalDateTime(event.getPayload().getOccuredAt()));
            source.setEventType(SourceEventType.SOURCE_STATUS_CHANGED);
            source.setSourceStatus(TBaseUtil.unionFieldToEnum(status, SourceStatus.class));

            sourceDao.updateNotCurrent(event.getSource());
            sourceDao.save(source);
            log.info("Source status have been changed, eventId={}, sourceId={}, status={}", event.getId(), event.getSource(), status);
        } catch (DaoException e) {
            throw new StorageException(e);
        }
    }
}

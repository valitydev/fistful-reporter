package com.rbkmoney.fistful.reporter.service.impl;

import com.rbkmoney.fistful.reporter.handler.source.SourceEventHandler;
import com.rbkmoney.fistful.source.TimestampedChange;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.sink.common.parser.impl.MachineEventParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class SourceEventService {

    private final List<SourceEventHandler> sourceEventHandlers;
    private final MachineEventParser<TimestampedChange> parser;

    @Transactional(propagation = Propagation.REQUIRED)
    public void handleEvents(List<MachineEvent> machineEvents) {
        machineEvents.forEach(this::handleIfAccept);
    }

    private void handleIfAccept(MachineEvent machineEvent) {
        TimestampedChange change = parser.parse(machineEvent);

        if (change.isSetChange()) {
            sourceEventHandlers.stream()
                    .filter(handler -> handler.accept(change))
                    .forEach(handler -> handler.handle(change, machineEvent));
        }

    }
}

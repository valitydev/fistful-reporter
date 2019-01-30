package com.rbkmoney.fistful.reporter.handler;

import com.rbkmoney.eventstock.client.EventAction;
import com.rbkmoney.eventstock.client.EventHandler;
import com.rbkmoney.fistful.reporter.service.EventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class EventSinkHandler<T> implements EventHandler<T> {

    private final EventService eventStockService;

    @Override
    public EventAction handle(T event, String subsKey) throws Exception {
        try {
            eventStockService.processSinkEvent(event);
            return EventAction.CONTINUE;
        } catch (Exception ex) {
            log.warn("Failed to handle event, retry", ex);
            return EventAction.DELAYED_RETRY;
        }
    }
}

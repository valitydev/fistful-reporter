package com.rbkmoney.fistful.reporter.poller;

import com.rbkmoney.eventstock.client.EventAction;
import com.rbkmoney.eventstock.client.EventHandler;
import com.rbkmoney.fistful.reporter.service.EventService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EventSinkHandler<T> implements EventHandler<T> {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final EventService eventStockService;

    public EventSinkHandler(EventService eventStockService) {
        this.eventStockService = eventStockService;
    }

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

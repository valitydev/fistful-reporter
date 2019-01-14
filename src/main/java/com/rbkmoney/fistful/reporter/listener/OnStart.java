package com.rbkmoney.fistful.reporter.listener;

import com.rbkmoney.eventstock.client.DefaultSubscriberConfig;
import com.rbkmoney.eventstock.client.EventConstraint;
import com.rbkmoney.eventstock.client.EventPublisher;
import com.rbkmoney.eventstock.client.SubscriberConfig;
import com.rbkmoney.eventstock.client.poll.EventFlowFilter;
import com.rbkmoney.fistful.reporter.service.impl.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class OnStart implements ApplicationListener<ApplicationReadyEvent> {

    private final EventPublisher depositEventPublisher;
    private final EventPublisher destinationEventPublisher;
    private final EventPublisher identityEventPublisher;
    private final EventPublisher sourceEventPublisher;
    private final EventPublisher walletEventPublisher;
    private final EventPublisher withdrawalEventPublisher;

    private final DepositEventService depositEventService;
    private final DestinationEventService destinationEventService;
    private final IdentityEventService identityEventService;
    private final SourceEventService sourceEventService;
    private final WalletEventService walletEventService;
    private final WithdrawalEventService withdrawalEventService;

    @Value("${eventStock.pollingEnable:true}")
    private boolean pollingEnabled;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        if (pollingEnabled) {
            depositEventPublisher.subscribe(buildSubscriberConfig(depositEventService.getLastEventId()));
            destinationEventPublisher.subscribe(buildSubscriberConfig(destinationEventService.getLastEventId()));
            identityEventPublisher.subscribe(buildSubscriberConfig(identityEventService.getLastEventId()));
            sourceEventPublisher.subscribe(buildSubscriberConfig(sourceEventService.getLastEventId()));
            walletEventPublisher.subscribe(buildSubscriberConfig(walletEventService.getLastEventId()));
            withdrawalEventPublisher.subscribe(buildSubscriberConfig(withdrawalEventService.getLastEventId()));
        }
    }

    private SubscriberConfig buildSubscriberConfig(Optional<Long> lastEventIdOptional) {
        EventConstraint.EventIDRange eventIDRange = new EventConstraint.EventIDRange();
        lastEventIdOptional.ifPresent(eventIDRange::setFromExclusive);
        EventFlowFilter eventFlowFilter = new EventFlowFilter(new EventConstraint(eventIDRange));
        return new DefaultSubscriberConfig(eventFlowFilter);
    }

}

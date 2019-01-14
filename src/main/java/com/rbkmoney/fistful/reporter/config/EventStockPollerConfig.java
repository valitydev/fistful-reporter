package com.rbkmoney.fistful.reporter.config;

import com.rbkmoney.eventstock.client.EventPublisher;
import com.rbkmoney.eventstock.client.poll.FistfulPollingEventPublisherBuilder;
import com.rbkmoney.fistful.reporter.poller.EventSinkHandler;
import com.rbkmoney.fistful.reporter.service.impl.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.io.IOException;

@Configuration
public class EventStockPollerConfig {

    @Bean
    public EventPublisher depositEventPublisher(
            DepositEventService eventService,
            @Value("${deposit.polling.url}") Resource pollingUrl,
            @Value("${deposit.polling.querySize}") int pollingQuerySize,
            @Value("${deposit.polling.maxPoolSize}") int pollingMaxPoolSize,
            @Value("${deposit.polling.delay}") int pollingMaxDelay,
            @Value("${deposit.polling.retryDelay}") int retryDelay
    ) throws IOException {
        return new FistfulPollingEventPublisherBuilder()
                .withIdentityServiceAdapter()
                .withURI(pollingUrl.getURI())
                .withMaxQuerySize(pollingQuerySize)
                .withMaxPoolSize(pollingMaxPoolSize)
                .withPollDelay(pollingMaxDelay)
                .withEventRetryDelay(retryDelay)
                .withEventHandler(new EventSinkHandler(eventService))
                .build();
    }

    @Bean
    public EventPublisher destinationEventPublisher(
            DestinationEventService eventService,
            @Value("${destination.polling.url}") Resource pollingUrl,
            @Value("${destination.polling.querySize}") int pollingQuerySize,
            @Value("${destination.polling.maxPoolSize}") int pollingMaxPoolSize,
            @Value("${destination.polling.delay}") int pollingMaxDelay,
            @Value("${destination.polling.retryDelay}") int retryDelay
    ) throws IOException {
        return new FistfulPollingEventPublisherBuilder()
                .withIdentityServiceAdapter()
                .withURI(pollingUrl.getURI())
                .withMaxQuerySize(pollingQuerySize)
                .withMaxPoolSize(pollingMaxPoolSize)
                .withPollDelay(pollingMaxDelay)
                .withEventRetryDelay(retryDelay)
                .withEventHandler(new EventSinkHandler(eventService))
                .build();
    }

    @Bean
    public EventPublisher identityEventPublisher(
            IdentityEventService eventService,
            @Value("${identity.polling.url}") Resource pollingUrl,
            @Value("${identity.polling.querySize}") int pollingQuerySize,
            @Value("${identity.polling.maxPoolSize}") int pollingMaxPoolSize,
            @Value("${identity.polling.delay}") int pollingMaxDelay,
            @Value("${identity.polling.retryDelay}") int retryDelay
    ) throws IOException {
        return new FistfulPollingEventPublisherBuilder()
                .withIdentityServiceAdapter()
                .withURI(pollingUrl.getURI())
                .withMaxQuerySize(pollingQuerySize)
                .withMaxPoolSize(pollingMaxPoolSize)
                .withPollDelay(pollingMaxDelay)
                .withEventRetryDelay(retryDelay)
                .withEventHandler(new EventSinkHandler(eventService))
                .build();
    }

    @Bean
    public EventPublisher sourceEventPublisher(
            SourceEventService eventService,
            @Value("${source.polling.url}") Resource pollingUrl,
            @Value("${source.polling.querySize}") int pollingQuerySize,
            @Value("${source.polling.maxPoolSize}") int pollingMaxPoolSize,
            @Value("${source.polling.delay}") int pollingMaxDelay,
            @Value("${source.polling.retryDelay}") int retryDelay
    ) throws IOException {
        return new FistfulPollingEventPublisherBuilder()
                .withIdentityServiceAdapter()
                .withURI(pollingUrl.getURI())
                .withMaxQuerySize(pollingQuerySize)
                .withMaxPoolSize(pollingMaxPoolSize)
                .withPollDelay(pollingMaxDelay)
                .withEventRetryDelay(retryDelay)
                .withEventHandler(new EventSinkHandler(eventService))
                .build();
    }

    @Bean
    public EventPublisher walletEventPublisher(
            WalletEventService eventService,
            @Value("${wallet.polling.url}") Resource pollingUrl,
            @Value("${wallet.polling.querySize}") int pollingQuerySize,
            @Value("${wallet.polling.maxPoolSize}") int pollingMaxPoolSize,
            @Value("${wallet.polling.delay}") int pollingMaxDelay,
            @Value("${wallet.polling.retryDelay}") int retryDelay
    ) throws IOException {
        return new FistfulPollingEventPublisherBuilder()
                .withWalletServiceAdapter()
                .withURI(pollingUrl.getURI())
                .withMaxQuerySize(pollingQuerySize)
                .withMaxPoolSize(pollingMaxPoolSize)
                .withPollDelay(pollingMaxDelay)
                .withEventRetryDelay(retryDelay)
                .withEventHandler(new EventSinkHandler(eventService))
                .build();
    }

    @Bean
    public EventPublisher withdrawalEventPublisher(
            WithdrawalEventService eventService,
            @Value("${withdrawal.polling.url}") Resource pollingUrl,
            @Value("${withdrawal.polling.querySize}") int pollingQuerySize,
            @Value("${withdrawal.polling.maxPoolSize}") int pollingMaxPoolSize,
            @Value("${withdrawal.polling.delay}") int pollingMaxDelay,
            @Value("${withdrawal.polling.retryDelay}") int retryDelay
    ) throws IOException {
        return new FistfulPollingEventPublisherBuilder()
                .withWithdrawalServiceAdapter()
                .withURI(pollingUrl.getURI())
                .withMaxQuerySize(pollingQuerySize)
                .withMaxPoolSize(pollingMaxPoolSize)
                .withPollDelay(pollingMaxDelay)
                .withEventRetryDelay(retryDelay)
                .withEventHandler(new EventSinkHandler(eventService))
                .build();
    }

}

package com.rbkmoney.fistful.reporter.config;

import com.rbkmoney.eventstock.client.EventPublisher;
import com.rbkmoney.eventstock.client.poll.FistfulPollingEventPublisherBuilder;
import com.rbkmoney.fistful.reporter.config.properties.*;
import com.rbkmoney.fistful.reporter.handler.EventSinkHandler;
import com.rbkmoney.fistful.reporter.service.impl.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class EventStockPollerConfig {

    @Bean
    public EventPublisher depositEventPublisher(DepositEventService eventService,
                                                DepositProperties depositProperties) throws IOException {
        return new FistfulPollingEventPublisherBuilder()
                .withDepositServiceAdapter()
                .withURI(depositProperties.getUrl().getURI())
                .withMaxQuerySize(depositProperties.getQuerySize())
                .withMaxPoolSize(depositProperties.getMaxPoolSize())
                .withPollDelay(depositProperties.getDelay())
                .withEventRetryDelay(depositProperties.getRetryDelay())
                .withEventHandler(new EventSinkHandler(eventService))
                .build();
    }

    @Bean
    public EventPublisher destinationEventPublisher(DestinationEventService eventService,
                                                    DestinationProperties destinationProperties) throws IOException {
        return new FistfulPollingEventPublisherBuilder()
                .withDestinationServiceAdapter()
                .withURI(destinationProperties.getUrl().getURI())
                .withMaxQuerySize(destinationProperties.getQuerySize())
                .withMaxPoolSize(destinationProperties.getMaxPoolSize())
                .withPollDelay(destinationProperties.getDelay())
                .withEventRetryDelay(destinationProperties.getRetryDelay())
                .withEventHandler(new EventSinkHandler(eventService))
                .build();
    }

    @Bean
    public EventPublisher identityEventPublisher(IdentityEventService eventService,
                                                 IdentityProperties identityProperties) throws IOException {
        return new FistfulPollingEventPublisherBuilder()
                .withIdentityServiceAdapter()
                .withURI(identityProperties.getUrl().getURI())
                .withMaxQuerySize(identityProperties.getQuerySize())
                .withMaxPoolSize(identityProperties.getMaxPoolSize())
                .withPollDelay(identityProperties.getDelay())
                .withEventRetryDelay(identityProperties.getRetryDelay())
                .withEventHandler(new EventSinkHandler(eventService))
                .build();
    }

    @Bean
    public EventPublisher sourceEventPublisher(SourceEventService eventService,
                                               SourceProperties sourceProperties) throws IOException {
        return new FistfulPollingEventPublisherBuilder()
                .withSourceServiceAdapter()
                .withURI(sourceProperties.getUrl().getURI())
                .withMaxQuerySize(sourceProperties.getQuerySize())
                .withMaxPoolSize(sourceProperties.getMaxPoolSize())
                .withPollDelay(sourceProperties.getDelay())
                .withEventRetryDelay(sourceProperties.getRetryDelay())
                .withEventHandler(new EventSinkHandler(eventService))
                .build();
    }

    @Bean
    public EventPublisher walletEventPublisher(WalletEventService eventService,
                                               WalletProperties walletProperties) throws IOException {
        return new FistfulPollingEventPublisherBuilder()
                .withWalletServiceAdapter()
                .withURI(walletProperties.getUrl().getURI())
                .withMaxQuerySize(walletProperties.getQuerySize())
                .withMaxPoolSize(walletProperties.getMaxPoolSize())
                .withPollDelay(walletProperties.getDelay())
                .withEventRetryDelay(walletProperties.getRetryDelay())
                .withEventHandler(new EventSinkHandler(eventService))
                .build();
    }

    @Bean
    public EventPublisher withdrawalEventPublisher(WithdrawalEventService eventService,
                                                   WithdrawalProperties withdrawalProperties) throws IOException {
        return new FistfulPollingEventPublisherBuilder()
                .withWithdrawalServiceAdapter()
                .withURI(withdrawalProperties.getUrl().getURI())
                .withMaxQuerySize(withdrawalProperties.getQuerySize())
                .withMaxPoolSize(withdrawalProperties.getMaxPoolSize())
                .withPollDelay(withdrawalProperties.getDelay())
                .withEventRetryDelay(withdrawalProperties.getRetryDelay())
                .withEventHandler(new EventSinkHandler(eventService))
                .build();
    }
}

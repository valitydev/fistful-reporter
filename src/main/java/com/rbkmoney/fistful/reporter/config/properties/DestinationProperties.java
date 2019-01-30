package com.rbkmoney.fistful.reporter.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "destination.polling")
public class DestinationProperties extends PollingProperties {
}

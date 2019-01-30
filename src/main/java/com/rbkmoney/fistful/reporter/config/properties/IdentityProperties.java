package com.rbkmoney.fistful.reporter.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "identity.polling")
public class IdentityProperties extends PollingProperties {
}

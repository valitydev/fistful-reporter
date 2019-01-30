package com.rbkmoney.fistful.reporter.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "withdrawal.polling")
public class WithdrawalProperties extends PollingProperties {
}

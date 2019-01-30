package com.rbkmoney.fistful.reporter.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

@Configuration
@ConfigurationProperties(prefix = "partymanagement")
@Data
public class PartyManagementProperties {

    private Resource url;
    private int timeout;

}

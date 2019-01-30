package com.rbkmoney.fistful.reporter.config;

import com.rbkmoney.damsel.payment_processing.PartyManagementSrv;
import com.rbkmoney.fistful.reporter.config.properties.PartyManagementProperties;
import com.rbkmoney.woody.thrift.impl.http.THSpawnClientBuilder;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class ApplicationConfig {

    @Bean
    public PartyManagementSrv.Iface partyManagementClient(PartyManagementProperties partyManagementProperties) throws IOException {
        return new THSpawnClientBuilder()
                .withAddress(partyManagementProperties.getUrl().getURI())
                .withNetworkTimeout(partyManagementProperties.getTimeout())
                .build(PartyManagementSrv.Iface.class);
    }

    @Bean
    public HttpClient httpClient() {
        return HttpClientBuilder.create().build();
    }
}

package dev.vality.fistful.reporter.config;

import dev.vality.damsel.payment_processing.PartyManagementSrv;
import dev.vality.file.storage.FileStorageSrv;
import dev.vality.fistful.reporter.config.properties.FileStorageProperties;
import dev.vality.fistful.reporter.config.properties.PartyManagementProperties;
import dev.vality.woody.thrift.impl.http.THSpawnClientBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class ClientConfig {

    @Bean
    public PartyManagementSrv.Iface partyManagementClient(
            PartyManagementProperties partyManagementProperties) throws IOException {
        return new THSpawnClientBuilder()
                .withAddress(partyManagementProperties.getUrl().getURI())
                .withNetworkTimeout(partyManagementProperties.getTimeout())
                .build(PartyManagementSrv.Iface.class);
    }

    @Bean
    public CloseableHttpClient httpClient() {
        return HttpClients.createDefault();
    }

    @Bean
    public FileStorageSrv.Iface fileStorageClient(FileStorageProperties fileStorageProperties) throws IOException {
        return new THSpawnClientBuilder()
                .withAddress(fileStorageProperties.getUrl().getURI())
                .withNetworkTimeout(fileStorageProperties.getClientTimeout())
                .build(FileStorageSrv.Iface.class);
    }
}

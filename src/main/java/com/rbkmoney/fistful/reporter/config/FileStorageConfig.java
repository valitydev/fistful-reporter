package com.rbkmoney.fistful.reporter.config;

import com.rbkmoney.file.storage.FileStorageSrv;
import com.rbkmoney.fistful.reporter.config.properties.FileStorageProperties;
import com.rbkmoney.woody.thrift.impl.http.THSpawnClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class FileStorageConfig {

    @Bean
    public FileStorageSrv.Iface fileStorageClient(FileStorageProperties fileStorageProperties) throws IOException {
        return new THSpawnClientBuilder()
                .withAddress(fileStorageProperties.getUrl().getURI())
                .withNetworkTimeout(fileStorageProperties.getClientTimeout())
                .build(FileStorageSrv.Iface.class);
    }
}

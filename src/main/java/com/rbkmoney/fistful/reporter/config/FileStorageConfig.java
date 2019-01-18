package com.rbkmoney.fistful.reporter.config;

import com.rbkmoney.file.storage.FileStorageSrv;
import com.rbkmoney.woody.thrift.impl.http.THSpawnClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.io.IOException;

@Configuration
public class FileStorageConfig {

    @Bean
    public FileStorageSrv.Iface fileStorageClient(
            @Value("${fileStorage.url}") Resource resource,
            @Value("${fileStorage.clientTimeout}") int timeout
    ) throws IOException {
        return new THSpawnClientBuilder()
                .withAddress(resource.getURI())
                .withNetworkTimeout(timeout)
                .build(FileStorageSrv.Iface.class);
    }
}

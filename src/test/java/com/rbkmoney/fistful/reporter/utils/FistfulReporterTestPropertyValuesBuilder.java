package com.rbkmoney.fistful.reporter.utils;

import com.rbkmoney.TestContainers;
import org.springframework.boot.test.util.TestPropertyValues;

import java.util.ArrayList;
import java.util.List;

public class FistfulReporterTestPropertyValuesBuilder {

    public static TestPropertyValues build(TestContainers testContainers) {
        List<String> strings = new ArrayList<>();
        testContainers.getPostgresSQLTestContainer().ifPresent(
                c -> {
                    strings.add("spring.datasource.url=" + c.getJdbcUrl());
                    strings.add("spring.datasource.username=" + c.getUsername());
                    strings.add("spring.datasource.password=" + c.getPassword());
                    strings.add("flyway.url=" + c.getJdbcUrl());
                    strings.add("flyway.user=" + c.getUsername());
                    strings.add("flyway.password=" + c.getPassword());
                }
        );
        testContainers.getCephTestContainer().ifPresent(
                c -> strings.add("filestorage.cephEndpoint=" + c.getContainerIpAddress() + ":" + c.getMappedPort(80))
        );
        testContainers.getFileStorageTestContainer().ifPresent(
                c -> {
                    strings.add("filestorage.url=http://" + c.getContainerIpAddress() + ":" + c.getMappedPort(8022) + "/file_storage");
                    strings.add("filestorage.healthCheckUrl=http://" + c.getContainerIpAddress() + ":" + c.getMappedPort(8022) + "/actuator/health");
                }
        );


        // with local containers in docker-compose-dev.yml
        // withoutUsingTestContainers(strings);

        strings.add("eventstock.pollingEnable=false");
        strings.add("reporting.pollingEnable=false");
        return TestPropertyValues.of(strings);
    }

    private static void withoutUsingTestContainers(List<String> strings) {
        strings.add("filestorage.cephEndpoint=localhost:42827");
        strings.add("filestorage.url=http://localhost:42826/file_storage");
        strings.add("filestorage.healthCheckUrl=http://localhost:42826/actuator/health");
    }
}

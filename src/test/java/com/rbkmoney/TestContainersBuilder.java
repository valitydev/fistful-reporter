package com.rbkmoney;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.HttpWaitStrategy;
import org.testcontainers.containers.wait.strategy.WaitStrategy;

import java.time.Duration;

public class TestContainersBuilder {

    private static final String SIGNING_REGION = "RU";
    private static final String AWS_ACCESS_KEY = "test";
    private static final String AWS_SECRET_KEY = "test";
    private static final String PROTOCOL = "HTTP";
    private static final String MAX_ERROR_RETRY = "10";
    private static final String BUCKET_NAME = "TEST";

    private static boolean networkEnable;
    private static boolean postgreSQLTestContainerEnable;
    private static boolean cephTestContainerEnable;
    private static boolean fileStorageTestContainerEnable;

    private TestContainersBuilder() {
    }

    public static TestContainersBuilder builder() {
        return new TestContainersBuilder();
    }

    public TestContainersBuilder addPostgreSQLTestContainer() {
        postgreSQLTestContainerEnable = true;
        return this;
    }

    public TestContainersBuilder addCephTestContainer() {
        cephTestContainerEnable = true;
        networkEnable = true;
        return this;
    }

    public TestContainersBuilder addFileStorageTestContainer() {
        fileStorageTestContainerEnable = true;
        networkEnable = true;
        return this;
    }

    public TestContainers build() {
        Network.NetworkImpl nt = Network.builder().build();

        TestContainers testContainers = new TestContainers();
        if (networkEnable) {
            testContainers.setNetwork(nt);
        }
        if (postgreSQLTestContainerEnable) {
            testContainers.setPostgresSQLTestContainer(
                    new PostgreSQLContainer<>("postgres:9.6")
                            .withStartupTimeout(Duration.ofMinutes(5))
            );
        }
        if (cephTestContainerEnable && networkEnable) {
            testContainers.setCephTestContainer(
                    new GenericContainer<>("dr.rbkmoney.com/ceph-demo:latest")
                            .withEnv("RGW_NAME", "localhost")
                            .withEnv("NETWORK_AUTO_DETECT", "4")
                            .withEnv("CEPH_DEMO_UID", "ceph-test")
                            .withEnv("CEPH_DEMO_ACCESS_KEY", AWS_ACCESS_KEY)
                            .withEnv("CEPH_DEMO_SECRET_KEY", AWS_SECRET_KEY)
                            .withEnv("CEPH_DEMO_BUCKET", BUCKET_NAME)
                            .withExposedPorts(5000, 80)
                            .withNetwork(nt)
                            .withNetworkAliases("ceph-test-container")
                            .waitingFor(getWaitStrategy("/api/v0.1/health"))
            );
        }
        if (fileStorageTestContainerEnable && networkEnable) {
            testContainers.setFileStorageTestContainer(
                    new GenericContainer<>("dr.rbkmoney.com/rbkmoney/file-storage:0d66e41b5c6200bb2101929f6c03b7430fa98958")
                            .withEnv("storage.endpoint", "http://ceph-test-container:80")
                            .withEnv("storage.signingRegion", SIGNING_REGION)
                            .withEnv("storage.accessKey", AWS_ACCESS_KEY)
                            .withEnv("storage.secretKey", AWS_SECRET_KEY)
                            .withEnv("storage.clientProtocol", PROTOCOL)
                            .withEnv("storage.clientMaxErrorRetry", MAX_ERROR_RETRY)
                            .withEnv("storage.bucketName", BUCKET_NAME)
                            .withEnv("server.port", "8022")
                            .withExposedPorts(8022)
                            .withNetwork(nt)
                            .withNetworkAliases("file-storage-test-container")
                            .waitingFor(getWaitStrategy("/actuator/health"))
            );
        }
        return testContainers;
    }

    private WaitStrategy getWaitStrategy(String path) {
        return new HttpWaitStrategy()
                .forPath(path)
                .forStatusCode(200)
                .withStartupTimeout(Duration.ofMinutes(10));
    }
}

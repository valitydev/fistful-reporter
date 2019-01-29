package com.rbkmoney;

import lombok.NoArgsConstructor;
import lombok.Setter;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.PostgreSQLContainer;

import java.util.Optional;

@NoArgsConstructor
@Setter
public class TestContainers {

    private Network.NetworkImpl network;
    private PostgreSQLContainer postgresSQLTestContainer;
    private GenericContainer cephTestContainer;
    private GenericContainer fileStorageTestContainer;

    public Optional<Network.NetworkImpl> getNetwork() {
        return Optional.ofNullable(network);
    }

    public Optional<PostgreSQLContainer> getPostgresSQLTestContainer() {
        return Optional.ofNullable(postgresSQLTestContainer);
    }

    public Optional<GenericContainer> getCephTestContainer() {
        return Optional.ofNullable(cephTestContainer);
    }

    public Optional<GenericContainer> getFileStorageTestContainer() {
        return Optional.ofNullable(fileStorageTestContainer);
    }

    public void startTestContainers() {
        getPostgresSQLTestContainer().ifPresent(GenericContainer::start);
        getCephTestContainer().ifPresent(GenericContainer::start);
        getFileStorageTestContainer().ifPresent(GenericContainer::start);
    }

    public void stopTestContainers() {
        getPostgresSQLTestContainer().ifPresent(GenericContainer::stop);
        getFileStorageTestContainer().ifPresent(GenericContainer::stop);
        getCephTestContainer().ifPresent(GenericContainer::stop);

        //network last for close
        getNetwork().ifPresent(Network.NetworkImpl::close);
    }
}

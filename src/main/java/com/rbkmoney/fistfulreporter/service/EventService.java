package com.rbkmoney.fistfulreporter.service;

import com.rbkmoney.fistfulreporter.exception.StorageException;

import java.util.Optional;

public interface EventService<E> {

    void processSinkEvent(E event) throws StorageException;

    Optional<Long> getLastEventId() throws StorageException;

}

package com.rbkmoney.fistfulreporter.service;

import java.util.Optional;

public interface EventService<E> {

    void processSinkEvent(E event);

    Optional<Long> getLastEventId();

}

package com.rbkmoney.fistful.reporter.dao;

import java.util.Optional;

public interface FistfulDao<T> extends EventDao {

    Optional<Long> save(T object);

    T get(String id);

    void updateNotCurrent(Long id);
}

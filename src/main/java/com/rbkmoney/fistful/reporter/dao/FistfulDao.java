package com.rbkmoney.fistful.reporter.dao;

import com.rbkmoney.fistful.reporter.exception.DaoException;

public interface FistfulDao<T> extends EventDao {

    Long save(T object) throws DaoException;

    T get(String id) throws DaoException;

    void updateNotCurrent(String id) throws DaoException;
}

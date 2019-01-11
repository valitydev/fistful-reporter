package com.rbkmoney.fistfulreporter.dao;

import com.rbkmoney.fistfulreporter.exception.DaoException;

public interface FistfulDao<T> extends EventDao {

    Long save(T object) throws DaoException;

    T get(String id) throws DaoException;

    void updateNotCurrent(String id) throws DaoException;
}

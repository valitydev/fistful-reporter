package com.rbkmoney.fistfulreporter.dao;

import com.rbkmoney.fistfulreporter.exception.DaoException;

import java.util.Optional;

public interface EventDao extends GenericDao {

    Optional<Long> getLastEventId() throws DaoException;

}

package com.rbkmoney.fistful.reporter.dao;

import com.rbkmoney.fistful.reporter.exception.DaoException;

import java.util.Optional;

public interface EventDao extends GenericDao {

    Optional<Long> getLastEventId() throws DaoException;

}

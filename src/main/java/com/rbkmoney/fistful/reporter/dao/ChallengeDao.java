package com.rbkmoney.fistful.reporter.dao;

import com.rbkmoney.fistful.reporter.domain.tables.pojos.Challenge;
import com.rbkmoney.fistful.reporter.exception.DaoException;

public interface ChallengeDao extends GenericDao {

    Long save(Challenge challenge) throws DaoException;

    Challenge get(String identityId, String challengeId) throws DaoException;

    void updateNotCurrent(String identityId, String challengeId) throws DaoException;

}

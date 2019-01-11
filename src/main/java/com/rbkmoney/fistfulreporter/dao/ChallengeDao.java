package com.rbkmoney.fistfulreporter.dao;

import com.rbkmoney.fistfulreporter.domain.tables.pojos.Challenge;
import com.rbkmoney.fistfulreporter.exception.DaoException;

public interface ChallengeDao extends GenericDao {

    Long save(Challenge challenge) throws DaoException;

    Challenge get(String identityId, String challengeId) throws DaoException;

    void updateNotCurrent(String identityId, String challengeId) throws DaoException;

}

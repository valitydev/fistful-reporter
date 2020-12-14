package com.rbkmoney.fistful.reporter.dao;

import com.rbkmoney.dao.GenericDao;
import com.rbkmoney.fistful.reporter.domain.tables.pojos.Challenge;

import java.util.Optional;

public interface ChallengeDao extends GenericDao {

    Optional<Long> save(Challenge challenge);

    Challenge get(String identityId, String challengeId);

    void updateNotCurrent(Long challengeId);

}

package dev.vality.fistful.reporter.dao;

import dev.vality.dao.GenericDao;
import dev.vality.fistful.reporter.domain.tables.pojos.Challenge;

import java.util.Optional;

public interface ChallengeDao extends GenericDao {

    Optional<Long> save(Challenge challenge);

    Challenge get(String identityId, String challengeId);

    void updateNotCurrent(Long challengeId);

}

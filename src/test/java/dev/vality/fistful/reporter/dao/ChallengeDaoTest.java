package dev.vality.fistful.reporter.dao;

import dev.vality.fistful.reporter.config.PostgresqlSpringBootITest;
import dev.vality.fistful.reporter.domain.tables.pojos.Challenge;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static dev.vality.testcontainers.annotations.util.RandomBeans.random;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@PostgresqlSpringBootITest
public class ChallengeDaoTest {

    @Autowired
    private ChallengeDao challengeDao;

    @Test
    public void challengeDaoTest() {
        Challenge challenge = random(Challenge.class);
        challenge.setCurrent(true);
        Long id = challengeDao.save(challenge).get();
        challenge.setId(id);
        assertEquals(challenge, challengeDao.get(challenge.getIdentityId(), challenge.getChallengeId()));
        challengeDao.updateNotCurrent(challenge.getId());
        assertNull(challengeDao.get(challenge.getIdentityId(), challenge.getChallengeId()));
    }
}

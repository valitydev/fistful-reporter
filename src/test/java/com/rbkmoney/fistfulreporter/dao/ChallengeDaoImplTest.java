package com.rbkmoney.fistfulreporter.dao;

import com.rbkmoney.fistfulreporter.AbstractIntegrationTest;
import com.rbkmoney.fistfulreporter.domain.tables.pojos.Challenge;
import com.rbkmoney.fistfulreporter.exception.DaoException;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class ChallengeDaoImplTest extends AbstractIntegrationTest {

    @Autowired
    private ChallengeDao challengeDao;

    @Test
    public void test() throws DaoException {
        Challenge challenge = random(Challenge.class);
        challenge.setCurrent(true);
        Long id = challengeDao.save(challenge);
        challenge.setId(id);
        assertEquals(challenge, challengeDao.get(challenge.getIdentityId(), challenge.getChallengeId()));
        challengeDao.updateNotCurrent(challenge.getIdentityId(), challenge.getChallengeId());
        assertNull(challengeDao.get(challenge.getIdentityId(), challenge.getChallengeId()));
    }
}

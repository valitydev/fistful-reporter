package com.rbkmoney.fistfulreporter.service.impl;

import com.rbkmoney.fistful.identity.*;
import com.rbkmoney.fistfulreporter.AbstractIntegrationTest;
import com.rbkmoney.fistfulreporter.domain.tables.pojos.Identity;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

import java.util.Arrays;
import java.util.List;

import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static org.junit.Assert.assertEquals;

public class IdentityEventServiceTest extends AbstractIntegrationTest {

//    @MockBean
//    private IdentityDao identityDao;

    @Autowired
    private IdentityEventService eventService;

    @Before
    public void setUp() throws Exception {
//        Mockito.when(identityDao.get(Mockito.anyString())).thenReturn(random(Identity.class));
    }

    @Test
    public void test() {
        String identityId = generateString();
        String challengeId = generateString();

        List<Change> changes = Arrays.asList(
                createCreatedChange(),
                createLevelChangedChange(),
                createChallengeCreatedChange(challengeId),
                createChallengeStatusChangedChange(challengeId),
                createEffectiveChallengeChangedChange()
        );

        Event event = new Event(generateInt(), generateDate(), changes);

        SinkEvent sinkEvent = new SinkEvent(
                generateLong(),
                generateDate(),
                identityId,
                event
        );

        eventService.processSinkEvent(sinkEvent);

        List<Identity> identities = jdbcTemplate.query(
                "SELECT * FROM fr.identity AS identity WHERE identity.identity_id = ?",
                new Object[]{identityId},
                new BeanPropertyRowMapper<>(Identity.class)
        );
        assertEquals(5, identities.size());

        identities = jdbcTemplate.query(
                "SELECT * FROM fr.identity AS identity WHERE identity.identity_id = ? AND identity.current",
                new Object[]{identityId},
                new BeanPropertyRowMapper<>(Identity.class)
        );
        assertEquals(1, identities.size());
    }

    private Change createCreatedChange() {
        return Change.created(random(com.rbkmoney.fistful.identity.Identity.class));
    }

    private Change createLevelChangedChange() {
        return Change.level_changed(generateString());
    }

    private Change createChallengeCreatedChange(String challengeId) {
        return Change.identity_challenge(
                new ChallengeChange(
                        challengeId,
                        ChallengeChangePayload.created(random(Challenge.class))
                )
        );
    }

    private Change createChallengeStatusChangedChange(String challengeId) {
        return Change.identity_challenge(
                new ChallengeChange(
                        challengeId,
                        ChallengeChangePayload.status_changed(
                                ChallengeStatus.cancelled(new ChallengeCancelled())
                        )
                )
        );
    }

    private Change createEffectiveChallengeChangedChange() {
        return Change.effective_challenge_changed(generateString());
    }
}
package com.rbkmoney.fistfulreporter.dao.impl;

import com.rbkmoney.fistfulreporter.dao.ChallengeDao;
import com.rbkmoney.fistfulreporter.dao.mapper.RecordRowMapper;
import com.rbkmoney.fistfulreporter.domain.tables.pojos.Challenge;
import com.rbkmoney.fistfulreporter.domain.tables.records.ChallengeRecord;
import com.rbkmoney.fistfulreporter.exception.DaoException;
import org.jooq.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

import static com.rbkmoney.fistfulreporter.domain.tables.Challenge.CHALLENGE;

@Component
public class ChallengeDaoImpl extends AbstractGenericDao implements ChallengeDao {

    private final RowMapper<Challenge> challengeRowMapper;

    @Autowired
    public ChallengeDaoImpl(DataSource dataSource) {
        super(dataSource);
        challengeRowMapper = new RecordRowMapper<>(CHALLENGE, Challenge.class);
    }

    @Override
    public Long save(Challenge challenge) throws DaoException {
        ChallengeRecord record = getDslContext().newRecord(CHALLENGE, challenge);
        Query query = getDslContext().insertInto(CHALLENGE).set(record).returning(CHALLENGE.ID);

        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        executeOne(query, keyHolder);
        return keyHolder.getKey().longValue();
    }

    @Override
    public Challenge get(String identityId, String challengeId) throws DaoException {
        Query query = getDslContext().selectFrom(CHALLENGE)
                .where(
                        CHALLENGE.IDENTITY_ID.eq(identityId)
                                .and(CHALLENGE.CHALLENGE_ID.eq(challengeId))
                                .and(CHALLENGE.CURRENT)
                );

        return fetchOne(query, challengeRowMapper);
    }

    @Override
    public void updateNotCurrent(String identityId, String challengeId) throws DaoException {
        Query query = getDslContext().update(CHALLENGE).set(CHALLENGE.CURRENT, false)
                .where(
                        CHALLENGE.IDENTITY_ID.eq(identityId)
                                .and(CHALLENGE.CHALLENGE_ID.eq(challengeId))
                                .and(CHALLENGE.CURRENT)
                );
        executeOne(query);
    }
}

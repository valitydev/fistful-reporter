package com.rbkmoney.fistful.reporter.dao.impl;

import com.rbkmoney.fistful.reporter.dao.ChallengeDao;
import com.rbkmoney.fistful.reporter.dao.mapper.RecordRowMapper;
import com.rbkmoney.fistful.reporter.domain.tables.pojos.Challenge;
import com.rbkmoney.fistful.reporter.domain.tables.records.ChallengeRecord;
import com.rbkmoney.fistful.reporter.exception.DaoException;
import org.jooq.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component
public class ChallengeDaoImpl extends AbstractGenericDao implements ChallengeDao {

    private final RowMapper<Challenge> challengeRowMapper;

    @Autowired
    public ChallengeDaoImpl(DataSource dataSource) {
        super(dataSource);
        challengeRowMapper = new RecordRowMapper<>(com.rbkmoney.fistful.reporter.domain.tables.Challenge.CHALLENGE, Challenge.class);
    }

    @Override
    public Long save(Challenge challenge) throws DaoException {
        ChallengeRecord record = getDslContext().newRecord(com.rbkmoney.fistful.reporter.domain.tables.Challenge.CHALLENGE, challenge);
        Query query = getDslContext().insertInto(com.rbkmoney.fistful.reporter.domain.tables.Challenge.CHALLENGE).set(record).returning(com.rbkmoney.fistful.reporter.domain.tables.Challenge.CHALLENGE.ID);

        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        executeOne(query, keyHolder);
        return keyHolder.getKey().longValue();
    }

    @Override
    public Challenge get(String identityId, String challengeId) throws DaoException {
        Query query = getDslContext().selectFrom(com.rbkmoney.fistful.reporter.domain.tables.Challenge.CHALLENGE)
                .where(
                        com.rbkmoney.fistful.reporter.domain.tables.Challenge.CHALLENGE.IDENTITY_ID.eq(identityId)
                                .and(com.rbkmoney.fistful.reporter.domain.tables.Challenge.CHALLENGE.CHALLENGE_ID.eq(challengeId))
                                .and(com.rbkmoney.fistful.reporter.domain.tables.Challenge.CHALLENGE.CURRENT)
                );

        return fetchOne(query, challengeRowMapper);
    }

    @Override
    public void updateNotCurrent(String identityId, String challengeId) throws DaoException {
        Query query = getDslContext().update(com.rbkmoney.fistful.reporter.domain.tables.Challenge.CHALLENGE).set(com.rbkmoney.fistful.reporter.domain.tables.Challenge.CHALLENGE.CURRENT, false)
                .where(
                        com.rbkmoney.fistful.reporter.domain.tables.Challenge.CHALLENGE.IDENTITY_ID.eq(identityId)
                                .and(com.rbkmoney.fistful.reporter.domain.tables.Challenge.CHALLENGE.CHALLENGE_ID.eq(challengeId))
                                .and(com.rbkmoney.fistful.reporter.domain.tables.Challenge.CHALLENGE.CURRENT)
                );
        executeOne(query);
    }
}

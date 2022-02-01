package dev.vality.fistful.reporter.dao.impl;

import com.zaxxer.hikari.HikariDataSource;
import dev.vality.dao.impl.AbstractGenericDao;
import dev.vality.fistful.reporter.dao.ChallengeDao;
import dev.vality.fistful.reporter.dao.mapper.RecordRowMapper;
import dev.vality.fistful.reporter.domain.tables.pojos.Challenge;
import dev.vality.fistful.reporter.domain.tables.records.ChallengeRecord;
import org.jooq.Condition;
import org.jooq.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static dev.vality.fistful.reporter.domain.tables.Challenge.CHALLENGE;

@Component
public class ChallengeDaoImpl extends AbstractGenericDao implements ChallengeDao {

    private final RowMapper<Challenge> challengeRowMapper;

    @Autowired
    public ChallengeDaoImpl(HikariDataSource dataSource) {
        super(dataSource);
        challengeRowMapper = new RecordRowMapper<>(CHALLENGE, Challenge.class);
    }

    @Override
    public Optional<Long> save(Challenge challenge) {
        ChallengeRecord record = getDslContext().newRecord(CHALLENGE, challenge);
        Query query = getDslContext()
                .insertInto(CHALLENGE)
                .set(record)
                .onConflict(CHALLENGE.CHALLENGE_ID, CHALLENGE.IDENTITY_ID, CHALLENGE.EVENT_ID)
                .doNothing()
                .returning(CHALLENGE.ID);

        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        execute(query, keyHolder);
        return Optional.ofNullable(keyHolder.getKey()).map(Number::longValue);
    }

    @Override
    public Challenge get(String identityId, String challengeId) {
        Condition condition = CHALLENGE.IDENTITY_ID.eq(identityId)
                .and(CHALLENGE.CHALLENGE_ID.eq(challengeId))
                .and(CHALLENGE.CURRENT);
        Query query = getDslContext().selectFrom(CHALLENGE).where(condition);

        return fetchOne(query, challengeRowMapper);
    }

    @Override
    public void updateNotCurrent(Long challengeId) {
        Query query = getDslContext()
                .update(CHALLENGE)
                .set(CHALLENGE.CURRENT, false)
                .where(CHALLENGE.ID.eq(challengeId)
                        .and(CHALLENGE.CURRENT));
        execute(query);
    }
}

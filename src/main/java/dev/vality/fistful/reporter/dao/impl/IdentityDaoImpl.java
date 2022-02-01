package dev.vality.fistful.reporter.dao.impl;

import com.zaxxer.hikari.HikariDataSource;
import dev.vality.dao.impl.AbstractGenericDao;
import dev.vality.fistful.reporter.dao.IdentityDao;
import dev.vality.fistful.reporter.dao.mapper.RecordRowMapper;
import dev.vality.fistful.reporter.domain.tables.pojos.Identity;
import dev.vality.fistful.reporter.domain.tables.records.IdentityRecord;
import org.jooq.Condition;
import org.jooq.Query;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static dev.vality.fistful.reporter.domain.tables.Challenge.CHALLENGE;
import static dev.vality.fistful.reporter.domain.tables.Identity.IDENTITY;

@Component
public class IdentityDaoImpl extends AbstractGenericDao implements IdentityDao {

    private static final String EVENT_ID = "event_id";

    private final RowMapper<Identity> identityRowMapper;

    @Autowired
    public IdentityDaoImpl(HikariDataSource dataSource) {
        super(dataSource);
        identityRowMapper = new RecordRowMapper<>(IDENTITY, Identity.class);
    }

    @Override
    public Optional<Long> getLastEventId() {
        Query query = getDslContext().select(DSL.max(DSL.field(EVENT_ID)))
                .from(
                        getDslContext().select(DSL.max(IDENTITY.EVENT_ID).as(EVENT_ID))
                                .from(IDENTITY)
                                .unionAll(
                                        getDslContext().select(DSL.max(CHALLENGE.EVENT_ID).as(EVENT_ID))
                                                .from(CHALLENGE)
                                )
                );

        return Optional.ofNullable(fetchOne(query, Long.class));
    }

    @Override
    public Optional<Long> save(Identity identity) {
        IdentityRecord record = getDslContext().newRecord(IDENTITY, identity);
        Query query = getDslContext()
                .insertInto(IDENTITY)
                .set(record)
                .onConflict(IDENTITY.IDENTITY_ID, IDENTITY.EVENT_ID)
                .doNothing()
                .returning(IDENTITY.ID);

        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        execute(query, keyHolder);
        return Optional.ofNullable(keyHolder.getKey()).map(Number::longValue);
    }

    @Override
    public Identity get(String identityId) {
        Condition condition = IDENTITY.IDENTITY_ID.eq(identityId)
                .and(IDENTITY.CURRENT);
        Query query = getDslContext().selectFrom(IDENTITY).where(condition);

        return fetchOne(query, identityRowMapper);
    }

    @Override
    public void updateNotCurrent(Long identityId) {
        Query query = getDslContext()
                .update(IDENTITY)
                .set(IDENTITY.CURRENT, false)
                .where(IDENTITY.ID.eq(identityId)
                        .and(IDENTITY.CURRENT)
                );
        execute(query);
    }
}

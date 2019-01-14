package com.rbkmoney.fistful.reporter.dao.impl;

import com.rbkmoney.fistful.reporter.dao.IdentityDao;
import com.rbkmoney.fistful.reporter.dao.mapper.RecordRowMapper;
import com.rbkmoney.fistful.reporter.domain.tables.Challenge;
import com.rbkmoney.fistful.reporter.domain.tables.pojos.Identity;
import com.rbkmoney.fistful.reporter.domain.tables.records.IdentityRecord;
import com.rbkmoney.fistful.reporter.exception.DaoException;
import org.jooq.Query;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.Optional;

@Component
public class IdentityDaoImpl extends AbstractGenericDao implements IdentityDao {

    private final RowMapper<Identity> identityRowMapper;

    @Autowired
    public IdentityDaoImpl(DataSource dataSource) {
        super(dataSource);
        identityRowMapper = new RecordRowMapper<>(com.rbkmoney.fistful.reporter.domain.tables.Identity.IDENTITY, Identity.class);
    }

    @Override
    public Optional<Long> getLastEventId() throws DaoException {
        String eventId = "event_id";
        Query query = getDslContext().select(DSL.max(DSL.field(eventId))).from(
                getDslContext().select(com.rbkmoney.fistful.reporter.domain.tables.Identity.IDENTITY.EVENT_ID.max().as(eventId)).from(com.rbkmoney.fistful.reporter.domain.tables.Identity.IDENTITY)
                        .unionAll(getDslContext().select(Challenge.CHALLENGE.EVENT_ID.max().as(eventId)).from(Challenge.CHALLENGE))
        );
        return Optional.ofNullable(fetchOne(query, Long.class));
    }

    @Override
    public Long save(Identity identity) throws DaoException {
        IdentityRecord record = getDslContext().newRecord(com.rbkmoney.fistful.reporter.domain.tables.Identity.IDENTITY, identity);
        Query query = getDslContext().insertInto(com.rbkmoney.fistful.reporter.domain.tables.Identity.IDENTITY).set(record).returning(com.rbkmoney.fistful.reporter.domain.tables.Identity.IDENTITY.ID);

        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        executeOne(query, keyHolder);
        return keyHolder.getKey().longValue();
    }

    @Override
    public Identity get(String identityId) throws DaoException {
        Query query = getDslContext().selectFrom(com.rbkmoney.fistful.reporter.domain.tables.Identity.IDENTITY)
                .where(
                        com.rbkmoney.fistful.reporter.domain.tables.Identity.IDENTITY.IDENTITY_ID.eq(identityId)
                                .and(com.rbkmoney.fistful.reporter.domain.tables.Identity.IDENTITY.CURRENT)
                );

        return fetchOne(query, identityRowMapper);
    }

    @Override
    public void updateNotCurrent(String identityId) throws DaoException {
        Query query = getDslContext().update(com.rbkmoney.fistful.reporter.domain.tables.Identity.IDENTITY).set(com.rbkmoney.fistful.reporter.domain.tables.Identity.IDENTITY.CURRENT, false)
                .where(
                        com.rbkmoney.fistful.reporter.domain.tables.Identity.IDENTITY.IDENTITY_ID.eq(identityId)
                                .and(com.rbkmoney.fistful.reporter.domain.tables.Identity.IDENTITY.CURRENT)
                );
        executeOne(query);
    }
}

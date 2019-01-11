package com.rbkmoney.fistfulreporter.dao.impl;

import com.rbkmoney.fistfulreporter.dao.IdentityDao;
import com.rbkmoney.fistfulreporter.dao.mapper.RecordRowMapper;
import com.rbkmoney.fistfulreporter.domain.tables.pojos.Identity;
import com.rbkmoney.fistfulreporter.domain.tables.records.IdentityRecord;
import com.rbkmoney.fistfulreporter.exception.DaoException;
import org.jooq.Query;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.Optional;

import static com.rbkmoney.fistfulreporter.domain.tables.Challenge.CHALLENGE;
import static com.rbkmoney.fistfulreporter.domain.tables.Identity.IDENTITY;

@Component
public class IdentityDaoImpl extends AbstractGenericDao implements IdentityDao {

    private final RowMapper<Identity> identityRowMapper;

    @Autowired
    public IdentityDaoImpl(DataSource dataSource) {
        super(dataSource);
        identityRowMapper = new RecordRowMapper<>(IDENTITY, Identity.class);
    }

    @Override
    public Optional<Long> getLastEventId() throws DaoException {
        Query query = getDslContext().select(DSL.max(DSL.field("event_id"))).from(
                getDslContext().select(IDENTITY.EVENT_ID.max().as("event_id")).from(IDENTITY)
                        .unionAll(getDslContext().select(CHALLENGE.EVENT_ID.max().as("event_id")).from(CHALLENGE))
        );
        return Optional.ofNullable(fetchOne(query, Long.class));
    }

    @Override
    public Long save(Identity identity) throws DaoException {
        IdentityRecord record = getDslContext().newRecord(IDENTITY, identity);
        Query query = getDslContext().insertInto(IDENTITY).set(record).returning(IDENTITY.ID);

        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        executeOne(query, keyHolder);
        return keyHolder.getKey().longValue();
    }

    @Override
    public Identity get(String identityId) throws DaoException {
        Query query = getDslContext().selectFrom(IDENTITY)
                .where(
                        IDENTITY.IDENTITY_ID.eq(identityId)
                                .and(IDENTITY.CURRENT)
                );

        return fetchOne(query, identityRowMapper);
    }

    @Override
    public void updateNotCurrent(String identityId) throws DaoException {
        Query query = getDslContext().update(IDENTITY).set(IDENTITY.CURRENT, false)
                .where(
                        IDENTITY.IDENTITY_ID.eq(identityId)
                                .and(IDENTITY.CURRENT)
                );
        executeOne(query);
    }
}

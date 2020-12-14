package com.rbkmoney.fistful.reporter.dao.impl;

import com.rbkmoney.dao.impl.AbstractGenericDao;
import com.rbkmoney.fistful.reporter.dao.SourceDao;
import com.rbkmoney.fistful.reporter.dao.mapper.RecordRowMapper;
import com.rbkmoney.fistful.reporter.domain.tables.pojos.Source;
import com.rbkmoney.fistful.reporter.domain.tables.records.SourceRecord;
import com.zaxxer.hikari.HikariDataSource;
import org.jooq.Condition;
import org.jooq.Query;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static com.rbkmoney.fistful.reporter.domain.tables.Identity.IDENTITY;
import static com.rbkmoney.fistful.reporter.domain.tables.Source.SOURCE;

@Component
public class SourceDaoImpl extends AbstractGenericDao implements SourceDao {

    private final RowMapper<Source> sourceRowMapper;

    @Autowired
    public SourceDaoImpl(HikariDataSource dataSource) {
        super(dataSource);
        sourceRowMapper = new RecordRowMapper<>(SOURCE, Source.class);
    }

    @Override
    public Optional<Long> getLastEventId() {
        Query query = getDslContext().select(DSL.max(SOURCE.EVENT_ID)).from(SOURCE);

        return Optional.ofNullable(fetchOne(query, Long.class));
    }

    @Override
    public Optional<Long> save(Source source) {
        SourceRecord record = getDslContext().newRecord(SOURCE, source);
        Query query = getDslContext()
                .insertInto(SOURCE)
                .set(record)
                .onConflict(SOURCE.SOURCE_ID, SOURCE.EVENT_ID)
                .doNothing()
                .returning(SOURCE.ID);

        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        execute(query, keyHolder);
        return Optional.ofNullable(keyHolder.getKey()).map(Number::longValue);
    }

    @Override
    public Source get(String sourceId) {
        Condition condition = SOURCE.SOURCE_ID.eq(sourceId)
                .and(SOURCE.CURRENT);
        Query query = getDslContext().selectFrom(SOURCE).where(condition);

        return fetchOne(query, sourceRowMapper);
    }

    @Override
    public void updateNotCurrent(Long sourceId) {
        Query query = getDslContext()
                .update(SOURCE)
                .set(SOURCE.CURRENT, false)
                .where(SOURCE.ID.eq(sourceId)
                        .and(SOURCE.CURRENT));

        execute(query);
    }
}

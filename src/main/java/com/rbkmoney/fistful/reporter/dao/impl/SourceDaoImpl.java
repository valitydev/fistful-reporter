package com.rbkmoney.fistful.reporter.dao.impl;

import com.rbkmoney.fistful.reporter.dao.SourceDao;
import com.rbkmoney.fistful.reporter.dao.mapper.RecordRowMapper;
import com.rbkmoney.fistful.reporter.domain.tables.pojos.Source;
import com.rbkmoney.fistful.reporter.domain.tables.records.SourceRecord;
import com.rbkmoney.fistful.reporter.exception.DaoException;
import org.jooq.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.Optional;

@Component
public class SourceDaoImpl extends AbstractGenericDao implements SourceDao {

    private final RowMapper<Source> sourceRowMapper;

    @Autowired
    public SourceDaoImpl(DataSource dataSource) {
        super(dataSource);
        sourceRowMapper = new RecordRowMapper<>(com.rbkmoney.fistful.reporter.domain.tables.Source.SOURCE, Source.class);
    }

    @Override
    public Optional<Long> getLastEventId() throws DaoException {
        Query query = getDslContext().select(com.rbkmoney.fistful.reporter.domain.tables.Source.SOURCE.EVENT_ID.max()).from(com.rbkmoney.fistful.reporter.domain.tables.Source.SOURCE);
        return Optional.ofNullable(fetchOne(query, Long.class));
    }

    @Override
    public Long save(Source source) throws DaoException {
        SourceRecord record = getDslContext().newRecord(com.rbkmoney.fistful.reporter.domain.tables.Source.SOURCE, source);
        Query query = getDslContext().insertInto(com.rbkmoney.fistful.reporter.domain.tables.Source.SOURCE).set(record).returning(com.rbkmoney.fistful.reporter.domain.tables.Source.SOURCE.ID);

        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        executeOne(query, keyHolder);
        return keyHolder.getKey().longValue();
    }

    @Override
    public Source get(String sourceId) throws DaoException {
        Query query = getDslContext().selectFrom(com.rbkmoney.fistful.reporter.domain.tables.Source.SOURCE)
                .where(
                        com.rbkmoney.fistful.reporter.domain.tables.Source.SOURCE.SOURCE_ID.eq(sourceId)
                                .and(com.rbkmoney.fistful.reporter.domain.tables.Source.SOURCE.CURRENT)
                );

        return fetchOne(query, sourceRowMapper);
    }

    @Override
    public void updateNotCurrent(String sourceId) throws DaoException {
        Query query = getDslContext().update(com.rbkmoney.fistful.reporter.domain.tables.Source.SOURCE).set(com.rbkmoney.fistful.reporter.domain.tables.Source.SOURCE.CURRENT, false)
                .where(
                        com.rbkmoney.fistful.reporter.domain.tables.Source.SOURCE.SOURCE_ID.eq(sourceId)
                                .and(com.rbkmoney.fistful.reporter.domain.tables.Source.SOURCE.CURRENT)
                );
        executeOne(query);
    }
}

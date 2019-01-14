package com.rbkmoney.fistful.reporter.dao.impl;

import com.rbkmoney.fistful.reporter.dao.DestinationDao;
import com.rbkmoney.fistful.reporter.dao.mapper.RecordRowMapper;
import com.rbkmoney.fistful.reporter.domain.tables.pojos.Destination;
import com.rbkmoney.fistful.reporter.domain.tables.records.DestinationRecord;
import com.rbkmoney.fistful.reporter.exception.DaoException;
import org.jooq.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.Optional;

@Component
public class DestinationDaoImpl extends AbstractGenericDao implements DestinationDao {

    private final RowMapper<Destination> destinationRowMapper;

    @Autowired
    public DestinationDaoImpl(DataSource dataSource) {
        super(dataSource);
        destinationRowMapper = new RecordRowMapper<>(com.rbkmoney.fistful.reporter.domain.tables.Destination.DESTINATION, Destination.class);
    }

    @Override
    public Optional<Long> getLastEventId() throws DaoException {
        Query query = getDslContext().select(com.rbkmoney.fistful.reporter.domain.tables.Destination.DESTINATION.EVENT_ID.max()).from(com.rbkmoney.fistful.reporter.domain.tables.Destination.DESTINATION);
        return Optional.ofNullable(fetchOne(query, Long.class));
    }

    @Override
    public Long save(Destination destination) throws DaoException {
        DestinationRecord record = getDslContext().newRecord(com.rbkmoney.fistful.reporter.domain.tables.Destination.DESTINATION, destination);
        Query query = getDslContext().insertInto(com.rbkmoney.fistful.reporter.domain.tables.Destination.DESTINATION).set(record).returning(com.rbkmoney.fistful.reporter.domain.tables.Destination.DESTINATION.ID);

        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        executeOne(query, keyHolder);
        return keyHolder.getKey().longValue();
    }

    @Override
    public Destination get(String destinationId) throws DaoException {
        Query query = getDslContext().selectFrom(com.rbkmoney.fistful.reporter.domain.tables.Destination.DESTINATION)
                .where(
                        com.rbkmoney.fistful.reporter.domain.tables.Destination.DESTINATION.DESTINATION_ID.eq(destinationId)
                                .and(com.rbkmoney.fistful.reporter.domain.tables.Destination.DESTINATION.CURRENT)
                );

        return fetchOne(query, destinationRowMapper);
    }

    @Override
    public void updateNotCurrent(String destinationId) throws DaoException {
        Query query = getDslContext().update(com.rbkmoney.fistful.reporter.domain.tables.Destination.DESTINATION).set(com.rbkmoney.fistful.reporter.domain.tables.Destination.DESTINATION.CURRENT, false)
                .where(
                        com.rbkmoney.fistful.reporter.domain.tables.Destination.DESTINATION.DESTINATION_ID.eq(destinationId)
                                .and(com.rbkmoney.fistful.reporter.domain.tables.Destination.DESTINATION.CURRENT)
                );
        executeOne(query);
    }
}

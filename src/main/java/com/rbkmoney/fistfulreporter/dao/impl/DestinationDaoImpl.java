package com.rbkmoney.fistfulreporter.dao.impl;

import com.rbkmoney.fistfulreporter.dao.DestinationDao;
import com.rbkmoney.fistfulreporter.dao.mapper.RecordRowMapper;
import com.rbkmoney.fistfulreporter.domain.tables.pojos.Destination;
import com.rbkmoney.fistfulreporter.domain.tables.records.DestinationRecord;
import com.rbkmoney.fistfulreporter.exception.DaoException;
import org.jooq.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.Optional;

import static com.rbkmoney.fistfulreporter.domain.tables.Destination.DESTINATION;

@Component
public class DestinationDaoImpl extends AbstractGenericDao implements DestinationDao {

    private final RowMapper<Destination> destinationRowMapper;

    @Autowired
    public DestinationDaoImpl(DataSource dataSource) {
        super(dataSource);
        destinationRowMapper = new RecordRowMapper<>(DESTINATION, Destination.class);
    }

    @Override
    public Optional<Long> getLastEventId() throws DaoException {
        Query query = getDslContext().select(DESTINATION.EVENT_ID.max()).from(DESTINATION);
        return Optional.ofNullable(fetchOne(query, Long.class));
    }

    @Override
    public Long save(Destination destination) throws DaoException {
        DestinationRecord record = getDslContext().newRecord(DESTINATION, destination);
        Query query = getDslContext().insertInto(DESTINATION).set(record).returning(DESTINATION.ID);

        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        executeOne(query, keyHolder);
        return keyHolder.getKey().longValue();
    }

    @Override
    public Destination get(String destinationId) throws DaoException {
        Query query = getDslContext().selectFrom(DESTINATION)
                .where(
                        DESTINATION.DESTINATION_ID.eq(destinationId)
                                .and(DESTINATION.CURRENT)
                );

        return fetchOne(query, destinationRowMapper);
    }

    @Override
    public void updateNotCurrent(String destinationId) throws DaoException {
        Query query = getDslContext().update(DESTINATION).set(DESTINATION.CURRENT, false)
                .where(
                        DESTINATION.DESTINATION_ID.eq(destinationId)
                                .and(DESTINATION.CURRENT)
                );
        executeOne(query);
    }
}

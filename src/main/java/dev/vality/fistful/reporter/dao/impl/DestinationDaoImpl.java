package dev.vality.fistful.reporter.dao.impl;

import com.zaxxer.hikari.HikariDataSource;
import dev.vality.dao.impl.AbstractGenericDao;
import dev.vality.fistful.reporter.dao.DestinationDao;
import dev.vality.fistful.reporter.dao.mapper.RecordRowMapper;
import dev.vality.fistful.reporter.domain.tables.pojos.Destination;
import dev.vality.fistful.reporter.domain.tables.records.DestinationRecord;
import org.jooq.Condition;
import org.jooq.Query;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static dev.vality.fistful.reporter.domain.tables.Destination.DESTINATION;

@Component
public class DestinationDaoImpl extends AbstractGenericDao implements DestinationDao {

    private final RowMapper<Destination> destinationRowMapper;

    @Autowired
    public DestinationDaoImpl(HikariDataSource dataSource) {
        super(dataSource);
        destinationRowMapper = new RecordRowMapper<>(DESTINATION, Destination.class);
    }

    @Override
    public Optional<Long> getLastEventId() {
        Query query = getDslContext().select(DSL.max(DESTINATION.EVENT_ID)).from(DESTINATION);

        return Optional.ofNullable(fetchOne(query, Long.class));
    }

    @Override
    public Optional<Long> save(Destination destination) {
        DestinationRecord record = getDslContext().newRecord(DESTINATION, destination);
        Query query = getDslContext()
                .insertInto(DESTINATION)
                .set(record)
                .onConflict(DESTINATION.DESTINATION_ID, DESTINATION.EVENT_ID)
                .doNothing()
                .returning(DESTINATION.ID);

        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        execute(query, keyHolder);
        return Optional.ofNullable(keyHolder.getKey()).map(Number::longValue);
    }

    @Override
    public Destination get(String destinationId) {
        Condition condition = DESTINATION.DESTINATION_ID.eq(destinationId)
                .and(DESTINATION.CURRENT);
        Query query = getDslContext().selectFrom(DESTINATION).where(condition);

        return fetchOne(query, destinationRowMapper);
    }

    @Override
    public void updateNotCurrent(Long destinationId) {
        Query query = getDslContext().update(DESTINATION)
                .set(DESTINATION.CURRENT, false)
                .where(DESTINATION.ID.eq(destinationId)
                        .and(DESTINATION.CURRENT));
        execute(query);
    }
}

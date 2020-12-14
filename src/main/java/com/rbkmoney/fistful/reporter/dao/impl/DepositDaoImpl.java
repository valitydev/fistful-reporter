package com.rbkmoney.fistful.reporter.dao.impl;

import com.rbkmoney.dao.impl.AbstractGenericDao;
import com.rbkmoney.fistful.reporter.dao.DepositDao;
import com.rbkmoney.fistful.reporter.dao.mapper.RecordRowMapper;
import com.rbkmoney.fistful.reporter.domain.tables.pojos.Deposit;
import com.rbkmoney.fistful.reporter.domain.tables.records.DepositRecord;
import com.zaxxer.hikari.HikariDataSource;
import org.jooq.Condition;
import org.jooq.Query;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static com.rbkmoney.fistful.reporter.domain.tables.Deposit.DEPOSIT;

@Component
public class DepositDaoImpl extends AbstractGenericDao implements DepositDao {

    private final RowMapper<Deposit> depositRowMapper;

    @Autowired
    public DepositDaoImpl(HikariDataSource dataSource) {
        super(dataSource);
        depositRowMapper = new RecordRowMapper<>(DEPOSIT, Deposit.class);
    }

    @Override
    public Optional<Long> getLastEventId() {
        Query query = getDslContext().select(DSL.max(DEPOSIT.EVENT_ID)).from(DEPOSIT);

        return Optional.ofNullable(fetchOne(query, Long.class));
    }

    @Override
    public Optional<Long> save(Deposit deposit) {
        DepositRecord record = getDslContext().newRecord(DEPOSIT, deposit);
        Query query = getDslContext()
                .insertInto(DEPOSIT)
                .set(record)
                .onConflict(DEPOSIT.DEPOSIT_ID, DEPOSIT.EVENT_ID)
                .doNothing()
                .returning(DEPOSIT.ID);

        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        execute(query, keyHolder);
        return Optional.ofNullable(keyHolder.getKey()).map(Number::longValue);
    }

    @Override
    public Deposit get(String depositId) {
        Condition condition = DEPOSIT.DEPOSIT_ID.eq(depositId)
                .and(DEPOSIT.CURRENT);
        Query query = getDslContext().selectFrom(DEPOSIT).where(condition);

        return fetchOne(query, depositRowMapper);
    }

    @Override
    public void updateNotCurrent(Long depositId) {
        Query query = getDslContext()
                .update(DEPOSIT)
                .set(DEPOSIT.CURRENT, false)
                .where(DEPOSIT.ID.eq(depositId)
                        .and(DEPOSIT.CURRENT));
        execute(query);
    }

}

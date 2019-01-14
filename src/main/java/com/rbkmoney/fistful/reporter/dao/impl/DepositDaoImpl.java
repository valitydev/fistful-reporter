package com.rbkmoney.fistful.reporter.dao.impl;

import com.rbkmoney.fistful.reporter.dao.DepositDao;
import com.rbkmoney.fistful.reporter.dao.mapper.RecordRowMapper;
import com.rbkmoney.fistful.reporter.domain.tables.pojos.Deposit;
import com.rbkmoney.fistful.reporter.domain.tables.records.DepositRecord;
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
public class DepositDaoImpl extends AbstractGenericDao implements DepositDao {

    private final RowMapper<Deposit> depositRowMapper;

    @Autowired
    public DepositDaoImpl(DataSource dataSource) {
        super(dataSource);
        depositRowMapper = new RecordRowMapper<>(com.rbkmoney.fistful.reporter.domain.tables.Deposit.DEPOSIT, Deposit.class);
    }

    @Override
    public Optional<Long> getLastEventId() throws DaoException {
        Query query = getDslContext().select(DSL.max(com.rbkmoney.fistful.reporter.domain.tables.Deposit.DEPOSIT.EVENT_ID)).from(com.rbkmoney.fistful.reporter.domain.tables.Deposit.DEPOSIT);
        return Optional.ofNullable(fetchOne(query, Long.class));
    }

    @Override
    public Long save(Deposit deposit) throws DaoException {
        DepositRecord record = getDslContext().newRecord(com.rbkmoney.fistful.reporter.domain.tables.Deposit.DEPOSIT, deposit);
        Query query = getDslContext().insertInto(com.rbkmoney.fistful.reporter.domain.tables.Deposit.DEPOSIT).set(record).returning(com.rbkmoney.fistful.reporter.domain.tables.Deposit.DEPOSIT.ID);

        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        executeOne(query, keyHolder);
        return keyHolder.getKey().longValue();
    }

    @Override
    public Deposit get(String depositId) throws DaoException {
        Query query = getDslContext().selectFrom(com.rbkmoney.fistful.reporter.domain.tables.Deposit.DEPOSIT)
                .where(
                        com.rbkmoney.fistful.reporter.domain.tables.Deposit.DEPOSIT.DEPOSIT_ID.eq(depositId)
                                .and(com.rbkmoney.fistful.reporter.domain.tables.Deposit.DEPOSIT.CURRENT)
                );

        return fetchOne(query, depositRowMapper);
    }

    @Override
    public void updateNotCurrent(String depositId) throws DaoException {
        Query query = getDslContext().update(com.rbkmoney.fistful.reporter.domain.tables.Deposit.DEPOSIT).set(com.rbkmoney.fistful.reporter.domain.tables.Deposit.DEPOSIT.CURRENT, false)
                .where(
                        com.rbkmoney.fistful.reporter.domain.tables.Deposit.DEPOSIT.DEPOSIT_ID.eq(depositId)
                                .and(com.rbkmoney.fistful.reporter.domain.tables.Deposit.DEPOSIT.CURRENT)
                );
        executeOne(query);
    }

}

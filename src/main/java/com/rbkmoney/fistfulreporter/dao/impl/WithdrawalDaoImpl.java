package com.rbkmoney.fistfulreporter.dao.impl;

import com.rbkmoney.fistfulreporter.dao.WithdrawalDao;
import com.rbkmoney.fistfulreporter.dao.mapper.RecordRowMapper;
import com.rbkmoney.fistfulreporter.domain.tables.pojos.Withdrawal;
import com.rbkmoney.fistfulreporter.domain.tables.records.WithdrawalRecord;
import com.rbkmoney.fistfulreporter.exception.DaoException;
import org.jooq.Query;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.Optional;

import static com.rbkmoney.fistfulreporter.domain.tables.Withdrawal.WITHDRAWAL;

@Component
public class WithdrawalDaoImpl extends AbstractGenericDao implements WithdrawalDao {

    private final RowMapper<Withdrawal> withdrawalRowMapper;

    @Autowired
    public WithdrawalDaoImpl(DataSource dataSource) {
        super(dataSource);
        withdrawalRowMapper = new RecordRowMapper<>(WITHDRAWAL, Withdrawal.class);
    }

    @Override
    public Optional<Long> getLastEventId() throws DaoException {
        Query query = getDslContext().select(DSL.max(WITHDRAWAL.EVENT_ID)).from(WITHDRAWAL);
        return Optional.ofNullable(fetchOne(query, Long.class));
    }

    @Override
    public Long save(Withdrawal withdrawal) throws DaoException {
        WithdrawalRecord record = getDslContext().newRecord(WITHDRAWAL, withdrawal);
        Query query = getDslContext().insertInto(WITHDRAWAL).set(record).returning(WITHDRAWAL.ID);

        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        executeOne(query, keyHolder);
        return keyHolder.getKey().longValue();
    }

    @Override
    public Withdrawal get(String withdrawalId) throws DaoException {
        Query query = getDslContext().selectFrom(WITHDRAWAL)
                .where(
                        WITHDRAWAL.WITHDRAWAL_ID.eq(withdrawalId)
                                .and(WITHDRAWAL.CURRENT)
                );

        return fetchOne(query, withdrawalRowMapper);
    }

    @Override
    public void updateNotCurrent(String withdrawalId) throws DaoException {
        Query query = getDslContext().update(WITHDRAWAL).set(WITHDRAWAL.CURRENT, false)
                .where(
                        WITHDRAWAL.WITHDRAWAL_ID.eq(withdrawalId)
                                .and(WITHDRAWAL.CURRENT)
                );
        executeOne(query);
    }
}

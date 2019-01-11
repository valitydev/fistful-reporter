package com.rbkmoney.fistfulreporter.dao.impl;

import com.rbkmoney.fistfulreporter.dao.WalletDao;
import com.rbkmoney.fistfulreporter.dao.mapper.RecordRowMapper;
import com.rbkmoney.fistfulreporter.domain.tables.pojos.Wallet;
import com.rbkmoney.fistfulreporter.domain.tables.records.WalletRecord;
import com.rbkmoney.fistfulreporter.exception.DaoException;
import org.jooq.Query;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.Optional;

import static com.rbkmoney.fistfulreporter.domain.tables.Wallet.WALLET;

@Component
public class WalletDaoImpl extends AbstractGenericDao implements WalletDao {

    private final RowMapper<Wallet> walletRowMapper;

    @Autowired
    public WalletDaoImpl(DataSource dataSource) {
        super(dataSource);
        walletRowMapper = new RecordRowMapper<>(WALLET, Wallet.class);
    }

    @Override
    public Optional<Long> getLastEventId() throws DaoException {
        Query query = getDslContext().select(DSL.max(WALLET.EVENT_ID)).from(WALLET);
        return Optional.ofNullable(fetchOne(query, Long.class));
    }

    @Override
    public Long save(Wallet wallet) throws DaoException {
        WalletRecord record = getDslContext().newRecord(WALLET, wallet);
        Query query = getDslContext().insertInto(WALLET).set(record).returning(WALLET.ID);

        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        executeOne(query, keyHolder);
        return keyHolder.getKey().longValue();
    }

    @Override
    public Wallet get(String walletId) throws DaoException {
        Query query = getDslContext().selectFrom(WALLET)
                .where(
                        WALLET.WALLET_ID.eq(walletId)
                                .and(WALLET.CURRENT)
                );

        return fetchOne(query, walletRowMapper);
    }

    @Override
    public void updateNotCurrent(String walletId) throws DaoException {
        Query query = getDslContext().update(WALLET).set(WALLET.CURRENT, false)
                .where(
                        WALLET.WALLET_ID.eq(walletId)
                                .and(WALLET.CURRENT)
                );
        executeOne(query);
    }

}

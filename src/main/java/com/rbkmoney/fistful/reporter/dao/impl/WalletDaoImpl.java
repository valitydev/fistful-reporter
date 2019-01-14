package com.rbkmoney.fistful.reporter.dao.impl;

import com.rbkmoney.fistful.reporter.dao.WalletDao;
import com.rbkmoney.fistful.reporter.dao.mapper.RecordRowMapper;
import com.rbkmoney.fistful.reporter.domain.tables.pojos.Wallet;
import com.rbkmoney.fistful.reporter.domain.tables.records.WalletRecord;
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
public class WalletDaoImpl extends AbstractGenericDao implements WalletDao {

    private final RowMapper<Wallet> walletRowMapper;

    @Autowired
    public WalletDaoImpl(DataSource dataSource) {
        super(dataSource);
        walletRowMapper = new RecordRowMapper<>(com.rbkmoney.fistful.reporter.domain.tables.Wallet.WALLET, Wallet.class);
    }

    @Override
    public Optional<Long> getLastEventId() throws DaoException {
        Query query = getDslContext().select(DSL.max(com.rbkmoney.fistful.reporter.domain.tables.Wallet.WALLET.EVENT_ID)).from(com.rbkmoney.fistful.reporter.domain.tables.Wallet.WALLET);
        return Optional.ofNullable(fetchOne(query, Long.class));
    }

    @Override
    public Long save(Wallet wallet) throws DaoException {
        WalletRecord record = getDslContext().newRecord(com.rbkmoney.fistful.reporter.domain.tables.Wallet.WALLET, wallet);
        Query query = getDslContext().insertInto(com.rbkmoney.fistful.reporter.domain.tables.Wallet.WALLET).set(record).returning(com.rbkmoney.fistful.reporter.domain.tables.Wallet.WALLET.ID);

        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        executeOne(query, keyHolder);
        return keyHolder.getKey().longValue();
    }

    @Override
    public Wallet get(String walletId) throws DaoException {
        Query query = getDslContext().selectFrom(com.rbkmoney.fistful.reporter.domain.tables.Wallet.WALLET)
                .where(
                        com.rbkmoney.fistful.reporter.domain.tables.Wallet.WALLET.WALLET_ID.eq(walletId)
                                .and(com.rbkmoney.fistful.reporter.domain.tables.Wallet.WALLET.CURRENT)
                );

        return fetchOne(query, walletRowMapper);
    }

    @Override
    public void updateNotCurrent(String walletId) throws DaoException {
        Query query = getDslContext().update(com.rbkmoney.fistful.reporter.domain.tables.Wallet.WALLET).set(com.rbkmoney.fistful.reporter.domain.tables.Wallet.WALLET.CURRENT, false)
                .where(
                        com.rbkmoney.fistful.reporter.domain.tables.Wallet.WALLET.WALLET_ID.eq(walletId)
                                .and(com.rbkmoney.fistful.reporter.domain.tables.Wallet.WALLET.CURRENT)
                );
        executeOne(query);
    }

}

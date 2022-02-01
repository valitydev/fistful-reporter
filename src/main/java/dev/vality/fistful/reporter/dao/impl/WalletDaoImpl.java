package dev.vality.fistful.reporter.dao.impl;

import com.zaxxer.hikari.HikariDataSource;
import dev.vality.dao.impl.AbstractGenericDao;
import dev.vality.fistful.reporter.dao.WalletDao;
import dev.vality.fistful.reporter.dao.mapper.RecordRowMapper;
import dev.vality.fistful.reporter.domain.tables.pojos.Wallet;
import dev.vality.fistful.reporter.domain.tables.records.WalletRecord;
import org.jooq.Condition;
import org.jooq.Query;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static dev.vality.fistful.reporter.domain.tables.Wallet.WALLET;

@Component
public class WalletDaoImpl extends AbstractGenericDao implements WalletDao {

    private final RowMapper<Wallet> walletRowMapper;

    @Autowired
    public WalletDaoImpl(HikariDataSource dataSource) {
        super(dataSource);
        walletRowMapper = new RecordRowMapper<>(WALLET, Wallet.class);
    }

    @Override
    public Optional<Long> getLastEventId() {
        Query query = getDslContext().select(DSL.max(WALLET.EVENT_ID)).from(WALLET);

        return Optional.ofNullable(fetchOne(query, Long.class));
    }

    @Override
    public Optional<Long> save(Wallet wallet) {
        WalletRecord record = getDslContext().newRecord(WALLET, wallet);
        Query query = getDslContext()
                .insertInto(WALLET)
                .set(record)
                .onConflict(WALLET.WALLET_ID, WALLET.EVENT_ID)
                .doNothing()
                .returning(WALLET.ID);

        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        execute(query, keyHolder);
        return Optional.ofNullable(keyHolder.getKey()).map(Number::longValue);
    }

    @Override
    public Wallet get(String walletId) {
        Condition condition = WALLET.WALLET_ID.eq(walletId)
                .and(WALLET.CURRENT);
        Query query = getDslContext().selectFrom(WALLET).where(condition);

        return fetchOne(query, walletRowMapper);
    }

    @Override
    public void updateNotCurrent(Long walletId) {
        Query query = getDslContext()
                .update(WALLET)
                .set(WALLET.CURRENT, false)
                .where(WALLET.ID.eq(walletId)
                        .and(WALLET.CURRENT));
        execute(query);
    }

}

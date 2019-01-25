package com.rbkmoney.fistful.reporter.dao.impl;

import com.rbkmoney.fistful.reporter.dao.WithdrawalDao;
import com.rbkmoney.fistful.reporter.dao.mapper.RecordRowMapper;
import com.rbkmoney.fistful.reporter.domain.enums.WithdrawalEventType;
import com.rbkmoney.fistful.reporter.domain.enums.WithdrawalStatus;
import com.rbkmoney.fistful.reporter.domain.tables.pojos.Report;
import com.rbkmoney.fistful.reporter.domain.tables.pojos.Withdrawal;
import com.rbkmoney.fistful.reporter.domain.tables.records.WithdrawalRecord;
import com.rbkmoney.fistful.reporter.exception.DaoException;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.Query;
import org.jooq.Table;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.rbkmoney.fistful.reporter.domain.tables.Identity.IDENTITY;
import static com.rbkmoney.fistful.reporter.domain.tables.Wallet.WALLET;
import static com.rbkmoney.fistful.reporter.domain.tables.Withdrawal.WITHDRAWAL;
import static org.jooq.impl.DSL.max;

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
        Query query = getDslContext().select(max(WITHDRAWAL.EVENT_ID)).from(WITHDRAWAL);

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
        Condition condition = WITHDRAWAL.WITHDRAWAL_ID.eq(withdrawalId)
                .and(WITHDRAWAL.CURRENT);
        Query query = getDslContext().selectFrom(WITHDRAWAL).where(condition);

        return fetchOne(query, withdrawalRowMapper);
    }

    @Override
    public void updateNotCurrent(String withdrawalId) throws DaoException {
        Condition condition = WITHDRAWAL.WITHDRAWAL_ID.eq(withdrawalId)
                .and(WITHDRAWAL.CURRENT);
        Query query = getDslContext().update(WITHDRAWAL).set(WITHDRAWAL.CURRENT, false).where(condition);

        executeOne(query);
    }

    @Override
    public List<Withdrawal> getSucceededWithdrawalsByReport(Report report, Long fromId, int limit) throws DaoException {
        String partyId = report.getPartyId();
        String contractId = report.getContractId();
        LocalDateTime fromTime = report.getFromTime();
        LocalDateTime toTime = report.getToTime();


        String identityIdsTableAlias = "i";
        String identityId = "identity_id";
        Table identityIdsTable = getDslContext().selectDistinct(IDENTITY.IDENTITY_ID).from(IDENTITY)
                .where(
                        IDENTITY.PARTY_ID.eq(partyId)
                                .and(IDENTITY.PARTY_CONTRACT_ID.eq(contractId))
                )
                .asTable(identityIdsTableAlias);

        String walletIdsTableAlias = "w";
        String walletId = "wallet_id";
        Table walletIdsTable = getDslContext().selectDistinct(WALLET.WALLET_ID).from(WALLET)
                .join(identityIdsTable)
                .on(WALLET.IDENTITY_ID.eq((Field<String>) identityIdsTable.field(identityId)))
                .asTable(walletIdsTableAlias);

        Query query = getDslContext().select().from(WITHDRAWAL)
                .join(walletIdsTable)
                .on(
                        WITHDRAWAL.WALLET_ID.eq((Field<String>) walletIdsTable.field(walletId))
                                .and(WITHDRAWAL.EVENT_TYPE.eq(WithdrawalEventType.WITHDRAWAL_STATUS_CHANGED))
                                .and(WITHDRAWAL.WITHDRAWAL_STATUS.eq(WithdrawalStatus.succeeded))
                                .and(WITHDRAWAL.EVENT_CREATED_AT.ge(fromTime))
                                .and(WITHDRAWAL.EVENT_CREATED_AT.le(toTime))
                                .and(WITHDRAWAL.ID.gt(fromId))
                )
                .orderBy(WITHDRAWAL.ID)
                .limit(limit);

        return fetch(query, withdrawalRowMapper);
    }
}

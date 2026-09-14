package dev.vality.fistful.reporter.dao.impl;

import com.zaxxer.hikari.HikariDataSource;
import dev.vality.dao.impl.AbstractGenericDao;
import dev.vality.fistful.reporter.dao.WithdrawalAdjustmentPlanDao;
import dev.vality.fistful.reporter.dao.mapper.RecordRowMapper;
import dev.vality.fistful.reporter.domain.tables.pojos.WithdrawalAdjustmentPlan;
import dev.vality.fistful.reporter.domain.tables.records.WithdrawalAdjustmentPlanRecord;
import org.jooq.Query;
import org.springframework.boot.sql.init.dependency.DependsOnDatabaseInitialization;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static dev.vality.fistful.reporter.domain.tables.WithdrawalAdjustmentPlan.WITHDRAWAL_ADJUSTMENT_PLAN;

@Component
@DependsOnDatabaseInitialization
public class WithdrawalAdjustmentPlanDaoImpl extends AbstractGenericDao implements WithdrawalAdjustmentPlanDao {

    private final RowMapper<WithdrawalAdjustmentPlan> rowMapper;

    public WithdrawalAdjustmentPlanDaoImpl(HikariDataSource dataSource) {
        super(dataSource);
        rowMapper = new RecordRowMapper<>(WITHDRAWAL_ADJUSTMENT_PLAN, WithdrawalAdjustmentPlan.class);
    }

    @Override
    public void save(WithdrawalAdjustmentPlan plan) {
        WithdrawalAdjustmentPlanRecord record = getDslContext().newRecord(WITHDRAWAL_ADJUSTMENT_PLAN, plan);
        Query query = getDslContext().insertInto(WITHDRAWAL_ADJUSTMENT_PLAN)
                .set(record)
                .onConflict(WITHDRAWAL_ADJUSTMENT_PLAN.ADJUSTMENT_ID)
                .doNothing();
        execute(query);
    }

    @Override
    public Optional<WithdrawalAdjustmentPlan> getUnapplied(String adjustmentId) {
        Query query = getDslContext().selectFrom(WITHDRAWAL_ADJUSTMENT_PLAN)
                .where(WITHDRAWAL_ADJUSTMENT_PLAN.ADJUSTMENT_ID.eq(adjustmentId)
                        .and(WITHDRAWAL_ADJUSTMENT_PLAN.APPLIED.isFalse()));
        return Optional.ofNullable(fetchOne(query, rowMapper));
    }

    @Override
    public void markApplied(String adjustmentId) {
        Query query = getDslContext().update(WITHDRAWAL_ADJUSTMENT_PLAN)
                .set(WITHDRAWAL_ADJUSTMENT_PLAN.APPLIED, true)
                .where(WITHDRAWAL_ADJUSTMENT_PLAN.ADJUSTMENT_ID.eq(adjustmentId));
        execute(query);
    }
}

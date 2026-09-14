package dev.vality.fistful.reporter.dao;

import dev.vality.fistful.reporter.domain.tables.pojos.WithdrawalAdjustmentPlan;

import java.util.Optional;

public interface WithdrawalAdjustmentPlanDao {

    void save(WithdrawalAdjustmentPlan plan);

    Optional<WithdrawalAdjustmentPlan> getUnapplied(String adjustmentId);

    void markApplied(String adjustmentId);
}

package dev.vality.fistful.reporter.handler.withdrawal;

import dev.vality.dao.DaoException;
import dev.vality.fistful.reporter.dao.WithdrawalAdjustmentPlanDao;
import dev.vality.fistful.reporter.dao.WithdrawalDao;
import dev.vality.fistful.reporter.domain.enums.WithdrawalEventType;
import dev.vality.fistful.reporter.domain.tables.pojos.Withdrawal;
import dev.vality.fistful.reporter.domain.tables.pojos.WithdrawalAdjustmentPlan;
import dev.vality.fistful.reporter.exception.StorageException;
import dev.vality.fistful.withdrawal.TimestampedChange;
import dev.vality.geck.common.util.TypeUtil;
import dev.vality.machinegun.eventsink.MachineEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class WithdrawalAdjustmentSucceededHandler implements WithdrawalEventHandler {

    private final WithdrawalDao withdrawalDao;
    private final WithdrawalAdjustmentPlanDao adjustmentPlanDao;

    @Override
    public boolean accept(TimestampedChange change) {
        return change.getChange().isSetAdjustment()
                && change.getChange().getAdjustment().isSetPayload()
                && change.getChange().getAdjustment().getPayload().isSetStatusChanged()
                && change.getChange().getAdjustment().getPayload().getStatusChanged().isSetStatus()
                && change.getChange().getAdjustment().getPayload().getStatusChanged().getStatus().isSetSucceeded();
    }

    @Override
    public void handle(TimestampedChange change, MachineEvent event) {
        try {
            String adjustmentId = change.getChange().getAdjustment().getId();
            adjustmentPlanDao.getUnapplied(adjustmentId).ifPresentOrElse(
                    plan -> apply(plan, change, event),
                    () -> log.info("Withdrawal adjustment plan is absent or already applied, eventId={}, " +
                                    "adjustmentId={}", event.getEventId(), adjustmentId));
        } catch (DaoException e) {
            throw new StorageException(e);
        }
    }

    private void apply(
            WithdrawalAdjustmentPlan plan,
            TimestampedChange change,
            MachineEvent event) {
        Withdrawal oldWithdrawal = withdrawalDao.get(event.getSourceId());
        Withdrawal withdrawal = new Withdrawal(oldWithdrawal);
        withdrawal.setId(null);
        withdrawal.setWtime(null);
        withdrawal.setEventId(event.getEventId());
        withdrawal.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
        withdrawal.setWithdrawalId(event.getSourceId());
        withdrawal.setEventOccuredAt(TypeUtil.stringToLocalDateTime(change.getOccuredAt()));
        withdrawal.setEventType(WithdrawalEventType.WITHDRAWAL_ADJUSTMENT_SUCCEEDED);
        withdrawal.setFee(plan.getFee());
        withdrawal.setProviderFee(plan.getProviderFee());
        if (plan.getAmount() != null) {
            withdrawal.setAmount(plan.getAmount());
            withdrawal.setCurrencyCode(plan.getCurrencyCode());
        }
        withdrawalDao.save(withdrawal).ifPresentOrElse(
                id -> {
                    withdrawalDao.updateNotCurrent(oldWithdrawal.getId());
                    adjustmentPlanDao.markApplied(plan.getAdjustmentId());
                    log.info("Withdrawal adjustment applied, eventId={}, withdrawalId={}, adjustmentId={}",
                            event.getEventId(), event.getSourceId(), plan.getAdjustmentId());
                },
                () -> log.info("Withdrawal adjustment bound duplicated, eventId={}, adjustmentId={}",
                        event.getEventId(), plan.getAdjustmentId()));
    }
}

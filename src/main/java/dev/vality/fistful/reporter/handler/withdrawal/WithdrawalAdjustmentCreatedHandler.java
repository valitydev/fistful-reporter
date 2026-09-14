package dev.vality.fistful.reporter.handler.withdrawal;

import dev.vality.dao.DaoException;
import dev.vality.fistful.base.Cash;
import dev.vality.fistful.reporter.dao.WithdrawalAdjustmentPlanDao;
import dev.vality.fistful.reporter.domain.tables.pojos.WithdrawalAdjustmentPlan;
import dev.vality.fistful.reporter.exception.StorageException;
import dev.vality.fistful.reporter.util.CashFlowConverter;
import dev.vality.fistful.withdrawal.TimestampedChange;
import dev.vality.fistful.withdrawal.adjustment.ChangesPlan;
import dev.vality.machinegun.eventsink.MachineEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class WithdrawalAdjustmentCreatedHandler implements WithdrawalEventHandler {

    private final WithdrawalAdjustmentPlanDao adjustmentPlanDao;

    @Override
    public boolean accept(TimestampedChange change) {
        return change.getChange().isSetAdjustment()
                && change.getChange().getAdjustment().isSetPayload()
                && change.getChange().getAdjustment().getPayload().isSetCreated()
                && change.getChange().getAdjustment().getPayload().getCreated().isSetAdjustment()
                && change.getChange().getAdjustment().getPayload().getCreated().getAdjustment().isSetChangesPlan()
                && change.getChange().getAdjustment().getPayload().getCreated().getAdjustment().getChangesPlan()
                        .isSetNewCashFlow();
    }

    @Override
    public void handle(TimestampedChange change, MachineEvent event) {
        try {
            var adjustment = change.getChange().getAdjustment();
            ChangesPlan changesPlan = adjustment.getPayload().getCreated().getAdjustment().getChangesPlan();
            var postings = changesPlan.getNewCashFlow().getNewCashFlow().getPostings();
            WithdrawalAdjustmentPlan plan = new WithdrawalAdjustmentPlan();
            plan.setAdjustmentId(adjustment.getId());
            plan.setWithdrawalId(event.getSourceId());
            plan.setFee(CashFlowConverter.getFistfulFee(postings));
            plan.setProviderFee(CashFlowConverter.getFistfulProviderFee(postings));
            plan.setApplied(false);
            if (changesPlan.isSetNewBody()) {
                Cash body = changesPlan.getNewBody().getNewBody();
                plan.setAmount(body.getAmount());
                plan.setCurrencyCode(body.getCurrency().getSymbolicCode());
            }
            adjustmentPlanDao.save(plan);
            log.info("Withdrawal adjustment plan saved, eventId={}, withdrawalId={}, adjustmentId={}",
                    event.getEventId(), event.getSourceId(), adjustment.getId());
        } catch (DaoException e) {
            throw new StorageException(e);
        }
    }
}

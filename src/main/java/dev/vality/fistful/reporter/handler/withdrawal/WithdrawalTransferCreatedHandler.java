package dev.vality.fistful.reporter.handler.withdrawal;

import dev.vality.dao.DaoException;
import dev.vality.fistful.cashflow.FinalCashFlowPosting;
import dev.vality.fistful.reporter.dao.FistfulCashFlowDao;
import dev.vality.fistful.reporter.dao.WithdrawalDao;
import dev.vality.fistful.reporter.domain.enums.FistfulCashFlowChangeType;
import dev.vality.fistful.reporter.domain.enums.WithdrawalEventType;
import dev.vality.fistful.reporter.domain.enums.WithdrawalTransferStatus;
import dev.vality.fistful.reporter.domain.tables.pojos.FistfulCashFlow;
import dev.vality.fistful.reporter.domain.tables.pojos.Withdrawal;
import dev.vality.fistful.reporter.dto.FistfulCashFlowSinkEvent;
import dev.vality.fistful.reporter.exception.StorageException;
import dev.vality.fistful.reporter.util.CashFlowConverter;
import dev.vality.fistful.withdrawal.TimestampedChange;
import dev.vality.geck.common.util.TypeUtil;
import dev.vality.machinegun.eventsink.MachineEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class WithdrawalTransferCreatedHandler implements WithdrawalEventHandler {

    private final WithdrawalDao withdrawalDao;
    private final FistfulCashFlowDao fistfulCashFlowDao;

    @Override
    public boolean accept(TimestampedChange change) {
        return change.getChange().isSetTransfer()
                && change.getChange().getTransfer().isSetPayload()
                && change.getChange().getTransfer().getPayload().isSetCreated()
                && change.getChange().getTransfer().getPayload().getCreated().isSetTransfer()
                && change.getChange().getTransfer().getPayload().getCreated().getTransfer().isSetCashflow();
    }

    @Override
    public void handle(TimestampedChange change, MachineEvent event) {
        try {
            log.info("Start withdrawal transfer created handling, eventId={}, withdrawalId={}, transferChange={}",
                    event.getEventId(), event.getSourceId(), change.getChange().getTransfer());
            Withdrawal oldWithdrawal = withdrawalDao.get(event.getSourceId());
            Withdrawal updatedWithdrawal = update(oldWithdrawal, change, event);
            List<FinalCashFlowPosting> postings = change.getChange().getTransfer().getPayload()
                    .getCreated().getTransfer().getCashflow().getPostings();
            withdrawalDao.save(updatedWithdrawal).ifPresentOrElse(
                    id -> {
                        withdrawalDao.updateNotCurrent(oldWithdrawal.getId());
                        List<FistfulCashFlow> fistfulCashFlows = CashFlowConverter.convertFistfulCashFlows(
                                new FistfulCashFlowSinkEvent(
                                        event.getEventId(),
                                        event.getCreatedAt(),
                                        event.getSourceId(),
                                        change.getOccuredAt(),
                                        WithdrawalEventType.WITHDRAWAL_TRANSFER_CREATED.toString(),
                                        id,
                                        FistfulCashFlowChangeType.withdrawal,
                                        postings
                                )
                        );
                        fistfulCashFlowDao.save(fistfulCashFlows);
                        log.info("Withdrawal transfer have been created, " +
                                        "eventId={}, withdrawalId={}, transferChange={}",
                                event.getEventId(), event.getSourceId(), change.getChange().getTransfer());
                    },
                    () -> log.info("Withdrawal transfer create bound duplicated, " +
                                    "eventId={}, withdrawalId={}, transferChange={}",
                            event.getEventId(), event.getSourceId(), change.getChange().getTransfer()));
        } catch (DaoException e) {
            throw new StorageException(e);
        }
    }

    private Withdrawal update(
            Withdrawal oldWithdrawal,
            TimestampedChange change,
            MachineEvent event) {
        Withdrawal withdrawal = new Withdrawal(oldWithdrawal);
        withdrawal.setId(null);
        withdrawal.setWtime(null);
        withdrawal.setEventId(event.getEventId());
        withdrawal.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
        withdrawal.setWithdrawalId(event.getSourceId());
        withdrawal.setEventOccuredAt(TypeUtil.stringToLocalDateTime(change.getOccuredAt()));
        withdrawal.setEventType(WithdrawalEventType.WITHDRAWAL_TRANSFER_CREATED);
        withdrawal.setWithdrawalTransferStatus(WithdrawalTransferStatus.created);
        List<FinalCashFlowPosting> postings = change.getChange().getTransfer().getPayload()
                .getCreated().getTransfer().getCashflow().getPostings();
        withdrawal.setFee(CashFlowConverter.getFistfulFee(postings));
        withdrawal.setProviderFee(CashFlowConverter.getFistfulProviderFee(postings));
        return withdrawal;
    }
}

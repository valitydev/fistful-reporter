package com.rbkmoney.fistful.reporter.handler.deposit;

import com.rbkmoney.dao.DaoException;
import dev.vality.fistful.cashflow.FinalCashFlowPosting;
import dev.vality.fistful.deposit.TimestampedChange;
import com.rbkmoney.fistful.reporter.dao.DepositDao;
import com.rbkmoney.fistful.reporter.dao.FistfulCashFlowDao;
import com.rbkmoney.fistful.reporter.domain.enums.*;
import com.rbkmoney.fistful.reporter.domain.tables.pojos.Deposit;
import com.rbkmoney.fistful.reporter.domain.tables.pojos.FistfulCashFlow;
import com.rbkmoney.fistful.reporter.dto.FistfulCashFlowSinkEvent;
import com.rbkmoney.fistful.reporter.exception.StorageException;
import com.rbkmoney.fistful.reporter.util.CashFlowConverter;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DepositTransferCreatedHandler implements DepositEventHandler {

    private final DepositDao depositDao;

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
            log.info("Start deposit transfer created handling, eventId={}, depositId={}, transferChange={}",
                    event.getEventId(), event.getSourceId(), change.getChange().getTransfer());
            List<FinalCashFlowPosting> postings = change
                    .getChange()
                    .getTransfer()
                    .getPayload()
                    .getCreated()
                    .getTransfer()
                    .getCashflow()
                    .getPostings();
            Deposit oldDeposit = depositDao.get(event.getSourceId());
            Deposit updatedDeposit = update(oldDeposit, change, event, postings);
            depositDao.save(updatedDeposit).ifPresentOrElse(
                    id -> {
                        depositDao.updateNotCurrent(oldDeposit.getId());
                        List<FistfulCashFlow> fistfulCashFlows = CashFlowConverter.convertFistfulCashFlows(
                                new FistfulCashFlowSinkEvent(
                                        event.getEventId(),
                                        event.getCreatedAt(),
                                        event.getSourceId(),
                                        change.getOccuredAt(),
                                        DepositEventType.DEPOSIT_TRANSFER_CREATED.toString(),
                                        id,
                                        FistfulCashFlowChangeType.deposit,
                                        postings
                                )
                        );
                        fistfulCashFlowDao.save(fistfulCashFlows);
                        log.info("Deposit transfer has been created, eventId={}, depositId={}, transferChange={}",
                                event.getEventId(), event.getSourceId(), change.getChange().getTransfer());
                    },
                    () -> log.info("Deposit transfer bound duplicated, eventId={}, depositId={}, transferChange={}",
                            event.getEventId(), event.getSourceId(), change.getChange().getTransfer())
            );
        } catch (DaoException e) {
            throw new StorageException(e);
        }
    }

    private Deposit update(
            Deposit oldDeposit,
            TimestampedChange change,
            MachineEvent event,
            List<FinalCashFlowPosting> postings) {
        Deposit deposit = new Deposit(oldDeposit);
        deposit.setId(null);
        deposit.setWtime(null);
        deposit.setEventId(event.getEventId());
        deposit.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
        deposit.setDepositId(event.getSourceId());
        deposit.setEventOccuredAt(TypeUtil.stringToLocalDateTime(change.getOccuredAt()));
        deposit.setEventType(DepositEventType.DEPOSIT_TRANSFER_CREATED);
        deposit.setDepositTransferStatus(DepositTransferStatus.created);
        deposit.setFee(CashFlowConverter.getFistfulFee(postings));
        deposit.setProviderFee(CashFlowConverter.getFistfulProviderFee(postings));
        return deposit;
    }
}

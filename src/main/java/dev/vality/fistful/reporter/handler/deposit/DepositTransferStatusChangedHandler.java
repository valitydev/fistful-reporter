package dev.vality.fistful.reporter.handler.deposit;

import dev.vality.dao.DaoException;
import dev.vality.fistful.deposit.TimestampedChange;
import dev.vality.fistful.reporter.dao.DepositDao;
import dev.vality.fistful.reporter.dao.FistfulCashFlowDao;
import dev.vality.fistful.reporter.domain.enums.DepositEventType;
import dev.vality.fistful.reporter.domain.enums.DepositTransferStatus;
import dev.vality.fistful.reporter.domain.enums.FistfulCashFlowChangeType;
import dev.vality.fistful.reporter.domain.tables.pojos.Deposit;
import dev.vality.fistful.reporter.domain.tables.pojos.FistfulCashFlow;
import dev.vality.fistful.reporter.exception.StorageException;
import dev.vality.fistful.transfer.Status;
import dev.vality.geck.common.util.TBaseUtil;
import dev.vality.geck.common.util.TypeUtil;
import dev.vality.machinegun.eventsink.MachineEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DepositTransferStatusChangedHandler implements DepositEventHandler {

    private final DepositDao depositDao;
    private final FistfulCashFlowDao fistfulCashFlowDao;

    @Override
    public boolean accept(TimestampedChange change) {
        return change.getChange().isSetTransfer()
                && change.getChange().getTransfer().isSetPayload()
                && change.getChange().getTransfer().getPayload().isSetStatusChanged()
                && change.getChange().getTransfer().getPayload().getStatusChanged().isSetStatus();
    }

    @Override
    public void handle(TimestampedChange change, MachineEvent event) {
        try {
            log.info("Start deposit transfer status changed handling, eventId={}, depositId={}, transferChange={}",
                    event.getEventId(), event.getSourceId(), change.getChange().getTransfer());
            Deposit oldDeposit = depositDao.get(event.getSourceId());
            Deposit updatedDeposit = update(oldDeposit, change, event);
            depositDao.save(updatedDeposit).ifPresentOrElse(
                    id -> {
                        depositDao.updateNotCurrent(oldDeposit.getId());
                        List<FistfulCashFlow> cashFlows = fistfulCashFlowDao.getByObjId(
                                id,
                                FistfulCashFlowChangeType.deposit);
                        fillCashFlows(cashFlows, event, DepositEventType.DEPOSIT_TRANSFER_STATUS_CHANGED, change, id);
                        fistfulCashFlowDao.save(cashFlows);
                        log.info("Deposit transfer status has been changed, " +
                                        "eventId={}, depositId={}, transferChange={}",
                                event.getEventId(), event.getSourceId(), change.getChange().getTransfer());
                    },
                    () -> log.info("Deposit transfer status change bound duplicated, " +
                                    "eventId={}, depositId={}, transferChange={}",
                            event.getEventId(), event.getSourceId(), change.getChange().getTransfer())
            );
        } catch (DaoException e) {
            throw new StorageException(e);
        }
    }

    private Deposit update(
            Deposit oldDeposit,
            TimestampedChange change,
            MachineEvent event) {
        Deposit deposit = new Deposit(oldDeposit);
        deposit.setId(null);
        deposit.setWtime(null);
        deposit.setEventId(event.getEventId());
        deposit.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
        deposit.setDepositId(event.getSourceId());
        deposit.setEventOccuredAt(TypeUtil.stringToLocalDateTime(change.getOccuredAt()));
        deposit.setEventType(DepositEventType.DEPOSIT_TRANSFER_STATUS_CHANGED);
        Status status = change.getChange().getTransfer().getPayload().getStatusChanged().getStatus();
        deposit.setDepositTransferStatus(TBaseUtil.unionFieldToEnum(status, DepositTransferStatus.class));
        return deposit;
    }
}

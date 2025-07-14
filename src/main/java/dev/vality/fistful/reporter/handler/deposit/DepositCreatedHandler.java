package dev.vality.fistful.reporter.handler.deposit;

import dev.vality.dao.DaoException;
import dev.vality.fistful.base.Cash;
import dev.vality.fistful.deposit.TimestampedChange;
import dev.vality.fistful.reporter.dao.DepositDao;
import dev.vality.fistful.reporter.domain.enums.DepositEventType;
import dev.vality.fistful.reporter.domain.enums.DepositStatus;
import dev.vality.fistful.reporter.domain.tables.pojos.Deposit;
import dev.vality.fistful.reporter.exception.StorageException;
import dev.vality.geck.common.util.TypeUtil;
import dev.vality.machinegun.eventsink.MachineEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class DepositCreatedHandler implements DepositEventHandler {

    private final DepositDao depositDao;

    @Override
    public boolean accept(TimestampedChange change) {
        return change.getChange().isSetCreated() && change.getChange().getCreated().isSetDeposit();
    }

    @Override
    public void handle(TimestampedChange change, MachineEvent event) {
        try {
            log.info("Start deposit created handling, eventId={}, depositId={}",
                    event.getEventId(), event.getSourceId());

            Deposit deposit = new Deposit();
            deposit.setEventId(event.getEventId());
            deposit.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
            deposit.setDepositId(event.getSourceId());
            deposit.setEventOccuredAt(TypeUtil.stringToLocalDateTime(change.getOccuredAt()));
            deposit.setEventType(DepositEventType.DEPOSIT_CREATED);

            var depositDamsel = change.getChange().getCreated().getDeposit();
            deposit.setWalletId(depositDamsel.getWalletId());
            deposit.setSourceId(depositDamsel.getSourceId());
            deposit.setDepositStatus(DepositStatus.pending);
            deposit.setPartyId(deposit.getPartyId());
            Cash cash = depositDamsel.getBody();
            deposit.setAmount(cash.getAmount());
            deposit.setCurrencyCode(cash.getCurrency().getSymbolicCode());

            depositDao.save(deposit).ifPresentOrElse(
                    dbContractId -> log.info("Deposit created has been saved, eventId={}, depositId={}",
                            event.getEventId(), event.getSourceId()),
                    () -> log.info("Deposit created bound duplicated, eventId={}, depositId={}",
                            event.getEventId(), event.getSourceId()));
        } catch (DaoException e) {
            throw new StorageException(e);
        }
    }
}

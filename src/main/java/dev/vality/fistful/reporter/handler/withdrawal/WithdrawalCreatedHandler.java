package dev.vality.fistful.reporter.handler.withdrawal;

import dev.vality.dao.DaoException;
import dev.vality.fistful.base.Cash;
import dev.vality.fistful.reporter.dao.WithdrawalDao;
import dev.vality.fistful.reporter.domain.enums.WithdrawalEventType;
import dev.vality.fistful.reporter.domain.enums.WithdrawalStatus;
import dev.vality.fistful.reporter.domain.tables.pojos.Withdrawal;
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
public class WithdrawalCreatedHandler implements WithdrawalEventHandler {

    private final WithdrawalDao withdrawalDao;

    @Override
    public boolean accept(TimestampedChange change) {
        return change.getChange().isSetCreated() && change.getChange().getCreated().isSetWithdrawal();
    }

    @Override
    public void handle(TimestampedChange change, MachineEvent event) {
        try {
            var withdrawalDamsel = change.getChange().getCreated().getWithdrawal();

            log.info("Start withdrawal created handling, eventId={}, withdrawalId={}",
                    event.getEventId(), event.getSourceId());

            Withdrawal withdrawal = new Withdrawal();
            withdrawal.setExternalId(withdrawalDamsel.getExternalId());
            withdrawal.setEventId(event.getEventId());
            withdrawal.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
            withdrawal.setWithdrawalId(event.getSourceId());
            withdrawal.setEventOccuredAt(TypeUtil.stringToLocalDateTime(change.getOccuredAt()));
            withdrawal.setEventType(WithdrawalEventType.WITHDRAWAL_CREATED);
            withdrawal.setWalletId(withdrawalDamsel.getWalletId());
            withdrawal.setDestinationId(withdrawalDamsel.getDestinationId());
            withdrawal.setWithdrawalStatus(WithdrawalStatus.pending);

            withdrawal.setPartyId(withdrawal.getPartyId());

            Cash cash = withdrawalDamsel.getBody();
            withdrawal.setAmount(cash.getAmount());
            withdrawal.setCurrencyCode(cash.getCurrency().getSymbolicCode());

            withdrawalDao.save(withdrawal).ifPresentOrElse(
                    dbContractId -> log.info("Withdrawal has been created, eventId={}, withdrawalId={}",
                            event.getEventId(), event.getSourceId()),
                    () -> log.info("Withdrawal created bound duplicated, eventId={}, withdrawalId={}",
                            event.getEventId(), event.getSourceId()));
        } catch (DaoException e) {
            throw new StorageException(e);
        }
    }
}

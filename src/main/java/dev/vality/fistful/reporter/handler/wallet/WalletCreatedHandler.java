package dev.vality.fistful.reporter.handler.wallet;

import dev.vality.dao.DaoException;
import dev.vality.fistful.reporter.dao.WalletDao;
import dev.vality.fistful.reporter.domain.enums.WalletEventType;
import dev.vality.fistful.reporter.domain.tables.pojos.Wallet;
import dev.vality.fistful.reporter.exception.StorageException;
import dev.vality.fistful.wallet.TimestampedChange;
import dev.vality.geck.common.util.TypeUtil;
import dev.vality.machinegun.eventsink.MachineEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class WalletCreatedHandler implements WalletEventHandler {

    private final WalletDao walletDao;

    @Override
    public boolean accept(TimestampedChange change) {
        return change.getChange().isSetCreated();
    }

    @Override
    public void handle(TimestampedChange change, MachineEvent event) {
        try {
            log.info("Start wallet created handling, eventId={}, walletId={}", event.getEventId(), event.getSourceId());
            Wallet wallet = new Wallet();

            wallet.setEventId(event.getEventId());
            wallet.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
            wallet.setWalletId(event.getSourceId());
            wallet.setEventOccuredAt(TypeUtil.stringToLocalDateTime(change.getOccuredAt()));
            wallet.setEventType(WalletEventType.WALLET_CREATED);
            wallet.setWalletName(change.getChange().getCreated().getName());

            walletDao.save(wallet).ifPresentOrElse(
                    dbContractId -> log.info("Wallet created has been saved, eventId={}, walletId={}",
                            event.getEventId(), event.getSourceId()),
                    () -> log.info("Wallet created bound duplicated, eventId={}, walletId={}",
                            event.getEventId(), event.getSourceId()));
        } catch (DaoException e) {
            throw new StorageException(e);
        }
    }
}

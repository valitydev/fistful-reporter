package dev.vality.fistful.reporter.handler.destination;

import dev.vality.dao.DaoException;
import dev.vality.fistful.base.BankCard;
import dev.vality.fistful.base.CryptoWallet;
import dev.vality.fistful.base.DigitalWallet;
import dev.vality.fistful.base.Resource;
import dev.vality.fistful.destination.TimestampedChange;
import dev.vality.fistful.reporter.dao.DestinationDao;
import dev.vality.fistful.reporter.domain.enums.DestinationEventType;
import dev.vality.fistful.reporter.domain.enums.DestinationResourceType;
import dev.vality.fistful.reporter.domain.enums.DestinationStatus;
import dev.vality.fistful.reporter.domain.tables.pojos.Destination;
import dev.vality.fistful.reporter.exception.StorageException;
import dev.vality.geck.common.util.TBaseUtil;
import dev.vality.geck.common.util.TypeUtil;
import dev.vality.machinegun.eventsink.MachineEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class DestinationCreatedHandler implements DestinationEventHandler {

    private final DestinationDao destinationDao;

    @Override
    public boolean accept(TimestampedChange change) {
        return change.getChange().isSetCreated();
    }

    @Override
    public void handle(TimestampedChange change, MachineEvent event) {
        try {
            log.info("Start destination created handling, eventId={}, destinationId={}",
                    event.getEventId(), event.getSourceId());
            Destination destination = new Destination();
            destination.setEventId(event.getEventId());
            destination.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
            destination.setDestinationId(event.getSourceId());
            destination.setEventOccuredAt(TypeUtil.stringToLocalDateTime(change.getOccuredAt()));
            destination.setEventType(DestinationEventType.DESTINATION_CREATED);
            destination.setDestinationName(change.getChange().getCreated().getName());
            destination.setDestinationStatus(DestinationStatus.unauthorized);

            Resource resource = change.getChange().getCreated().getResource();
            if (resource.isSetBankCard()) {
                BankCard bankCard = resource.getBankCard().getBankCard();
                destination.setResourceBankCardToken(bankCard.getToken());
                destination.setResourceBankCardBin(bankCard.getBin());
                destination.setResourceBankCardMaskedPan(bankCard.getMaskedPan());
                if (bankCard.isSetPaymentSystem()) {
                    destination.setResourceBankCardPaymentSystem(bankCard.getPaymentSystem().toString());
                }
            } else if (resource.isSetCryptoWallet()) {
                CryptoWallet cryptoWallet = resource.getCryptoWallet().getCryptoWallet();
                destination.setCryptoWalletId(cryptoWallet.getId());
                destination.setCryptoWalletCurrency(cryptoWallet.getCurrency().getId());
            } else if (resource.isSetDigitalWallet()) {
                DigitalWallet digitalWallet = resource.getDigitalWallet().getDigitalWallet();
                destination.setDigitalWalletId(digitalWallet.getId());
                if (digitalWallet.getPaymentService() != null) {
                    destination.setDigitalWalletProvider(digitalWallet.getPaymentService().getId());
                }
            }
            destination.setResourceType(TBaseUtil.unionFieldToEnum(resource, DestinationResourceType.class));

            destinationDao.save(destination).ifPresentOrElse(
                    dbContractId -> log.info("Destination created has been saved, eventId={}, destinationId={}",
                            event.getEventId(), event.getSourceId()),
                    () -> log.info("Destination created bound duplicated, eventId={}, destinationId={}",
                            event.getEventId(), event.getSourceId()));
        } catch (DaoException e) {
            throw new StorageException(e);
        }
    }
}

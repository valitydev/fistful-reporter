package com.rbkmoney.fistful.reporter.util.handler;

import dev.vality.fistful.base.*;
import dev.vality.fistful.destination.*;
import com.rbkmoney.kafka.common.serialization.ThriftSerializer;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.machinegun.msgpack.Value;

public class DestinationHandlerTestUtil {

    public static final String DESTINATION_NAME = "name";

    public static final String DIGITAL_WALLET_ID = "digital_wallet_id";
    public static final String CRYPTO_WALLET_ID = "crypto_wallet_id";

    public static final String CARD_BIN = "bin";
    public static final String CARD_MASKED_PAN = "1232132";
    public static final String CARD_TOKEN_PROVIDER = "cardToken";

    public static MachineEvent createMachineEvent(String id) {
        return new MachineEvent()
                .setEventId(2L)
                .setSourceId(id)
                .setSourceNs("2")
                .setCreatedAt("2021-05-31T06:12:27Z")
                .setData(Value.bin(new ThriftSerializer<>().serialize("", createStatusChanged())));
    }

    public static TimestampedChange createStatusChanged() {
        return new TimestampedChange()
                .setOccuredAt("2021-05-31T06:12:27Z")
                .setChange(Change.status(StatusChange.changed(Status.authorized(new Authorized()))));
    }

    public static MachineEvent createCreatedMachineEvent(String id, Destination destination) {
        return new MachineEvent()
                .setEventId(2L)
                .setSourceId(id)
                .setSourceNs("2")
                .setCreatedAt("2021-05-31T06:12:27Z")
                .setData(Value.bin(new ThriftSerializer<>().serialize("", createCreated(destination))));
    }

    public static TimestampedChange createCreated(Destination destination) {
        return new TimestampedChange()
                .setOccuredAt("2021-05-31T06:12:27Z")
                .setChange(Change.created(destination));
    }

    public static dev.vality.fistful.base.DigitalWallet createFistfulDigitalWallet() {
        dev.vality.fistful.base.DigitalWallet digitalWallet = new dev.vality.fistful.base.DigitalWallet();
        digitalWallet.setId(DIGITAL_WALLET_ID);
        digitalWallet.setPaymentService(new PaymentServiceRef("webmoney"));
        return digitalWallet;
    }

    public static dev.vality.fistful.base.CryptoWallet createFistfulCryptoWallet() {
        dev.vality.fistful.base.CryptoWallet cryptoWallet = new dev.vality.fistful.base.CryptoWallet();
        cryptoWallet.setId(CRYPTO_WALLET_ID);
        cryptoWallet.setData(CryptoData.bitcoin(new CryptoDataBitcoin()));
        cryptoWallet.setCurrency(CryptoCurrency.bitcoin);
        return cryptoWallet;
    }

    public static dev.vality.fistful.base.BankCard createFistfulBankCard() {
        dev.vality.fistful.base.BankCard bankCard = new dev.vality.fistful.base.BankCard();
        bankCard.setToken(CARD_TOKEN_PROVIDER);
        bankCard.setBin(CARD_BIN);
        bankCard.setMaskedPan(CARD_MASKED_PAN);
        return bankCard;
    }

    public static ResourceDigitalWallet createResourceDigitalWallet() {
        ResourceDigitalWallet resourceDigitalWallet = new ResourceDigitalWallet();
        resourceDigitalWallet.setDigitalWallet(DestinationHandlerTestUtil.createFistfulDigitalWallet());
        return resourceDigitalWallet;
    }

    public static ResourceCryptoWallet createResourceCryptoWallet() {
        ResourceCryptoWallet resourceCryptoWallet = new ResourceCryptoWallet();
        resourceCryptoWallet.setCryptoWallet(DestinationHandlerTestUtil.createFistfulCryptoWallet());
        return resourceCryptoWallet;
    }

    public static ResourceBankCard createResourceBankCard() {
        ResourceBankCard resourceBankCard = new ResourceBankCard();
        resourceBankCard.setBankCard(DestinationHandlerTestUtil.createFistfulBankCard());
        return resourceBankCard;
    }

    public static dev.vality.fistful.destination.Destination createFistfulDestination(Resource fistfulResource) {
        dev.vality.fistful.destination.Destination fistfulDestination
                = new dev.vality.fistful.destination.Destination();
        fistfulDestination.setResource(fistfulResource);
        fistfulDestination.setName(DESTINATION_NAME);
        return fistfulDestination;
    }
}

package dev.vality.fistful.reporter.data;

import dev.vality.fistful.reporter.domain.enums.WithdrawalEventType;
import dev.vality.fistful.reporter.domain.enums.WithdrawalStatus;
import dev.vality.fistful.reporter.domain.tables.pojos.Report;
import dev.vality.fistful.reporter.domain.tables.pojos.Withdrawal;
import dev.vality.kafka.common.serialization.ThriftSerializer;
import dev.vality.machinegun.eventsink.MachineEvent;
import dev.vality.machinegun.eventsink.SinkEvent;
import dev.vality.machinegun.msgpack.Value;
import org.apache.thrift.TBase;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static dev.vality.testcontainers.annotations.util.RandomBeans.random;
import static dev.vality.testcontainers.annotations.util.RandomBeans.randomListOf;
import static dev.vality.testcontainers.annotations.util.ValuesGenerator.*;

public class TestData {

    public static final String identityId = generateString();
    public static final String partyId = generateString();
    public static final String contractId = generateString();
    public static final String walletId = generateString();

    public static List<Withdrawal> createWithdrawals(
            String partyId,
            String walletId,
            LocalDateTime eventCreatedAtTime,
            int expectedSize) {
        List<Withdrawal> withdrawals = new ArrayList<>();
        for (Withdrawal withdrawal : randomListOf(expectedSize, Withdrawal.class)) {
            fillAsReportWithdrawal(partyId, walletId, eventCreatedAtTime, withdrawal);
            withdrawals.add(withdrawal);
        }
        Withdrawal filtered = random(Withdrawal.class);
        fillAsReportWithdrawal(partyId, walletId, eventCreatedAtTime, filtered);
        filtered.setCurrent(false);
        withdrawals.add(filtered);
        for (Withdrawal withdrawal : randomListOf(4, Withdrawal.class)) {
            withdrawal.setId(null);
            withdrawals.add(withdrawal);
            withdrawal.setCurrent(true);
        }
        for (Withdrawal withdrawal : randomListOf(4, Withdrawal.class)) {
            withdrawal.setId(null);
            withdrawal.setWalletId(walletId);
            withdrawal.setWithdrawalStatus(WithdrawalStatus.pending);
            withdrawal.setEventType(WithdrawalEventType.WITHDRAWAL_STATUS_CHANGED);
            withdrawal.setEventCreatedAt(eventCreatedAtTime);
            withdrawal.setPartyId(partyId);
            withdrawal.setCurrencyCode("RUB");
            withdrawals.add(withdrawal);
            withdrawal.setCurrent(true);
        }
        for (Withdrawal withdrawal : randomListOf(4, Withdrawal.class)) {
            withdrawal.setId(null);
            withdrawal.setWalletId(walletId);
            withdrawal.setWithdrawalStatus(WithdrawalStatus.succeeded);
            withdrawal.setEventType(WithdrawalEventType.WITHDRAWAL_CREATED);
            withdrawal.setEventCreatedAt(eventCreatedAtTime);
            withdrawal.setCurrencyCode("RUB");
            withdrawals.add(withdrawal);
            withdrawal.setCurrent(true);
        }
        //filtered by time
        for (Withdrawal withdrawal : randomListOf(4, Withdrawal.class)) {
            fillAsReportWithdrawal(
                    partyId,
                    walletId,
                    eventCreatedAtTime.minusDays(21),
                    withdrawal);
            withdrawals.add(withdrawal);
        }
        return withdrawals;
    }

    public static Report createReport() {
        return createReport(partyId, getToTime(), getFromTime());
    }

    private static dev.vality.fistful.reporter.domain.tables.pojos.Report createReport(
            String partyId,
            LocalDateTime toTime,
            LocalDateTime fromTime) {
        Report report = new Report();
        report.setPartyId(partyId);
        report.setToTime(toTime);
        report.setFromTime(fromTime);
        return report;
    }

    public static SinkEvent sinkEvent(MachineEvent machineEvent) {
        SinkEvent sinkEvent = new SinkEvent();
        sinkEvent.setEvent(machineEvent);
        return sinkEvent;
    }

    public static <T extends TBase> MachineEvent machineEvent(
            ThriftSerializer<T> depositChangeSerializer,
            T change) {
        return new MachineEvent()
                .setEventId(1L)
                .setSourceId("source_id")
                .setSourceNs("source_ns")
                .setCreatedAt("2016-03-22T06:12:27Z")
                .setData(Value.bin(depositChangeSerializer.serialize("", change)));
    }

    private static void fillAsReportWithdrawal(
            String partyId,
            String walletId,
            LocalDateTime eventCreatedAtTime,
            Withdrawal withdrawal) {
        withdrawal.setId(null);
        withdrawal.setWalletId(walletId);
        withdrawal.setWithdrawalStatus(WithdrawalStatus.succeeded);
        withdrawal.setEventType(WithdrawalEventType.WITHDRAWAL_STATUS_CHANGED);
        withdrawal.setEventCreatedAt(eventCreatedAtTime);
        withdrawal.setPartyId(partyId);
        withdrawal.setCurrencyCode("RUB");
        withdrawal.setCurrent(true);
    }
}

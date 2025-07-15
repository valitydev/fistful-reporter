package dev.vality.fistful.reporter.config.testconfiguration;

import dev.vality.fistful.reporter.dao.WithdrawalDao;
import dev.vality.fistful.reporter.domain.tables.pojos.Withdrawal;
import lombok.RequiredArgsConstructor;

import static dev.vality.fistful.reporter.data.TestData.*;
import static dev.vality.testcontainers.annotations.util.ValuesGenerator.getInFromToPeriodTime;

@RequiredArgsConstructor
public class WithdrawalTestDao {

    private final WithdrawalDao withdrawalDao;

    public void saveWithdrawalsDependencies(int expectedSize) {

        for (Withdrawal withdrawal : createWithdrawals(
                partyId,
                walletId,
                getInFromToPeriodTime(),
                expectedSize)) {
            withdrawalDao.save(withdrawal);
        }
    }
}

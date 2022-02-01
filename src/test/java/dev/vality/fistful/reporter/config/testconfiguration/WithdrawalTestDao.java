package dev.vality.fistful.reporter.config.testconfiguration;

import dev.vality.fistful.reporter.dao.IdentityDao;
import dev.vality.fistful.reporter.dao.WalletDao;
import dev.vality.fistful.reporter.dao.WithdrawalDao;
import dev.vality.fistful.reporter.domain.tables.pojos.Identity;
import dev.vality.fistful.reporter.domain.tables.pojos.Wallet;
import dev.vality.fistful.reporter.domain.tables.pojos.Withdrawal;
import lombok.RequiredArgsConstructor;

import static dev.vality.fistful.reporter.data.TestData.*;
import static dev.vality.testcontainers.annotations.util.ValuesGenerator.getInFromToPeriodTime;

@RequiredArgsConstructor
public class WithdrawalTestDao {

    private final IdentityDao identityDao;
    private final WalletDao walletDao;
    private final WithdrawalDao withdrawalDao;

    public void saveWithdrawalsDependencies(int expectedSize) {
        for (Identity identity : createIdentities(identityId, partyId, contractId)) {
            identityDao.save(identity);
        }
        for (Wallet wallet : createWallets(identityId, partyId, contractId, walletId)) {
            walletDao.save(wallet);
        }
        for (Withdrawal withdrawal : createWithdrawals(
                identityId,
                partyId,
                contractId,
                walletId,
                getInFromToPeriodTime(),
                expectedSize)) {
            withdrawalDao.save(withdrawal);
        }
    }
}

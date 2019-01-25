package com.rbkmoney.fistful.reporter;

import com.rbkmoney.fistful.reporter.dao.IdentityDao;
import com.rbkmoney.fistful.reporter.dao.WalletDao;
import com.rbkmoney.fistful.reporter.dao.WithdrawalDao;
import com.rbkmoney.fistful.reporter.domain.enums.WithdrawalEventType;
import com.rbkmoney.fistful.reporter.domain.enums.WithdrawalStatus;
import com.rbkmoney.fistful.reporter.domain.tables.pojos.Identity;
import com.rbkmoney.fistful.reporter.domain.tables.pojos.Report;
import com.rbkmoney.fistful.reporter.domain.tables.pojos.Wallet;
import com.rbkmoney.fistful.reporter.domain.tables.pojos.Withdrawal;
import com.rbkmoney.fistful.reporter.exception.DaoException;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

import static io.github.benas.randombeans.api.EnhancedRandom.randomListOf;

public abstract class AbstractWithdrawalTest extends AbstractIntegrationTest {

    protected String identityId = generateString();
    protected String partyId = generateString();
    protected String contractId = generateString();
    protected String walletId = generateString();

    @Autowired
    protected WithdrawalDao withdrawalDao;

    @Autowired
    protected IdentityDao identityDao;

    @Autowired
    protected WalletDao walletDao;

    protected Report report;

    protected abstract int getExpectedSize();

    @Before
    public void setUp() throws Exception {
        saveIdentities(identityId, partyId, contractId);
        saveWallets(identityId, walletId);
        saveWithdrawals(walletId, inFromToPeriodTime);
        report = createReport(partyId, contractId, toTime, fromTime);
    }

    private com.rbkmoney.fistful.reporter.domain.tables.pojos.Report createReport(String partyId, String contractId, LocalDateTime toTime, LocalDateTime fromTime) {
        com.rbkmoney.fistful.reporter.domain.tables.pojos.Report report = new Report();
        report.setPartyId(partyId);
        report.setContractId(contractId);
        report.setToTime(toTime);
        report.setFromTime(fromTime);
        return report;
    }

    private void saveIdentities(String identityId, String partyId, String contractId) throws DaoException {
        for (Identity identity : randomListOf(2, Identity.class)) {
            identity.setPartyId(partyId);
            identity.setPartyContractId(contractId);
            identity.setIdentityId(identityId);
            identityDao.save(identity);
        }
        for (Identity identity : randomListOf(4, Identity.class)) {
            identity.setIdentityId(identityId);
            identityDao.save(identity);
        }
        for (Identity identity : randomListOf(4, Identity.class)) {
            identityDao.save(identity);
        }
    }

    private void saveWallets(String identityId, String walletId) throws DaoException {
        for (Wallet wallet : randomListOf(2, Wallet.class)) {
            wallet.setIdentityId(identityId);
            wallet.setWalletId(walletId);
            walletDao.save(wallet);
        }
        for (Wallet wallet : randomListOf(4, Wallet.class)) {
            walletDao.save(wallet);
        }
    }

    private void saveWithdrawals(String walletId, LocalDateTime eventCreatedAtTime) throws DaoException {
        for (Withdrawal withdrawal : randomListOf(getExpectedSize(), Withdrawal.class)) {
            withdrawal.setId(null);
            withdrawal.setWalletId(walletId);
            withdrawal.setWithdrawalStatus(WithdrawalStatus.succeeded);
            withdrawal.setEventType(WithdrawalEventType.WITHDRAWAL_STATUS_CHANGED);
            withdrawal.setEventCreatedAt(eventCreatedAtTime);
            withdrawalDao.save(withdrawal);
        }
        for (Withdrawal withdrawal : randomListOf(4, Withdrawal.class)) {
            withdrawal.setId(null);
            withdrawalDao.save(withdrawal);
        }
        for (Withdrawal withdrawal : randomListOf(4, Withdrawal.class)) {
            withdrawal.setId(null);
            withdrawal.setWalletId(walletId);
            withdrawal.setWithdrawalStatus(WithdrawalStatus.pending);
            withdrawal.setEventType(WithdrawalEventType.WITHDRAWAL_STATUS_CHANGED);
            withdrawal.setEventCreatedAt(eventCreatedAtTime);
            withdrawalDao.save(withdrawal);
        }
        for (Withdrawal withdrawal : randomListOf(4, Withdrawal.class)) {
            withdrawal.setId(null);
            withdrawal.setWalletId(walletId);
            withdrawal.setWithdrawalStatus(WithdrawalStatus.succeeded);
            withdrawal.setEventType(WithdrawalEventType.WITHDRAWAL_CREATED);
            withdrawal.setEventCreatedAt(eventCreatedAtTime);
            withdrawalDao.save(withdrawal);
        }
        for (Withdrawal withdrawal : randomListOf(4, Withdrawal.class)) {
            withdrawal.setId(null);
            withdrawal.setWalletId(walletId);
            withdrawal.setWithdrawalStatus(WithdrawalStatus.succeeded);
            withdrawal.setEventType(WithdrawalEventType.WITHDRAWAL_STATUS_CHANGED);
            withdrawal.setEventCreatedAt(eventCreatedAtTime.minusDays(21));
            withdrawalDao.save(withdrawal);
        }
    }
}

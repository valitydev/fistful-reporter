package dev.vality.fistful.reporter.service;

import dev.vality.dao.DaoException;
import dev.vality.fistful.reporter.dao.WithdrawalDao;
import dev.vality.fistful.reporter.domain.tables.pojos.Report;
import dev.vality.fistful.reporter.domain.tables.pojos.Withdrawal;
import dev.vality.fistful.reporter.exception.StorageException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class WithdrawalService {

    public static final int WITHDRAWAL_LIMIT = 5000;

    private final WithdrawalDao withdrawalDao;

    @Transactional(propagation = Propagation.REQUIRED)
    public List<Withdrawal> getSucceededLimitWithdrawals(Report report, long fromId) {
        try {
            log.info("Trying to get succeeded withdrawals, " +
                            "reportId={}, partyId={}, fromId={}",
                    report.getId(), report.getPartyId(), fromId);

            List<Withdrawal> withdrawals = withdrawalDao.getSucceededWithdrawals(report, fromId, WITHDRAWAL_LIMIT);

            log.info("{} succeeded withdrawals has been found, " +
                            "reportId={}, partyId={}, fromId={}",
                    withdrawals.size(), report.getId(), report.getPartyId(), fromId);

            return withdrawals;
        } catch (DaoException ex) {
            throw new StorageException("Failed to get succeeded withdrawals", ex);
        }
    }
}

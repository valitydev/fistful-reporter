package com.rbkmoney.fistful.reporter.service.impl;

import com.rbkmoney.fistful.reporter.dao.WithdrawalDao;
import com.rbkmoney.fistful.reporter.domain.tables.pojos.Report;
import com.rbkmoney.fistful.reporter.domain.tables.pojos.Withdrawal;
import com.rbkmoney.fistful.reporter.exception.DaoException;
import com.rbkmoney.fistful.reporter.exception.StorageException;
import com.rbkmoney.fistful.reporter.service.WithdrawalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class WithdrawalServiceImpl implements WithdrawalService {

    private final WithdrawalDao withdrawalDao;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public List<Withdrawal> getSucceededWithdrawalsByReport(Report report) throws StorageException {
        try {
            log.info("Trying to get succeeded withdrawals by report, " +
                            "reportId={}, partyId={}, contractId={}",
                    report.getId(), report.getPartyId(), report.getContractId()
            );
            long fromId = 0L;
            int limit = 1000;
            List<Withdrawal> pendingReports = withdrawalDao.getSucceededWithdrawalsByReport(report, fromId, limit);
            List<Withdrawal> allPendingReports = new ArrayList<>(pendingReports);
            while (pendingReports.size() == limit) {
                fromId += limit;
                pendingReports = withdrawalDao.getSucceededWithdrawalsByReport(report, fromId, limit);
                allPendingReports.addAll(pendingReports);
            }
            log.info("{} succeeded withdrawals by report have been found", allPendingReports.size());
            return allPendingReports;
        } catch (DaoException ex) {
            throw new StorageException("Failed to get pending reports", ex);
        }
    }
}

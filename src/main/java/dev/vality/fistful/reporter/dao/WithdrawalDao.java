package dev.vality.fistful.reporter.dao;

import dev.vality.fistful.reporter.domain.tables.pojos.Report;
import dev.vality.fistful.reporter.domain.tables.pojos.Withdrawal;

import java.util.List;

public interface WithdrawalDao extends FistfulDao<Withdrawal> {

    List<Withdrawal> getSucceededWithdrawals(Report report, Long fromId, int limit);

}

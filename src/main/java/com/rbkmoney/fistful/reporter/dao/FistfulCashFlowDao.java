package com.rbkmoney.fistful.reporter.dao;

import com.rbkmoney.fistful.reporter.domain.enums.FistfulCashFlowChangeType;
import com.rbkmoney.fistful.reporter.domain.tables.pojos.FistfulCashFlow;
import com.rbkmoney.fistful.reporter.exception.DaoException;

import java.util.List;

public interface FistfulCashFlowDao extends GenericDao {

    void save(List<FistfulCashFlow> fistfulCashFlowList) throws DaoException;

    List<FistfulCashFlow> getByObjId(Long objId, FistfulCashFlowChangeType changeType) throws DaoException;

}

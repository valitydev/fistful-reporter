package com.rbkmoney.fistfulreporter.dao;

import com.rbkmoney.fistfulreporter.domain.enums.FistfulCashFlowChangeType;
import com.rbkmoney.fistfulreporter.domain.tables.pojos.FistfulCashFlow;
import com.rbkmoney.fistfulreporter.exception.DaoException;

import java.util.List;

public interface FistfulCashFlowDao extends GenericDao {

    void save(List<FistfulCashFlow> fistfulCashFlowList) throws DaoException;

    List<FistfulCashFlow> getByObjId(Long objId, FistfulCashFlowChangeType changeType) throws DaoException;

}

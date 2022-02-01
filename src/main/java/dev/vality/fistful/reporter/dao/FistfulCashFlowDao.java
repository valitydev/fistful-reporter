package dev.vality.fistful.reporter.dao;

import dev.vality.dao.GenericDao;
import dev.vality.fistful.reporter.domain.enums.FistfulCashFlowChangeType;
import dev.vality.fistful.reporter.domain.tables.pojos.FistfulCashFlow;

import java.util.List;

public interface FistfulCashFlowDao extends GenericDao {

    void save(List<FistfulCashFlow> fistfulCashFlowList);

    List<FistfulCashFlow> getByObjId(Long objId, FistfulCashFlowChangeType changeType);

}

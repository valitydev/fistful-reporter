package com.rbkmoney.fistful.reporter.dao.impl;

import com.rbkmoney.fistful.reporter.dao.FistfulCashFlowDao;
import com.rbkmoney.fistful.reporter.dao.mapper.RecordRowMapper;
import com.rbkmoney.fistful.reporter.domain.enums.FistfulCashFlowChangeType;
import com.rbkmoney.fistful.reporter.domain.tables.pojos.FistfulCashFlow;
import com.rbkmoney.fistful.reporter.domain.tables.records.FistfulCashFlowRecord;
import com.rbkmoney.fistful.reporter.exception.DaoException;
import org.jooq.Query;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.List;

@Component
public class FistfulCashFlowDaoImpl extends AbstractGenericDao implements FistfulCashFlowDao {

    private final RowMapper<FistfulCashFlow> cashFlowRowMapper;

    public FistfulCashFlowDaoImpl(DataSource dataSource) {
        super(dataSource);
        cashFlowRowMapper = new RecordRowMapper<>(com.rbkmoney.fistful.reporter.domain.tables.FistfulCashFlow.FISTFUL_CASH_FLOW, FistfulCashFlow.class);
    }

    @Override
    public void save(List<FistfulCashFlow> cashFlowList) throws DaoException {
        for (FistfulCashFlow paymentCashFlow : cashFlowList) {
            FistfulCashFlowRecord record = getDslContext().newRecord(com.rbkmoney.fistful.reporter.domain.tables.FistfulCashFlow.FISTFUL_CASH_FLOW, paymentCashFlow);
            Query query = getDslContext().insertInto(com.rbkmoney.fistful.reporter.domain.tables.FistfulCashFlow.FISTFUL_CASH_FLOW).set(record);
            executeOne(query);
        }
    }

    @Override
    public List<FistfulCashFlow> getByObjId(Long objId, FistfulCashFlowChangeType cashFlowChangeType) throws DaoException {
        Query query = getDslContext().selectFrom(com.rbkmoney.fistful.reporter.domain.tables.FistfulCashFlow.FISTFUL_CASH_FLOW)
                .where(com.rbkmoney.fistful.reporter.domain.tables.FistfulCashFlow.FISTFUL_CASH_FLOW.OBJ_ID.eq(objId))
                .and(com.rbkmoney.fistful.reporter.domain.tables.FistfulCashFlow.FISTFUL_CASH_FLOW.OBJ_TYPE.eq(cashFlowChangeType));
        return fetch(query, cashFlowRowMapper);
    }
}

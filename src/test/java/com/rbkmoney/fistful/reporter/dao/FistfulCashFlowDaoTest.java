package com.rbkmoney.fistful.reporter.dao;

import com.rbkmoney.fistful.reporter.AbstractIntegrationTest;
import com.rbkmoney.fistful.reporter.domain.enums.FistfulCashFlowChangeType;
import com.rbkmoney.fistful.reporter.domain.tables.pojos.FistfulCashFlow;
import com.rbkmoney.fistful.reporter.exception.DaoException;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static io.github.benas.randombeans.api.EnhancedRandom.randomListOf;
import static org.junit.Assert.assertEquals;

public class FistfulCashFlowDaoTest extends AbstractIntegrationTest {

    @Autowired
    private FistfulCashFlowDao fistfulCashFlowDao;

    private final FistfulCashFlowChangeType objType = FistfulCashFlowChangeType.deposit;

    @Test
    public void test() throws DaoException {
        int size = 4;
        long objId = generateLong();
        List<FistfulCashFlow> fistfulCashFlows = randomListOf(size, FistfulCashFlow.class);
        for (FistfulCashFlow fistfulCashFlow : fistfulCashFlows) {
            fistfulCashFlow.setObjId(objId);
            fistfulCashFlow.setObjType(objType);
        }
        fistfulCashFlowDao.save(fistfulCashFlows);
        assertEquals(size, fistfulCashFlowDao.getByObjId(objId, objType).size());
    }
}

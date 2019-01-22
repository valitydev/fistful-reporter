package com.rbkmoney.fistful.reporter.dao;

import com.rbkmoney.fistful.reporter.AbstractIntegrationTest;
import com.rbkmoney.fistful.reporter.domain.enums.FistfulCashFlowChangeType;
import com.rbkmoney.fistful.reporter.domain.tables.pojos.FistfulCashFlow;
import com.rbkmoney.fistful.reporter.exception.DaoException;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static io.github.benas.randombeans.api.EnhancedRandom.randomListOf;

public class FistfulCashFlowDaoTest extends AbstractIntegrationTest {

    @Autowired
    private FistfulCashFlowDao fistfulCashFlowDao;

    private final FistfulCashFlowChangeType objType = FistfulCashFlowChangeType.deposit;
    private final long objId = 0L;
    private final int size = 4;

    @Test
    public void test() throws DaoException {
        List<FistfulCashFlow> fistfulCashFlows = randomListOf(size, FistfulCashFlow.class);
        for (FistfulCashFlow fistfulCashFlow : fistfulCashFlows) {
            fistfulCashFlow.setObjId(objId);
            fistfulCashFlow.setObjType(objType);
        }
        fistfulCashFlowDao.save(fistfulCashFlows);
        Assert.assertEquals(size, fistfulCashFlowDao.getByObjId(objId, objType).size());
    }
}

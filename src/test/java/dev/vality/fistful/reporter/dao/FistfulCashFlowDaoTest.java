package dev.vality.fistful.reporter.dao;

import dev.vality.fistful.reporter.config.PostgresqlSpringBootITest;
import dev.vality.fistful.reporter.domain.enums.FistfulCashFlowChangeType;
import dev.vality.fistful.reporter.domain.tables.pojos.FistfulCashFlow;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static dev.vality.testcontainers.annotations.util.RandomBeans.randomListOf;
import static dev.vality.testcontainers.annotations.util.ValuesGenerator.generateLong;
import static org.junit.jupiter.api.Assertions.assertEquals;

@PostgresqlSpringBootITest
public class FistfulCashFlowDaoTest {

    @Autowired
    private FistfulCashFlowDao fistfulCashFlowDao;

    @Test
    public void fistfulCashFlowDaoTest() {
        FistfulCashFlowChangeType objType = FistfulCashFlowChangeType.deposit;
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

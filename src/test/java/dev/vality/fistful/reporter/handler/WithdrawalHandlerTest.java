package dev.vality.fistful.reporter.handler;

import dev.vality.fistful.reporter.config.PostgresqlSpringBootITest;
import dev.vality.fistful.reporter.dao.WithdrawalDao;
import dev.vality.fistful.reporter.dao.mapper.RecordRowMapper;
import dev.vality.fistful.reporter.domain.tables.pojos.Withdrawal;
import dev.vality.fistful.reporter.handler.withdrawal.WithdrawalAdjustmentCreatedHandler;
import dev.vality.fistful.reporter.handler.withdrawal.WithdrawalAdjustmentSucceededHandler;
import dev.vality.fistful.reporter.handler.withdrawal.WithdrawalBodyChangedHandler;
import dev.vality.fistful.reporter.handler.withdrawal.WithdrawalRouteChangeHandler;
import dev.vality.fistful.reporter.handler.withdrawal.WithdrawalStatusChangedHandler;
import dev.vality.fistful.reporter.handler.withdrawal.WithdrawalTransferCreatedHandler;
import dev.vality.fistful.reporter.handler.withdrawal.WithdrawalTransferStatusChangedHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import static dev.vality.fistful.reporter.domain.tables.Withdrawal.WITHDRAWAL;
import static dev.vality.fistful.reporter.util.handler.WithdrawalHandlerTestUtil.*;
import static dev.vality.testcontainers.annotations.util.RandomBeans.random;
import static org.junit.jupiter.api.Assertions.assertEquals;

@PostgresqlSpringBootITest
public class WithdrawalHandlerTest {

    @Autowired
    private WithdrawalBodyChangedHandler withdrawalBodyChangedHandler;

    @Autowired
    private WithdrawalAdjustmentCreatedHandler withdrawalAdjustmentCreatedHandler;

    @Autowired
    private WithdrawalAdjustmentSucceededHandler withdrawalAdjustmentSucceededHandler;

    @Autowired
    private WithdrawalStatusChangedHandler withdrawalStatusChangedHandler;

    @Autowired
    private WithdrawalTransferStatusChangedHandler withdrawalTransferStatusChangedHandler;

    @Autowired
    private WithdrawalTransferCreatedHandler withdrawalTransferCreatedHandler;

    @Autowired
    private WithdrawalRouteChangeHandler withdrawalRouteChangeHandler;

    @Autowired
    private WithdrawalDao withdrawalDao;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    Withdrawal withdrawal = random(Withdrawal.class);
    String sqlStatement = "select * from fr.withdrawal where id='" + withdrawal.getId() + "';";

    @BeforeEach
    public void setUp() {
        withdrawal.setCurrent(true);
        withdrawal.setAmount(10000L);
        withdrawal.setCurrencyCode("RUB");
        withdrawal.setFee(1000L);
        withdrawalDao.save(withdrawal);
    }

    @Test
    public void withdrawalStatusChangedHandlerTest() {
        withdrawalStatusChangedHandler.handle(createStatusChanged(), createMachineEvent(withdrawal.getWithdrawalId()));
        assertEquals(2L, withdrawalDao.get(withdrawal.getWithdrawalId()).getEventId().longValue());
        assertEquals(
                false,
                jdbcTemplate.queryForObject(sqlStatement,
                        new RecordRowMapper<>(WITHDRAWAL, Withdrawal.class)).getCurrent()
        );
    }

    @Test
    public void withdrawalTransferStatusChangedHandlerTest() {
        withdrawalTransferStatusChangedHandler.handle(
                createTransferChanged(),
                createMachineEvent(withdrawal.getWithdrawalId()));
        assertEquals(2L, withdrawalDao.get(withdrawal.getWithdrawalId()).getEventId().longValue());
        assertEquals(
                false,
                jdbcTemplate.queryForObject(sqlStatement,
                        new RecordRowMapper<>(WITHDRAWAL, Withdrawal.class)).getCurrent()
        );
    }

    @Test
    public void withdrawalTransferCreatedHandlerTest() {
        withdrawalTransferCreatedHandler.handle(
                createTransferCreated(),
                createMachineEvent(withdrawal.getWithdrawalId()));
        assertEquals(2L, withdrawalDao.get(withdrawal.getWithdrawalId()).getEventId().longValue());
        assertEquals(
                false,
                jdbcTemplate.queryForObject(sqlStatement,
                        new RecordRowMapper<>(WITHDRAWAL, Withdrawal.class)).getCurrent()
        );
    }

    @Test
    public void withdrawalRouteChangeHandlerTest() {
        withdrawalRouteChangeHandler.handle(createTransferCreated(), createMachineEvent(withdrawal.getWithdrawalId()));
        assertEquals(2L, withdrawalDao.get(withdrawal.getWithdrawalId()).getEventId().longValue());
        assertEquals(
                false,
                jdbcTemplate.queryForObject(
                        sqlStatement, new RecordRowMapper<>(WITHDRAWAL, Withdrawal.class)).getCurrent()
        );
    }

    @Test
    public void withdrawalBodyChangedHandlerTest() {
        withdrawalBodyChangedHandler.handle(
                createBodyChanged(),
                createMachineEvent(withdrawal.getWithdrawalId()));

        Withdrawal updatedWithdrawal = withdrawalDao.get(withdrawal.getWithdrawalId());
        assertEquals(2L, updatedWithdrawal.getEventId().longValue());
        assertEquals(75L, updatedWithdrawal.getAmount().longValue());
        assertEquals("USD", updatedWithdrawal.getCurrencyCode());
        assertEquals(
                false,
                jdbcTemplate.queryForObject(
                        sqlStatement, new RecordRowMapper<>(WITHDRAWAL, Withdrawal.class)).getCurrent()
        );
    }

    @Test
    public void withdrawalAdjustmentUpdatesFeeOnlyAfterSucceededTest() {
        withdrawalAdjustmentCreatedHandler.handle(
                createAdjustmentCreated(), createMachineEvent(withdrawal.getWithdrawalId(), 2L));

        Withdrawal pendingWithdrawal = withdrawalDao.get(withdrawal.getWithdrawalId());
        assertEquals(10000L, pendingWithdrawal.getAmount().longValue());
        assertEquals(1000L, pendingWithdrawal.getFee().longValue());

        withdrawalAdjustmentSucceededHandler.handle(
                createAdjustmentSucceeded(), createMachineEvent(withdrawal.getWithdrawalId(), 3L));

        Withdrawal succeededWithdrawal = withdrawalDao.get(withdrawal.getWithdrawalId());
        assertEquals(20000L, succeededWithdrawal.getAmount().longValue());
        assertEquals(2000L, succeededWithdrawal.getFee().longValue());

        withdrawalBodyChangedHandler.handle(
                createAdjustmentBodyChanged(), createMachineEvent(withdrawal.getWithdrawalId(), 4L));

        assertEquals(2000L, withdrawalDao.get(withdrawal.getWithdrawalId()).getFee().longValue());
    }
}

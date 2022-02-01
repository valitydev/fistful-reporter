package dev.vality.fistful.reporter.handler;

import dev.vality.fistful.reporter.config.PostgresqlSpringBootITest;
import dev.vality.fistful.reporter.dao.WithdrawalDao;
import dev.vality.fistful.reporter.dao.mapper.RecordRowMapper;
import dev.vality.fistful.reporter.domain.tables.pojos.Withdrawal;
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
}

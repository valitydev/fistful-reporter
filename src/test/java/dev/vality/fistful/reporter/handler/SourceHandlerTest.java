package dev.vality.fistful.reporter.handler;

import dev.vality.fistful.account.Account;
import dev.vality.fistful.reporter.config.PostgresqlSpringBootITest;
import dev.vality.fistful.reporter.dao.SourceDao;
import dev.vality.fistful.reporter.dao.mapper.RecordRowMapper;
import dev.vality.fistful.reporter.domain.tables.pojos.Source;
import dev.vality.fistful.reporter.handler.source.SourceStatusChangedHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import static dev.vality.fistful.reporter.domain.tables.Source.SOURCE;
import static dev.vality.fistful.reporter.util.handler.SourceHandlerTestUtil.createMachineEvent;
import static dev.vality.fistful.reporter.util.handler.SourceHandlerTestUtil.createStatusChanged;
import static dev.vality.testcontainers.annotations.util.RandomBeans.random;
import static org.junit.jupiter.api.Assertions.assertEquals;

@PostgresqlSpringBootITest
public class SourceHandlerTest {

    @Autowired
    private SourceStatusChangedHandler sourceStatusChangedHandler;

    @Autowired
    private SourceDao sourceDao;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    Source source = random(Source.class);
    Account account = random(Account.class);
    String sqlStatement = "select * from fr.source where id='" + source.getId() + "';";

    @Test
    public void sourceAccountCreatedHandlerTest() {
        source.setCurrent(true);
        sourceDao.save(source);
        sourceStatusChangedHandler.handle(createStatusChanged(account),
                createMachineEvent(source.getSourceId(), account));
        assertEquals(2L, sourceDao.get(source.getSourceId()).getEventId().longValue());
        assertEquals(
                false,
                jdbcTemplate.queryForObject(sqlStatement,
                        new RecordRowMapper<>(SOURCE, Source.class)).getCurrent()
        );
    }
}

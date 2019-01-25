package com.rbkmoney.fistful.reporter.service.impl;

import com.rbkmoney.fistful.account.Account;
import com.rbkmoney.fistful.reporter.AbstractIntegrationTest;
import com.rbkmoney.fistful.reporter.dao.IdentityDao;
import com.rbkmoney.fistful.reporter.domain.tables.pojos.Identity;
import com.rbkmoney.fistful.source.*;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

import java.util.List;

import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.when;

public class SourceEventServiceTest extends AbstractIntegrationTest {

    @MockBean
    private IdentityDao identityDao;

    @Autowired
    private SourceEventService eventService;

    @Before
    public void setUp() throws Exception {
        when(identityDao.get(anyString())).thenReturn(random(Identity.class));
    }

    @Test
    public void test() {
        String sourceId = generateString();

        List<Change> changes = asList(
                createCreatedChange(),
                createStatusChangedChange(),
                createAccountCreatedChange()
        );

        Event event = new Event(generateInt(), generateDate(), changes);

        SinkEvent sinkEvent = new SinkEvent(
                generateLong(),
                generateDate(),
                sourceId,
                event
        );

        eventService.processSinkEvent(sinkEvent);

        List<com.rbkmoney.fistful.reporter.domain.tables.pojos.Source> sources = jdbcTemplate.query(
                "SELECT * FROM fr.source AS source WHERE source.source_id = ?",
                new Object[]{sourceId},
                new BeanPropertyRowMapper<>(com.rbkmoney.fistful.reporter.domain.tables.pojos.Source.class)
        );
        assertEquals(3, sources.size());

        sources = jdbcTemplate.query(
                "SELECT * FROM fr.source AS source WHERE source.source_id = ? AND source.current",
                new Object[]{sourceId},
                new BeanPropertyRowMapper<>(com.rbkmoney.fistful.reporter.domain.tables.pojos.Source.class)
        );
        assertEquals(1, sources.size());
    }

    private Change createCreatedChange() {
        return Change.created(
                new com.rbkmoney.fistful.source.Source(
                        generateString(),
                        Resource.internal(new Internal())
                )
        );
    }

    private Change createStatusChangedChange() {
        return Change.status(StatusChange.changed(Status.authorized(new Authorized())));
    }

    private Change createAccountCreatedChange() {
        return Change.account(AccountChange.created(random(Account.class)));
    }
}

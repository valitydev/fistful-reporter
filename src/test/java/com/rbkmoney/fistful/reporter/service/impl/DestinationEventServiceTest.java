package com.rbkmoney.fistful.reporter.service.impl;

import com.rbkmoney.fistful.account.Account;
import com.rbkmoney.fistful.base.BankCard;
import com.rbkmoney.fistful.destination.*;
import com.rbkmoney.fistful.reporter.AbstractIntegrationTest;
import com.rbkmoney.fistful.reporter.dao.IdentityDao;
import com.rbkmoney.fistful.reporter.domain.tables.pojos.Identity;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

import java.util.Arrays;
import java.util.List;

import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static org.junit.Assert.assertEquals;

public class DestinationEventServiceTest extends AbstractIntegrationTest {

    @MockBean
    private IdentityDao identityDao;

    @Autowired
    private DestinationEventService eventService;

    @Before
    public void setUp() throws Exception {
        Mockito.when(identityDao.get(Mockito.anyString())).thenReturn(random(Identity.class));
    }

    @Test
    public void test() {
        String destinationId = generateString();

        List<Change> changes = Arrays.asList(
                createCreatedChange(),
                createStatusChangedChange(),
                createAccountCreatedChange()
        );

        Event event = new Event(generateInt(), generateDate(), changes);

        SinkEvent sinkEvent = new SinkEvent(
                generateLong(),
                generateDate(),
                destinationId,
                event
        );

        eventService.processSinkEvent(sinkEvent);

        List<com.rbkmoney.fistful.reporter.domain.tables.pojos.Destination> deposits = jdbcTemplate.query(
                "SELECT * FROM fr.destination AS destination WHERE destination.destination_id = ?",
                new Object[]{destinationId},
                new BeanPropertyRowMapper<>(com.rbkmoney.fistful.reporter.domain.tables.pojos.Destination.class)
        );
        assertEquals(3, deposits.size());

        deposits = jdbcTemplate.query(
                "SELECT * FROM fr.destination AS destination WHERE destination.destination_id = ? AND destination.current",
                new Object[]{destinationId},
                new BeanPropertyRowMapper<>(com.rbkmoney.fistful.reporter.domain.tables.pojos.Destination.class)
        );
        assertEquals(1, deposits.size());
    }

    private Change createAccountCreatedChange() {
        return Change.account(AccountChange.created(random(Account.class)));
    }

    private Change createStatusChangedChange() {
        return Change.status(StatusChange.changed(Status.authorized(new Authorized())));
    }

    private Change createCreatedChange() {
        return Change.created(
                new com.rbkmoney.fistful.destination.Destination(
                        generateString(),
                        Resource.bank_card(random(BankCard.class))
                )
        );
    }
}
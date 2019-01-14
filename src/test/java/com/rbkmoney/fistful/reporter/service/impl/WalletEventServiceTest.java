package com.rbkmoney.fistful.reporter.service.impl;

import com.rbkmoney.fistful.account.Account;
import com.rbkmoney.fistful.reporter.AbstractIntegrationTest;
import com.rbkmoney.fistful.reporter.dao.IdentityDao;
import com.rbkmoney.fistful.reporter.domain.tables.pojos.Identity;
import com.rbkmoney.fistful.reporter.domain.tables.pojos.Wallet;
import com.rbkmoney.fistful.wallet.AccountChange;
import com.rbkmoney.fistful.wallet.Change;
import com.rbkmoney.fistful.wallet.Event;
import com.rbkmoney.fistful.wallet.SinkEvent;
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

public class WalletEventServiceTest extends AbstractIntegrationTest {

    @MockBean
    private IdentityDao identityDao;

    @Autowired
    private WalletEventService eventService;

    @Before
    public void setUp() throws Exception {
        Mockito.when(identityDao.get(Mockito.anyString())).thenReturn(random(Identity.class));
    }

    @Test
    public void test() {
        List<Change> changes = Arrays.asList(
                createCreatedChange(),
                createAccountCreatedChange()
        );

        String walletId = generateString();

        Event event = new Event(generateInt(), generateDate(), changes);

        SinkEvent sinkEvent = new SinkEvent(
                generateLong(),
                generateDate(),
                walletId,
                event
        );

        eventService.processSinkEvent(sinkEvent);

        List<Wallet> wallets = jdbcTemplate.query(
                "SELECT * FROM fr.wallet AS wallet WHERE wallet.wallet_id = ?",
                new Object[]{walletId},
                new BeanPropertyRowMapper<>(Wallet.class)
        );
        assertEquals(2, wallets.size());

        wallets = jdbcTemplate.query(
                "SELECT * FROM fr.wallet AS wallet WHERE wallet.wallet_id = ? AND wallet.current",
                new Object[]{walletId},
                new BeanPropertyRowMapper<>(Wallet.class)
        );
        assertEquals(1, wallets.size());
    }

    private Change createCreatedChange() {
        return Change.created(random(com.rbkmoney.fistful.wallet.Wallet.class));
    }

    private Change createAccountCreatedChange() {
        return Change.account(AccountChange.created(random(Account.class)));
    }
}
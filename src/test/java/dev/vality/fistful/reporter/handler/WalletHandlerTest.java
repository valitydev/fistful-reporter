package dev.vality.fistful.reporter.handler;

import dev.vality.fistful.account.Account;
import dev.vality.fistful.reporter.config.PostgresqlSpringBootITest;
import dev.vality.fistful.reporter.dao.IdentityDao;
import dev.vality.fistful.reporter.dao.WalletDao;
import dev.vality.fistful.reporter.dao.mapper.RecordRowMapper;
import dev.vality.fistful.reporter.domain.tables.pojos.Identity;
import dev.vality.fistful.reporter.domain.tables.pojos.Wallet;
import dev.vality.fistful.reporter.handler.wallet.WalletAccountCreatedHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import static dev.vality.fistful.reporter.domain.tables.Wallet.WALLET;
import static dev.vality.fistful.reporter.util.handler.WalletHandlerTestUtil.createAccountCreated;
import static dev.vality.fistful.reporter.util.handler.WalletHandlerTestUtil.createMachineEvent;
import static dev.vality.testcontainers.annotations.util.RandomBeans.random;
import static org.junit.jupiter.api.Assertions.assertEquals;

@PostgresqlSpringBootITest
public class WalletHandlerTest {

    @Autowired
    private WalletAccountCreatedHandler walletAccountCreatedHandler;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private WalletDao walletDao;

    @Autowired
    private IdentityDao identityDao;

    Wallet wallet = random(Wallet.class);
    Identity identity = random(Identity.class);
    Account account = random(Account.class);
    String sqlStatement = "select * from fr.wallet where id='" + wallet.getId() + "';";

    @Test
    public void walletAccountCreatedHandlerTest() {
        wallet.setCurrent(true);
        walletDao.save(wallet);
        identity.setIdentityId(wallet.getIdentityId());
        identity.setCurrent(true);
        identityDao.save(identity);
        account.setIdentity(wallet.getIdentityId());
        walletAccountCreatedHandler.handle(
                createAccountCreated(account),
                createMachineEvent(wallet.getWalletId(), account));
        assertEquals(2L, walletDao.get(wallet.getWalletId()).getEventId().longValue());
        assertEquals(
                false,
                jdbcTemplate.queryForObject(sqlStatement,
                        new RecordRowMapper<>(WALLET, Wallet.class)).getCurrent()
        );
    }
}

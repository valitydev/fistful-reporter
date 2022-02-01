package dev.vality.fistful.reporter.config.testconfiguration;

import dev.vality.fistful.reporter.dao.IdentityDao;
import dev.vality.fistful.reporter.dao.WalletDao;
import dev.vality.fistful.reporter.dao.WithdrawalDao;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class WithdrawalTestDaoConfig {

    @Bean
    public WithdrawalTestDao withdrawalTestDao(
            IdentityDao identityDao,
            WalletDao walletDao,
            WithdrawalDao withdrawalDao) {
        return new WithdrawalTestDao(identityDao, walletDao, withdrawalDao);
    }
}

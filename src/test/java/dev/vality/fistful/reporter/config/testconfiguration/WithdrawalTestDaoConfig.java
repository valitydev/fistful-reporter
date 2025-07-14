package dev.vality.fistful.reporter.config.testconfiguration;

import dev.vality.fistful.reporter.dao.WithdrawalDao;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class WithdrawalTestDaoConfig {

    @Bean
    public WithdrawalTestDao withdrawalTestDao(
            WithdrawalDao withdrawalDao) {
        return new WithdrawalTestDao(withdrawalDao);
    }
}

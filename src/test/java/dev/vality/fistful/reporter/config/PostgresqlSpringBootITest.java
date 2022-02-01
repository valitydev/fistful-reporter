package dev.vality.fistful.reporter.config;

import dev.vality.fistful.reporter.config.testconfiguration.WithdrawalTestDaoConfig;
import dev.vality.testcontainers.annotations.DefaultSpringBootTest;
import dev.vality.testcontainers.annotations.postgresql.PostgresqlTestcontainerSingleton;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@PostgresqlTestcontainerSingleton
@DefaultSpringBootTest
@Import(WithdrawalTestDaoConfig.class)
public @interface PostgresqlSpringBootITest {
}

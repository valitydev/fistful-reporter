package dev.vality.fistful.reporter.config;

import dev.vality.testcontainers.annotations.KafkaSpringBootTest;
import dev.vality.testcontainers.annotations.kafka.KafkaTestcontainerSingleton;
import dev.vality.testcontainers.annotations.postgresql.PostgresqlTestcontainerSingleton;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@PostgresqlTestcontainerSingleton
@KafkaTestcontainerSingleton(
        properties = {
                "kafka.topic.deposit.listener.enabled=true",
                "kafka.topic.destination.listener.enabled=true",
                "kafka.topic.source.listener.enabled=true",
                "kafka.topic.withdrawal.listener.enabled=true",
                "kafka.topic.wallet.listener.enabled=true",
                "kafka.topic.identity.listener.enabled=true"},
        topicsKeys = {
                "kafka.topic.deposit.name",
                "kafka.topic.destination.name",
                "kafka.topic.source.name",
                "kafka.topic.withdrawal.name",
                "kafka.topic.wallet.name",
                "kafka.topic.identity.name"})
@KafkaSpringBootTest
public @interface KafkaPostgresqlSpringBootITest {
}

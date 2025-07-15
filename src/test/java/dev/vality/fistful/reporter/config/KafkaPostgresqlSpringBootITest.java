package dev.vality.fistful.reporter.config;

import dev.vality.testcontainers.annotations.KafkaConfig;
import dev.vality.testcontainers.annotations.kafka.KafkaTestcontainer;
import dev.vality.testcontainers.annotations.kafka.KafkaTestcontainerSingleton;
import dev.vality.testcontainers.annotations.postgresql.PostgresqlTestcontainerSingleton;
import org.springframework.boot.test.context.SpringBootTest;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@SpringBootTest
@KafkaConfig
@KafkaTestcontainer(
        properties = {
                "kafka.topic.deposit.listener.enabled=true",
                "kafka.topic.withdrawal.listener.enabled=true"},
        topicsKeys = {
                "kafka.topic.deposit.name",
                "kafka.topic.withdrawal.name"})
@PostgresqlTestcontainerSingleton
public @interface KafkaPostgresqlSpringBootITest {
}

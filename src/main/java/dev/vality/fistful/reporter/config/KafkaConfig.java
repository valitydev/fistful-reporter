package dev.vality.fistful.reporter.config;

import dev.vality.fistful.reporter.kafka.serdes.SinkEventDeserializer;
import dev.vality.machinegun.eventsink.MachineEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.SeekToCurrentBatchErrorHandler;

import java.util.Map;

@Slf4j
@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(KafkaProperties.class)
public class KafkaConfig {

    private final KafkaProperties kafkaProperties;

    @Value("${kafka.consumer.concurrency}")
    private int consumerConcurrency;

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, MachineEvent> depositEventListenerContainerFactory() {
        return listenerContainerFactory();
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, MachineEvent> destinationEventListenerContainerFactory() {
        return listenerContainerFactory();
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, MachineEvent> identityEventListenerContainerFactory() {
        return listenerContainerFactory();
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, MachineEvent> walletEventListenerContainerFactory() {
        return listenerContainerFactory();
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, MachineEvent> withdrawalEventListenerContainerFactory() {
        return listenerContainerFactory();
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, MachineEvent> sourceEventListenerContainerFactory() {
        return listenerContainerFactory();
    }

    private ConcurrentKafkaListenerContainerFactory<String, MachineEvent> listenerContainerFactory() {
        var factory = new ConcurrentKafkaListenerContainerFactory<String, MachineEvent>();
        factory.setConsumerFactory(new DefaultKafkaConsumerFactory<>(consumerConfig()));
        factory.setConcurrency(consumerConcurrency);
        factory.setBatchErrorHandler(new SeekToCurrentBatchErrorHandler());
        factory.setBatchListener(true);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);
        return factory;
    }

    private Map<String, Object> consumerConfig() {
        var properties = kafkaProperties.buildConsumerProperties();
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, SinkEventDeserializer.class);
        return properties;
    }
}

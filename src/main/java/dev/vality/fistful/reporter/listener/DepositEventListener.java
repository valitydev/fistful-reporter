package dev.vality.fistful.reporter.listener;

import dev.vality.fistful.reporter.service.impl.DepositEventService;
import dev.vality.machinegun.eventsink.SinkEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Slf4j
@Service
@RequiredArgsConstructor
public class DepositEventListener {

    private final DepositEventService depositEventService;

    @KafkaListener(
            autoStartup = "${kafka.topic.deposit.listener.enabled}",
            topics = "${kafka.topic.deposit.name}",
            containerFactory = "depositEventListenerContainerFactory")
    public void listen(
            List<SinkEvent> batch,
            @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition,
            @Header(KafkaHeaders.OFFSET) int offset,
            Acknowledgment ack) {
        log.info("Listening Deposit: partition={}, offset={}, batch.size()={}", partition, offset, batch.size());
        depositEventService.handleEvents(batch.stream().map(SinkEvent::getEvent).collect(toList()));
        ack.acknowledge();
        log.info("Ack Deposit: partition={}, offset={}", partition, offset);
    }
}

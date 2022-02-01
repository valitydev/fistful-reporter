package dev.vality.fistful.reporter.kafka.listener;

import dev.vality.dao.DaoException;
import dev.vality.fistful.reporter.config.KafkaPostgresqlSpringBootITest;
import dev.vality.fistful.reporter.dao.WithdrawalDao;
import dev.vality.fistful.reporter.domain.enums.WithdrawalStatus;
import dev.vality.fistful.reporter.domain.tables.pojos.Withdrawal;
import dev.vality.fistful.withdrawal.Change;
import dev.vality.fistful.withdrawal.StatusChange;
import dev.vality.fistful.withdrawal.TimestampedChange;
import dev.vality.fistful.withdrawal.status.Status;
import dev.vality.fistful.withdrawal.status.Succeeded;
import dev.vality.kafka.common.serialization.ThriftSerializer;
import dev.vality.machinegun.eventsink.SinkEvent;
import dev.vality.testcontainers.annotations.kafka.config.KafkaProducer;
import org.apache.thrift.TBase;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;

import static dev.vality.fistful.reporter.data.TestData.machineEvent;
import static dev.vality.fistful.reporter.data.TestData.sinkEvent;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@KafkaPostgresqlSpringBootITest
public class WithdrawalEventListenerTest {

    @Value("${kafka.topic.withdrawal.name}")
    private String topicName;

    @Autowired
    private KafkaProducer<TBase<?, ?>> testThriftKafkaProducer;

    @MockBean
    private WithdrawalDao withdrawalDao;

    @Captor
    private ArgumentCaptor<Withdrawal> captor;

    @Test
    public void shouldListenAndSave() throws InterruptedException, DaoException {
        // Given
        TimestampedChange statusChanged = new TimestampedChange()
                .setOccuredAt("2016-03-22T06:12:27Z")
                .setChange(Change.status_changed(
                        new StatusChange().setStatus(
                                Status.succeeded(new Succeeded()))));

        SinkEvent sinkEvent = sinkEvent(
                machineEvent(
                        new ThriftSerializer<>(),
                        statusChanged));

        when(withdrawalDao.get("source_id"))
                .thenReturn(new Withdrawal());

        // When
        testThriftKafkaProducer.send(topicName, sinkEvent);

        // Then
        verify(withdrawalDao, timeout(5000).times(1))
                .save(captor.capture());
        assertThat(captor.getValue().getWithdrawalStatus())
                .isEqualTo(WithdrawalStatus.succeeded);
    }
}
package dev.vality.fistful.reporter.kafka.listener;

import dev.vality.dao.DaoException;
import dev.vality.fistful.deposit.Change;
import dev.vality.fistful.deposit.StatusChange;
import dev.vality.fistful.deposit.TimestampedChange;
import dev.vality.fistful.deposit.status.Status;
import dev.vality.fistful.deposit.status.Succeeded;
import dev.vality.fistful.reporter.config.KafkaPostgresqlSpringBootITest;
import dev.vality.fistful.reporter.dao.DepositDao;
import dev.vality.fistful.reporter.domain.enums.DepositStatus;
import dev.vality.fistful.reporter.domain.tables.pojos.Deposit;
import dev.vality.kafka.common.serialization.ThriftSerializer;
import dev.vality.machinegun.eventsink.SinkEvent;
import dev.vality.testcontainers.annotations.kafka.config.KafkaProducer;
import org.apache.thrift.TBase;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static dev.vality.fistful.reporter.data.TestData.machineEvent;
import static dev.vality.fistful.reporter.data.TestData.sinkEvent;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

@KafkaPostgresqlSpringBootITest
public class DepositEventListenerTest {

    @Value("${kafka.topic.deposit.name}")
    private String topicName;

    @Autowired
    private KafkaProducer<TBase<?, ?>> testThriftKafkaProducer;

    @MockitoBean
    private DepositDao depositDao;

    @Captor
    private ArgumentCaptor<Deposit> captor;

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

        when(depositDao.get("source_id"))
                .thenReturn(new Deposit());

        // When
        testThriftKafkaProducer.send(topicName, sinkEvent);

        // Then
        verify(depositDao, timeout(15000).times(1))
                .save(captor.capture());
        assertThat(captor.getValue().getDepositStatus())
                .isEqualTo(DepositStatus.succeeded);
    }
}

package dev.vality.fistful.reporter.kafka.listener;

import dev.vality.dao.DaoException;
import dev.vality.fistful.reporter.config.KafkaPostgresqlSpringBootITest;
import dev.vality.fistful.reporter.dao.WalletDao;
import dev.vality.fistful.reporter.domain.tables.pojos.Wallet;
import dev.vality.fistful.wallet.Change;
import dev.vality.fistful.wallet.TimestampedChange;
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
public class WalletEventListenerTest {

    @Value("${kafka.topic.wallet.name}")
    private String topicName;

    @Autowired
    private KafkaProducer<TBase<?, ?>> testThriftKafkaProducer;

    @MockBean
    private WalletDao walletDao;

    @Captor
    private ArgumentCaptor<Wallet> captor;

    @Test
    public void shouldListenAndSave() throws InterruptedException, DaoException {
        // Given
        TimestampedChange created = new TimestampedChange()
                .setOccuredAt("2016-03-22T06:12:27Z")
                .setChange(Change.created(new dev.vality.fistful.wallet.Wallet()
                        .setName("wallet")));

        SinkEvent sinkEvent = sinkEvent(
                machineEvent(
                        new ThriftSerializer<>(),
                        created));

        when(walletDao.get("source_id"))
                .thenReturn(new Wallet());

        // When
        testThriftKafkaProducer.send(topicName, sinkEvent);

        // Then
        verify(walletDao, timeout(5000).times(1))
                .save(captor.capture());
        assertThat(captor.getValue().getWalletName())
                .isEqualTo("wallet");
    }
}

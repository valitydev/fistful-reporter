package com.rbkmoney.fistful.reporter.kafka.listener;

import com.rbkmoney.dao.DaoException;
import com.rbkmoney.fistful.reporter.FistfulReporterApplication;
import com.rbkmoney.fistful.reporter.dao.WalletDao;
import com.rbkmoney.fistful.reporter.domain.tables.pojos.Wallet;
import com.rbkmoney.fistful.wallet.Change;
import com.rbkmoney.fistful.wallet.TimestampedChange;
import com.rbkmoney.kafka.common.serialization.ThriftSerializer;
import com.rbkmoney.machinegun.eventsink.SinkEvent;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@Slf4j
@DirtiesContext
@RunWith(SpringRunner.class)
@SpringBootTest(
        classes = FistfulReporterApplication.class,
        properties = {"kafka.state.cache.size=0"})
public class WalletEventListenerTest extends AbstractListenerTest {

    private static final long MESSAGE_TIMEOUT = 10_000L;

    @MockBean
    private WalletDao walletDao;

    @Captor
    private ArgumentCaptor<Wallet> captor;

    @Test
    public void shouldListenAndSave() throws InterruptedException, DaoException {
        // Given
        TimestampedChange created = new TimestampedChange()
                .setOccuredAt("2016-03-22T06:12:27Z")
                .setChange(Change.created(new com.rbkmoney.fistful.wallet.Wallet()
                        .setName("wallet")));

        SinkEvent sinkEvent = sinkEvent(
                machineEvent(
                        new ThriftSerializer<>(),
                        created));

        when(walletDao.get("source_id"))
                .thenReturn(new Wallet());

        // When
        produce(sinkEvent, "wallet");
        Thread.sleep(MESSAGE_TIMEOUT);

        // Then
        verify(walletDao, times(1))
                .save(captor.capture());
        assertThat(captor.getValue().getWalletName())
                .isEqualTo("wallet");
    }
}

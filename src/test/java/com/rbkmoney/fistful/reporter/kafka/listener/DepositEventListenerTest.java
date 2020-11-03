package com.rbkmoney.fistful.reporter.kafka.listener;

import com.rbkmoney.dao.DaoException;
import com.rbkmoney.fistful.deposit.Change;
import com.rbkmoney.fistful.deposit.StatusChange;
import com.rbkmoney.fistful.deposit.TimestampedChange;
import com.rbkmoney.fistful.deposit.status.Status;
import com.rbkmoney.fistful.deposit.status.Succeeded;
import com.rbkmoney.fistful.reporter.FistfulReporterApplication;
import com.rbkmoney.fistful.reporter.dao.DepositDao;
import com.rbkmoney.fistful.reporter.domain.enums.DepositStatus;
import com.rbkmoney.fistful.reporter.domain.tables.pojos.Deposit;
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
public class DepositEventListenerTest extends AbstractListenerTest {

    private static final long MESSAGE_TIMEOUT = 4_000L;

    @MockBean
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
        produce(sinkEvent, "deposit");
        Thread.sleep(MESSAGE_TIMEOUT);

        // Then
        verify(depositDao, times(1))
                .save(captor.capture());
        assertThat(captor.getValue().getDepositStatus())
                .isEqualTo(DepositStatus.succeeded);
    }
}

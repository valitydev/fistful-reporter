package com.rbkmoney.fistful.reporter.kafka.listener;

import com.rbkmoney.dao.DaoException;
import com.rbkmoney.fistful.identity.Change;
import com.rbkmoney.fistful.identity.TimestampedChange;
import com.rbkmoney.fistful.reporter.FistfulReporterApplication;
import com.rbkmoney.fistful.reporter.dao.IdentityDao;
import com.rbkmoney.fistful.reporter.domain.tables.pojos.Identity;
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
public class IdentityEventListenerTest extends AbstractListenerTest {

    private static final long MESSAGE_TIMEOUT = 10_000L;

    @MockBean
    private IdentityDao identityDao;

    @Captor
    private ArgumentCaptor<Identity> captor;

    @Test
    public void shouldListenAndSave() throws InterruptedException, DaoException {
        // Given
        TimestampedChange levelChanged = new TimestampedChange()
                .setOccuredAt("2016-03-22T06:12:27Z")
                .setChange(Change.level_changed("upd"));

        SinkEvent sinkEvent = sinkEvent(
                machineEvent(
                        new ThriftSerializer<>(),
                        levelChanged));

        when(identityDao.get("source_id"))
                .thenReturn(new Identity());

        // When
        produce(sinkEvent, "identity");
        Thread.sleep(MESSAGE_TIMEOUT);

        // Then
        verify(identityDao, times(1))
                .save(captor.capture());
        assertThat(captor.getValue().getIdentityLevelId())
                .isEqualTo("upd");
    }
}
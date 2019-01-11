package com.rbkmoney.fistfulreporter.poller.handler;

import com.rbkmoney.fistful.destination.Change;
import com.rbkmoney.fistful.destination.SinkEvent;

public interface DestinationEventHandler extends EventHandler<Change, SinkEvent> {
}

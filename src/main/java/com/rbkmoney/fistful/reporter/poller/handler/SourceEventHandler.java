package com.rbkmoney.fistful.reporter.poller.handler;

import com.rbkmoney.fistful.source.Change;
import com.rbkmoney.fistful.source.SinkEvent;

public interface SourceEventHandler extends EventHandler<Change, SinkEvent> {
}

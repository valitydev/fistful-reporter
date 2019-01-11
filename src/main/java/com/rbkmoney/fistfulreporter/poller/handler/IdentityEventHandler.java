package com.rbkmoney.fistfulreporter.poller.handler;

import com.rbkmoney.fistful.identity.Change;
import com.rbkmoney.fistful.identity.SinkEvent;

public interface IdentityEventHandler extends EventHandler<Change, SinkEvent> {
}

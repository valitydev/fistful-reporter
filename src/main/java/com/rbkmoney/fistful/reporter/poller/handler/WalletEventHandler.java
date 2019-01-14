package com.rbkmoney.fistful.reporter.poller.handler;

import com.rbkmoney.fistful.wallet.Change;
import com.rbkmoney.fistful.wallet.SinkEvent;

public interface WalletEventHandler extends EventHandler<Change, SinkEvent> {
}

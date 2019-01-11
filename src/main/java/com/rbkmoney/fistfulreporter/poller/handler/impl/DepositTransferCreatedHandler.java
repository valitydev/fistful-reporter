package com.rbkmoney.fistfulreporter.poller.handler.impl;

import com.rbkmoney.fistful.cashflow.FinalCashFlowPosting;
import com.rbkmoney.fistful.deposit.Change;
import com.rbkmoney.fistful.deposit.SinkEvent;
import com.rbkmoney.fistfulreporter.dao.DepositDao;
import com.rbkmoney.fistfulreporter.dao.FistfulCashFlowDao;
import com.rbkmoney.fistfulreporter.domain.enums.DepositEventType;
import com.rbkmoney.fistfulreporter.domain.enums.DepositTransferStatus;
import com.rbkmoney.fistfulreporter.domain.enums.FistfulCashFlowChangeType;
import com.rbkmoney.fistfulreporter.domain.tables.pojos.Deposit;
import com.rbkmoney.fistfulreporter.domain.tables.pojos.FistfulCashFlow;
import com.rbkmoney.fistfulreporter.exception.DaoException;
import com.rbkmoney.fistfulreporter.exception.StorageException;
import com.rbkmoney.fistfulreporter.poller.handler.DepositEventHandler;
import com.rbkmoney.fistfulreporter.util.CashFlowUtil;
import com.rbkmoney.fistfulreporter.util.FistfulCashFlowSinkEvent;
import com.rbkmoney.geck.common.util.TypeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class DepositTransferCreatedHandler implements DepositEventHandler {

    private final DepositDao depositDao;

    private final FistfulCashFlowDao fistfulCashFlowDao;

    @Override
    public boolean accept(Change change) {
        return change.isSetTransfer() && change.getTransfer().isSetCreated();
    }

    @Override
    public void handle(Change change, SinkEvent event) {
        try {
            log.info("Start deposit transfer created handling, eventId={}, depositId={}, transferChange={}", event.getId(), event.getSource(), change.getTransfer());
            Deposit deposit = depositDao.get(event.getSource());

            deposit.setId(null);
            deposit.setWtime(null);

            deposit.setEventId(event.getId());
            deposit.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
            deposit.setDepositId(event.getSource());
            deposit.setSequenceId(event.getPayload().getSequence());
            deposit.setEventOccuredAt(TypeUtil.stringToLocalDateTime(event.getPayload().getOccuredAt()));
            deposit.setEventType(DepositEventType.DEPOSIT_TRANSFER_CREATED);
            deposit.setDepositTransferStatus(DepositTransferStatus.created);

            List<FinalCashFlowPosting> postings = change.getTransfer().getCreated().getCashflow().getPostings();
            deposit.setFee(CashFlowUtil.getFistfulFee(postings));
            deposit.setProviderFee(CashFlowUtil.getFistfulProviderFee(postings));

            depositDao.updateNotCurrent(event.getSource());
            long id = depositDao.save(deposit);

            List<FistfulCashFlow> fistfulCashFlows = CashFlowUtil.convertFistfulCashFlows(
                    new FistfulCashFlowSinkEvent(
                            event.getId(),
                            event.getCreatedAt(),
                            event.getSource(),
                            event.getPayload().getSequence(),
                            event.getPayload().getOccuredAt(),
                            DepositEventType.DEPOSIT_TRANSFER_CREATED.toString(),
                            id,
                            FistfulCashFlowChangeType.deposit,
                            postings
                    )
            );
            fistfulCashFlowDao.save(fistfulCashFlows);
            log.info("Deposit transfer have been saved, eventId={}, depositId={}, transferChange={}", event.getId(), event.getSource(), change.getTransfer());
        } catch (DaoException e) {
            throw new StorageException(e);
        }
    }
}

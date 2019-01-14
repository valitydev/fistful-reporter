package com.rbkmoney.fistful.reporter.util;

import com.rbkmoney.fistful.cashflow.FinalCashFlowPosting;
import com.rbkmoney.fistful.reporter.domain.enums.FistfulCashFlowChangeType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
@Getter
public class FistfulCashFlowSinkEvent {

    private final Long eventId;
    private final String eventCreatedAt;
    private final String sourceId;
    private final Integer sequenceId;
    private final String eventOccuredAt;
    private final String eventType;
    private final Long objId;
    private final FistfulCashFlowChangeType cashFlowChangeType;
    private final List<FinalCashFlowPosting> postings;

}

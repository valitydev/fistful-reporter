package com.rbkmoney.fistful.reporter.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * данный enum должны содержит типы отчетов, использующиеся в протоколе
 */
@Getter
@RequiredArgsConstructor
public enum ReportType {

    WITHDRAWAL_REGISTRY("withdrawalRegistry");

    private final String type;

}

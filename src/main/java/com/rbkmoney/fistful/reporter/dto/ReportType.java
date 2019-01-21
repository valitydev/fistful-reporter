package com.rbkmoney.fistful.reporter.dto;

/**
 * данный enum должны содержит типы отчетов, использующиеся в протоколе
 */
public enum ReportType {

    WITHDRAWAL_REGISTRY("withdrawalRegistry");

    private final String type;

    ReportType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}

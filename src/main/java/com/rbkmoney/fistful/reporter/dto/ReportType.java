package com.rbkmoney.fistful.reporter.dto;

public enum ReportType {

    withdrawal_registry("withdrawal_registry");

    private final String type;

    ReportType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}

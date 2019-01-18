package com.rbkmoney.fistful.reporter.service.impl;

import com.rbkmoney.fistful.reporter.domain.tables.pojos.Report;
import com.rbkmoney.fistful.reporter.dto.ReportType;
import com.rbkmoney.fistful.reporter.service.TemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.OutputStream;

@Component
@RequiredArgsConstructor
public class WithdrawalRegistryTemplateServiceImpl implements TemplateService {

    @Override
    public boolean accept(String reportType) {
        return reportType.equals(ReportType.withdrawal_registry.getType());
    }

    @Override
    public void processReportFileByTemplate(Report report, OutputStream outputStream) throws IOException {
        // todo
    }
}

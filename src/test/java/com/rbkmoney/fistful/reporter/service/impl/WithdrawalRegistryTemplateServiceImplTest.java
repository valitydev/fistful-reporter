package com.rbkmoney.fistful.reporter.service.impl;

import com.rbkmoney.fistful.reporter.AbstractWithdrawalTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.nio.file.Path;

import static java.nio.file.Files.*;

public class WithdrawalRegistryTemplateServiceImplTest extends AbstractWithdrawalTest {

    @Autowired
    private WithdrawalRegistryTemplateServiceImpl withdrawalRegistryTemplateService;

    @Test
    public void test() throws IOException {
        report.setTimezone("Europe/Moscow");
        Path reportFile = createTempFile(report.getType() + "_", "_report.xlsx");
        try {
            withdrawalRegistryTemplateService.processReportFileByTemplate(report, newOutputStream(reportFile));
        } finally {
            deleteIfExists(reportFile);
        }
    }

    @Override
    protected int getExpectedSize() {
        return 20;
    }
}

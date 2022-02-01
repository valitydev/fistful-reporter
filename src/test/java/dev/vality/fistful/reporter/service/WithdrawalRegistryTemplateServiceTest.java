package dev.vality.fistful.reporter.service;

import dev.vality.fistful.reporter.config.PostgresqlSpringBootITest;
import dev.vality.fistful.reporter.config.testconfiguration.WithdrawalTestDao;
import dev.vality.fistful.reporter.data.TestData;
import dev.vality.fistful.reporter.domain.tables.pojos.Report;
import dev.vality.fistful.reporter.service.impl.WithdrawalRegistryTemplateServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.nio.file.Path;

import static java.nio.file.Files.*;

@PostgresqlSpringBootITest
public class WithdrawalRegistryTemplateServiceTest {

    @Autowired
    private WithdrawalRegistryTemplateServiceImpl withdrawalRegistryTemplateService;

    @Autowired
    private WithdrawalTestDao withdrawalTestDao;

    @Test
    public void withdrawalRegistryTemplateServiceTest() throws IOException {
        withdrawalTestDao.saveWithdrawalsDependencies(4001);

        Report report = TestData.createReport();
        report.setTimezone("Europe/Moscow");
        Path reportFile = createTempFile(report.getType() + "_", "_report.xlsx");
        try {
            withdrawalRegistryTemplateService.processReportFileByTemplate(report, newOutputStream(reportFile));
        } finally {
            deleteIfExists(reportFile);
        }
    }
}

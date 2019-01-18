package com.rbkmoney.fistful.reporter.component;

import com.rbkmoney.fistful.reporter.domain.enums.ReportStatus;
import com.rbkmoney.fistful.reporter.domain.tables.pojos.Report;
import com.rbkmoney.fistful.reporter.exception.StorageException;
import com.rbkmoney.fistful.reporter.service.FileInfoService;
import com.rbkmoney.fistful.reporter.service.FileStorageService;
import com.rbkmoney.fistful.reporter.service.ReportService;
import com.rbkmoney.fistful.reporter.service.TemplateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.validation.ValidationException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class ReportGenerator {

    private final List<TemplateService> templateServices;
    private final ReportService reportService;
    private final FileInfoService fileInfoService;
    private final FileStorageService fileStorageService;

    public void generateReportFile(Report report) {
        try {
            log.info(
                    "Trying to process report, reportId='{}', " +
                            "reportType='{}', partyId='{}', contractId='{}', fromTime='{}', toTime='{}'",
                    report.getId(), report.getType(), report.getPartyId(), report.getContractId(), report.getFromTime(), report.getToTime()
            );

            List<String> fileDataIds = new ArrayList<>();
            for (TemplateService templateService : templateServices) {
                if (templateService.accept(report.getType())) {
                    Path reportFile = Files.createTempFile(report.getType() + "_", "_report.xlsx");
                    try {
                        templateService.processReportFileByTemplate(report, Files.newOutputStream(reportFile));
                        String fileDataId = fileStorageService.saveFile(reportFile);
                        fileDataIds.add(fileDataId);
                    } finally {
                        Files.deleteIfExists(reportFile);
                    }
                }
            }

            finishedReportTask(report, fileDataIds);

            log.info(
                    "Report has been successfully processed, " +
                            "reportId='{}', reportType='{}', partyId='{}', contractId='{}', fromTime='{}', toTime='{}'",
                    report.getId(), report.getType(), report.getPartyId(), report.getContractId(), report.getFromTime(), report.getToTime()
            );
        } catch (ValidationException ex) {
            log.error("Report data validation failed, reportId='{}'", report.getId(), ex);
            reportService.changeReportStatus(report, ReportStatus.cancelled);
        } catch (Throwable throwable) {
            log.error(
                    "The report has failed to process, " +
                            "reportId='{}', reportType='{}', partyId='{}', contractId='{}', fromTime='{}', toTime='{}'",
                    report.getId(), report.getType(), report.getPartyId(), report.getContractId(), report.getFromTime(), report.getToTime(),
                    throwable
            );
        }
    }

    private void finishedReportTask(Report report, List<String> fileDataIds) throws StorageException {
        for (String fileDataId : fileDataIds) {
            fileInfoService.save(report.getId(), fileDataId);
        }
        reportService.changeReportStatus(report, ReportStatus.created);
    }
}

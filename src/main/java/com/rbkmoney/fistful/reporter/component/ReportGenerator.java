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
import java.io.IOException;
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
            logInfo("Trying to process report, ", report);

            List<String> fileDataIds = new ArrayList<>();
            for (TemplateService templateService : templateServices) {
                if (templateService.accept(report.getType())) {
                    Path reportFile = Files.createTempFile(report.getType() + "_", "_report.xlsx");
                    try {
                        templateService.processReportFileByTemplate(report, Files.newOutputStream(reportFile));
                        String fileDataId = fileStorageService.saveFile(reportFile);
                        fileDataIds.add(fileDataId);
                    } catch (IOException ex) {
                        logError("The report has failed to save, ", report);
                    } finally {
                        Files.deleteIfExists(reportFile);
                    }
                }
            }

            finishedReportTask(report, fileDataIds);

            logInfo("Report has been successfully processed, ", report);
        } catch (ValidationException ex) {
            logError("Report data validation failed, ", report);
            reportService.changeReportStatus(report, ReportStatus.cancelled);
        } catch (Exception ex) {
            logError("The report has failed to process, ", report);
        }
    }

    private void finishedReportTask(Report report, List<String> fileDataIds) throws StorageException {
        fileInfoService.save(report.getId(), fileDataIds);
        reportService.changeReportStatus(report, ReportStatus.created);
    }

    private void logInfo(String message, Report report) {
        log.info(
                message +
                        "reportId='{}', " +
                        "partyId='{}', " +
                        "contractId='{}', " +
                        "fromTime='{}', " +
                        "toTime='{}', " +
                        "createdAt='{}', " +
                        "reportType='{}', " +
                        "status='{}'"
                ,
                report.getId(),
                report.getPartyId(),
                report.getContractId(),
                report.getFromTime(),
                report.getToTime(),
                report.getCreatedAt(),
                report.getType(),
                report.getStatus()
        );
    }

    private void logError(String message, Report report) {
        log.error(
                message +
                        "reportId='{}', " +
                        "partyId='{}', " +
                        "contractId='{}', " +
                        "fromTime='{}', " +
                        "toTime='{}', " +
                        "createdAt='{}', " +
                        "reportType='{}', " +
                        "status='{}'"
                ,
                report.getId(),
                report.getPartyId(),
                report.getContractId(),
                report.getFromTime(),
                report.getToTime(),
                report.getCreatedAt(),
                report.getType(),
                report.getStatus()
        );
    }
}

package dev.vality.fistful.reporter.generator;

import dev.vality.fistful.reporter.domain.enums.ReportStatus;
import dev.vality.fistful.reporter.domain.tables.pojos.Report;
import dev.vality.fistful.reporter.exception.ReportGeneratorException;
import dev.vality.fistful.reporter.service.FileInfoService;
import dev.vality.fistful.reporter.service.FileStorageService;
import dev.vality.fistful.reporter.service.ReportService;
import dev.vality.fistful.reporter.service.TemplateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequiredArgsConstructor
public class ReportGenerator {

    private final List<TemplateService> templateServices;
    private final ReportService reportService;
    private final FileInfoService fileInfoService;
    private final FileStorageService fileStorageService;

    public void generateReportFile(Report report) {
        logInfo("Start generate report", report);

        List<String> fileDataIds = new ArrayList<>();

        List<TemplateService> templates = templateServices.stream()
                .filter(templateService -> templateService.accept(report.getType()))
                .toList();

        for (TemplateService templateService : templates) {
            try {
                Path reportFile = Files.createTempFile(getReportName(templateService), ".xlsx");

                try {
                    logInfo("Fill report file, templateType=" + templateService.getTemplateType(), report);

                    templateService.processReportFileByTemplate(report, Files.newOutputStream(reportFile));

                    logInfo("Upload report file, templateType=" + templateService.getTemplateType(), report);

                    String fileDataId = fileStorageService.saveFile(reportFile);

                    fileDataIds.add(fileDataId);
                } finally {
                    Files.deleteIfExists(reportFile);
                }
            } catch (IOException ex) {
                throw new ReportGeneratorException("File can not be written", ex);
            }
        }

        finishedReportTask(report, fileDataIds);

        logInfo("Finish generate report", report);
    }

    private void finishedReportTask(Report report, List<String> fileDataIds) {
        String ids = fileDataIds.stream().collect(Collectors.joining(", ", "[", "]"));

        logInfo("Save report fileInfo, fileDataIds=" + ids, report);
        fileInfoService.save(report.getId(), fileDataIds);

        logInfo("Change report status on [created]", report);
        reportService.changeReportStatus(report, ReportStatus.created);
        report.setStatus(ReportStatus.created);
    }

    private String getReportName(TemplateService templateService) {
        return templateService.getReportPrefixName() + "_report_for_a_period_of_time_" + LocalDateTime.now().toString();
    }

    private void logInfo(String message, Report report) {
        String format = getFormatMessage(message, report);
        log.info(format);
    }

    private String getFormatMessage(String message, Report report) {
        return String.format(
                message +
                        ", reportId=%s, " +
                        "partyId=%s, " +
                        "fromTime=%s, " +
                        "toTime=%s, " +
                        "createdAt=%s, " +
                        "reportType=%s, " +
                        "status=%s",
                report.getId(),
                report.getPartyId(),
                report.getFromTime().toString(),
                report.getToTime().toString(),
                report.getCreatedAt().toString(),
                report.getType(),
                report.getStatus()
        );
    }
}

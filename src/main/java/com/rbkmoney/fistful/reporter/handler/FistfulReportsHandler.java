package com.rbkmoney.fistful.reporter.handler;

import com.rbkmoney.fistful.reporter.*;
import com.rbkmoney.fistful.reporter.config.properties.ReportingProperties;
import com.rbkmoney.fistful.reporter.dto.ReportType;
import com.rbkmoney.fistful.reporter.exception.ContractNotFoundException;
import com.rbkmoney.fistful.reporter.exception.PartyNotFoundException;
import com.rbkmoney.fistful.reporter.exception.ReportNotFoundException;
import com.rbkmoney.fistful.reporter.service.FileInfoService;
import com.rbkmoney.fistful.reporter.service.PartyManagementService;
import com.rbkmoney.fistful.reporter.service.ReportService;
import com.rbkmoney.fistful.reporter.util.ThriftUtils;
import com.rbkmoney.geck.common.util.TypeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.rbkmoney.fistful.reporter.util.ThriftUtils.buildInvalidRequest;

@Component
@Slf4j
@RequiredArgsConstructor
public class FistfulReportsHandler implements ReportingSrv.Iface {

    private final ReportingProperties reportingProperties;
    private final ReportService reportService;
    private final FileInfoService fileService;
    private final PartyManagementService partyManagementService;

    @Override
    public List<Report> getReports(ReportRequest reportRequest, List<String> reportTypes) throws DatasetTooBig, InvalidRequest {
        checkArgs(reportRequest, reportTypes);

        Instant toTime = TypeUtil.stringToInstant(reportRequest.getTimeRange().getToTime());
        Instant fromTime = TypeUtil.stringToInstant(reportRequest.getTimeRange().getFromTime());

        List<com.rbkmoney.fistful.reporter.domain.tables.pojos.Report> reportsByRange = reportService.getReportsByRangeNotCancelled(
                reportRequest.getPartyId(),
                reportRequest.getContractId(),
                fromTime,
                toTime,
                reportTypes
        );

        if (reportingProperties.getReportsLimit() > 0 && reportsByRange.size() > reportingProperties.getReportsLimit()) {
            throw new DatasetTooBig(reportingProperties.getReportsLimit());
        }

        return reportsByRange.stream()
                .map(report -> ThriftUtils.map(report, fileService.getFileDataIds(report.getId())))
                .collect(Collectors.toList());
    }

    @Override
    public long generateReport(ReportRequest reportRequest, String reportType) throws PartyNotFound, ContractNotFound, InvalidRequest {
        checkArgs(reportRequest, Collections.singletonList(reportType));

        Instant toTime = TypeUtil.stringToInstant(reportRequest.getTimeRange().getToTime());
        Instant fromTime = TypeUtil.stringToInstant(reportRequest.getTimeRange().getFromTime());

        try {
            // проверка на существование в хелгейте
            partyManagementService.getContract(reportRequest.getPartyId(), reportRequest.getContractId());

            return reportService.createReport(
                    reportRequest.getPartyId(),
                    reportRequest.getContractId(),
                    fromTime,
                    toTime,
                    reportType
            );
        } catch (PartyNotFoundException ex) {
            throw new PartyNotFound();
        } catch (ContractNotFoundException ex) {
            throw new ContractNotFound();
        }
    }

    @Override
    public Report getReport(String partyId, String contractId, long reportId) throws ReportNotFound {
        try {
            return ThriftUtils.map(
                    reportService.getReport(partyId, contractId, reportId),
                    fileService.getFileDataIds(reportId)
            );
        } catch (ReportNotFoundException ex) {
            throw new ReportNotFound();
        }
    }

    @Override
    public void cancelReport(String partyId, String contractId, long reportId) throws ReportNotFound {
        try {
            reportService.cancelReport(partyId, contractId, reportId);
        } catch (ReportNotFoundException ex) {
            throw new ReportNotFound();
        }
    }

    private void checkReportType(String reportType) throws IllegalArgumentException {
        if (Arrays.stream(ReportType.values())
                .noneMatch(r -> r.getType().equals(reportType))) {
            throw new IllegalArgumentException("reportType does not exist");
        }
    }

    private void checkArgs(ReportRequest reportRequest, List<String> reportTypes) throws InvalidRequest {
        try {
            Instant fromTime = TypeUtil.stringToInstant(reportRequest.getTimeRange().getFromTime());
            Instant toTime = TypeUtil.stringToInstant(reportRequest.getTimeRange().getToTime());

            if (fromTime.isAfter(toTime)) {
                throw new IllegalArgumentException("fromTime must be less that toTime");
            }

            for (String reportType : reportTypes) {
                checkReportType(reportType);
            }
        } catch (IllegalArgumentException ex) {
            throw buildInvalidRequest(ex);
        }
    }
}

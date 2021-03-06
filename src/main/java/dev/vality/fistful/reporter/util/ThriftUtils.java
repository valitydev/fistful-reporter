package dev.vality.fistful.reporter.util;

import dev.vality.fistful.reporter.Report;
import dev.vality.fistful.reporter.ReportStatus;
import dev.vality.fistful.reporter.ReportTimeRange;
import dev.vality.geck.common.util.TypeUtil;

import java.util.List;

public class ThriftUtils {

    public static Report map(
            dev.vality.fistful.reporter.domain.tables.pojos.Report report,
            List<String> fileDataIds) {
        Report thriftReport = new Report();
        thriftReport.setReportId(report.getId());
        thriftReport.setTimeRange(createTimeRange(report));
        thriftReport.setCreatedAt(TypeUtil.temporalToString(report.getCreatedAt()));
        thriftReport.setReportType(report.getType());
        thriftReport.setStatus(ReportStatus.valueOf(report.getStatus().getLiteral()));
        thriftReport.setFileDataIds(fileDataIds);
        return thriftReport;
    }

    private static ReportTimeRange createTimeRange(dev.vality.fistful.reporter.domain.tables.pojos.Report report) {
        return new ReportTimeRange(
                TypeUtil.temporalToString(report.getFromTime()),
                TypeUtil.temporalToString(report.getToTime())
        );
    }
}

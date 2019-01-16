package com.rbkmoney.fistful.reporter.service;

import com.rbkmoney.fistful.reporter.domain.tables.pojos.Report;
import com.rbkmoney.fistful.reporter.exception.ReportNotFoundException;
import com.rbkmoney.fistful.reporter.exception.StorageException;

import java.time.Instant;
import java.util.List;

public interface ReportService {

    long createReport(String partyId, String contractId, Instant fromTime, Instant toTime, String reportType) throws StorageException;

    List<Report> getReportsByRange(String partyId, String contractId, Instant fromTime, Instant toTime, List<String> reportTypes) throws StorageException;

    List<Report> getReportsByRangeNotCancelled(String partyId, String contractId, Instant fromTime, Instant toTime, List<String> reportTypes) throws StorageException;

    Report getReport(String partyId, String contractId, long reportId) throws ReportNotFoundException, StorageException;

    void cancelReport(String partyId, String shopId, long reportId) throws ReportNotFoundException, StorageException;
}

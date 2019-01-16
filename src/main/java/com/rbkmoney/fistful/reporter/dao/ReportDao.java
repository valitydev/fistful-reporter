package com.rbkmoney.fistful.reporter.dao;

import com.rbkmoney.fistful.reporter.domain.enums.ReportStatus;
import com.rbkmoney.fistful.reporter.domain.tables.pojos.Report;
import com.rbkmoney.fistful.reporter.exception.DaoException;

import java.time.LocalDateTime;
import java.util.List;

public interface ReportDao extends GenericDao {

    List<Report> getReportsByRange(String partyId, String contractId, LocalDateTime fromTime, LocalDateTime toTime, List<String> reportTypes) throws DaoException;

    Report getReport(String partyId, String contractId, long reportId) throws DaoException;

    long createReport(String partyId, String contractId, LocalDateTime fromTime, LocalDateTime toTime, String reportType, String timezone, LocalDateTime createdAt) throws DaoException;

    void changeReportStatus(Long reportId, ReportStatus reportStatus) throws DaoException;
}

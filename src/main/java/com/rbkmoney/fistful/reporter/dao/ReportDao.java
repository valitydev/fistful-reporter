package com.rbkmoney.fistful.reporter.dao;

import com.rbkmoney.fistful.reporter.domain.enums.ReportStatus;
import com.rbkmoney.fistful.reporter.domain.tables.pojos.Report;
import com.rbkmoney.fistful.reporter.exception.DaoException;

import java.time.LocalDateTime;
import java.util.List;

public interface ReportDao extends GenericDao {

    long save(Report report) throws DaoException;

    List<Report> getReportsByRange(String partyId, String contractId, LocalDateTime fromTime, LocalDateTime toTime, List<String> reportTypes) throws DaoException;

    Report getReport(long reportId, String partyId, String contractId) throws DaoException;

    void changeReportStatus(Long reportId, ReportStatus reportStatus) throws DaoException;

    List<Report> getPendingReports() throws DaoException;
}

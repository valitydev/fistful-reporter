package com.rbkmoney.fistful.reporter.dao;

import com.rbkmoney.fistful.reporter.domain.tables.pojos.FileInfo;
import com.rbkmoney.fistful.reporter.exception.DaoException;

import java.util.List;

public interface FileInfoDao extends GenericDao {

    Long save(FileInfo fileInfo) throws DaoException;

    List<FileInfo> getByReportId(Long reportId) throws DaoException;
}

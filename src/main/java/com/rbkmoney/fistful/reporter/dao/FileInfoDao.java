package com.rbkmoney.fistful.reporter.dao;

import dev.vality.dao.GenericDao;
import com.rbkmoney.fistful.reporter.domain.tables.pojos.FileInfo;

import java.util.List;

public interface FileInfoDao extends GenericDao {

    Long save(FileInfo fileInfo);

    List<FileInfo> getByReportId(Long reportId);
}

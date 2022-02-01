package dev.vality.fistful.reporter.dao;

import dev.vality.dao.GenericDao;
import dev.vality.fistful.reporter.domain.tables.pojos.FileInfo;

import java.util.List;

public interface FileInfoDao extends GenericDao {

    Long save(FileInfo fileInfo);

    List<FileInfo> getByReportId(Long reportId);
}

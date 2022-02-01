package dev.vality.fistful.reporter.dao.impl;

import com.zaxxer.hikari.HikariDataSource;
import dev.vality.dao.impl.AbstractGenericDao;
import dev.vality.fistful.reporter.dao.FileInfoDao;
import dev.vality.fistful.reporter.dao.mapper.RecordRowMapper;
import dev.vality.fistful.reporter.domain.tables.pojos.FileInfo;
import dev.vality.fistful.reporter.domain.tables.records.FileInfoRecord;
import org.jooq.Condition;
import org.jooq.Query;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;

import java.util.List;

import static dev.vality.fistful.reporter.domain.tables.FileInfo.FILE_INFO;

@Component
public class FileInfoDaoImpl extends AbstractGenericDao implements FileInfoDao {

    private final RecordRowMapper<FileInfo> fileInfoRecordRowMapper;

    public FileInfoDaoImpl(HikariDataSource dataSource) {
        super(dataSource);
        fileInfoRecordRowMapper = new RecordRowMapper<>(FILE_INFO, FileInfo.class);
    }

    @Override
    public Long save(FileInfo fileInfo) {
        FileInfoRecord record = getDslContext().newRecord(FILE_INFO, fileInfo);
        Query query = getDslContext().insertInto(FILE_INFO).set(record).returning(FILE_INFO.ID);

        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        executeOne(query, keyHolder);
        return keyHolder.getKey().longValue();
    }

    @Override
    public List<FileInfo> getByReportId(Long reportId) {
        Condition condition = FILE_INFO.REPORT_ID.eq(reportId);
        Query query = getDslContext().selectFrom(FILE_INFO).where(condition);

        return fetch(query, fileInfoRecordRowMapper);
    }
}

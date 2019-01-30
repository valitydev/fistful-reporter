package com.rbkmoney.fistful.reporter.dao.impl;

import com.rbkmoney.fistful.reporter.dao.FileInfoDao;
import com.rbkmoney.fistful.reporter.dao.mapper.RecordRowMapper;
import com.rbkmoney.fistful.reporter.domain.tables.pojos.FileInfo;
import com.rbkmoney.fistful.reporter.domain.tables.records.FileInfoRecord;
import com.rbkmoney.fistful.reporter.exception.DaoException;
import org.jooq.Condition;
import org.jooq.Query;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.List;

import static com.rbkmoney.fistful.reporter.domain.tables.FileInfo.FILE_INFO;

@Component
public class FileInfoDaoImpl extends AbstractGenericDao implements FileInfoDao {

    private final RecordRowMapper<FileInfo> fileInfoRecordRowMapper;

    public FileInfoDaoImpl(DataSource dataSource) {
        super(dataSource);
        fileInfoRecordRowMapper = new RecordRowMapper<>(FILE_INFO, FileInfo.class);
    }

    @Override
    public Long save(FileInfo fileInfo) throws DaoException {
        FileInfoRecord record = getDslContext().newRecord(FILE_INFO, fileInfo);
        Query query = getDslContext().insertInto(FILE_INFO).set(record).returning(FILE_INFO.ID);

        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        executeOne(query, keyHolder);
        return keyHolder.getKey().longValue();
    }

    @Override
    public List<FileInfo> getByReportId(Long reportId) throws DaoException {
        Condition condition = FILE_INFO.REPORT_ID.eq(reportId);
        Query query = getDslContext().selectFrom(FILE_INFO).where(condition);

        return fetch(query, fileInfoRecordRowMapper);
    }
}

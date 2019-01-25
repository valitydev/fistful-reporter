package com.rbkmoney.fistful.reporter.dao;

import com.rbkmoney.fistful.reporter.AbstractIntegrationTest;
import com.rbkmoney.fistful.reporter.domain.tables.pojos.FileInfo;
import com.rbkmoney.fistful.reporter.domain.tables.pojos.Report;
import com.rbkmoney.fistful.reporter.exception.DaoException;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static io.github.benas.randombeans.api.EnhancedRandom.randomListOf;
import static org.junit.Assert.assertEquals;

public class FileInfoDaoImplTest extends AbstractIntegrationTest {

    @Autowired
    private FileInfoDao fileInfoDao;

    @Autowired
    private ReportDao reportDao;

    private final int size = 4;

    private long reportId;

    @Before
    public void setUp() throws Exception {
        Report report = random(Report.class);
        reportId = reportDao.save(report);
    }

    @Test
    public void test() throws DaoException {
        List<FileInfo> fileInfos = randomListOf(size, FileInfo.class);
        for (FileInfo fileInfo : fileInfos) {
            fileInfo.setReportId(reportId);
            fileInfoDao.save(fileInfo);

        }
        assertEquals(size, fileInfoDao.getByReportId(reportId).size());
    }
}

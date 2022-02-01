package dev.vality.fistful.reporter.dao;

import dev.vality.fistful.reporter.config.PostgresqlSpringBootITest;
import dev.vality.fistful.reporter.domain.tables.pojos.FileInfo;
import dev.vality.fistful.reporter.domain.tables.pojos.Report;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static dev.vality.testcontainers.annotations.util.RandomBeans.random;
import static dev.vality.testcontainers.annotations.util.RandomBeans.randomListOf;
import static org.junit.jupiter.api.Assertions.assertEquals;

@PostgresqlSpringBootITest
public class FileInfoDaoTest {

    @Autowired
    private FileInfoDao fileInfoDao;

    @Autowired
    private ReportDao reportDao;

    @Test
    public void fileInfoDaoTest() {
        final int size = 4;
        long reportId;
        Report report = random(Report.class);
        reportId = reportDao.save(report);
        List<FileInfo> fileInfos = randomListOf(size, FileInfo.class);
        for (FileInfo fileInfo : fileInfos) {
            fileInfo.setReportId(reportId);
            fileInfoDao.save(fileInfo);

        }
        assertEquals(size, fileInfoDao.getByReportId(reportId).size());
    }
}

package dev.vality.fistful.reporter.service;

import dev.vality.fistful.reporter.domain.tables.pojos.Report;
import org.jxls.common.Context;
import org.jxls.util.JxlsHelper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

@Service
public interface TemplateService {

    String getTemplateType();

    String getReportPrefixName();

    boolean accept(String reportType);

    void processReportFileByTemplate(Report report, OutputStream outputStream) throws IOException;

    default void processTemplate(
            Context context,
            InputStream templateStream,
            OutputStream outputStream) throws IOException {
        JxlsHelper.getInstance().processTemplate(templateStream, outputStream, context);
    }
}
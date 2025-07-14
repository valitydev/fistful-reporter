package dev.vality.fistful.reporter.service.impl;

import dev.vality.file.storage.FileStorageSrv;
import dev.vality.file.storage.NewFileResult;
import dev.vality.fistful.reporter.config.properties.FileStorageProperties;
import dev.vality.fistful.reporter.exception.FileStorageClientException;
import dev.vality.fistful.reporter.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.ClientProtocolException;
import org.apache.hc.client5.http.classic.methods.HttpPut;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.FileEntity;
import org.apache.thrift.TException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collections;

@Service
@Slf4j
@RequiredArgsConstructor
public class FileStorageServiceImpl implements FileStorageService {

    private final FileStorageProperties fileStorageProperties;
    private final FileStorageSrv.Iface fileStorageClient;
    private final CloseableHttpClient httpClient;

    @Override
    public String saveFile(Path file) {
        var fileName = file.getFileName().toString();
        try {
            log.info("Trying to upload report file");
            var fileResult = createNewFile(fileName);
            var requestPut = new HttpPut(fileResult.getUploadUrl());
            var encode = URLEncoder.encode(fileName, StandardCharsets.UTF_8);
            requestPut.setHeader("Content-Disposition", "attachment;filename=" + encode);
            requestPut.setEntity(new FileEntity(file.toFile(), ContentType.APPLICATION_OCTET_STREAM));
            HttpClientResponseHandler<String> handler = response -> EntityUtils.toString(response.getEntity());
            httpClient.execute(requestPut, handler);
            log.info("Report file has been successfully uploaded, fileDataId={}", fileResult.getFileDataId());
            return fileResult.getFileDataId();
        } catch (UnsupportedEncodingException ex) {
            throw new FileStorageClientException(String.format("Error with encoding fileName=%s", fileName), ex);
        } catch (ClientProtocolException ex) {
            throw new FileStorageClientException("Http protocol error", ex);
        } catch (IOException ex) {
            throw new FileStorageClientException("Connection was aborted", ex);
        }
    }

    private NewFileResult createNewFile(String fileName) {
        try {
            return fileStorageClient.createNewFile(Collections.emptyMap(), getTime().toString());
        } catch (TException e) {
            throw new FileStorageClientException(
                    String.format(
                            "Failed to create new, file name='%s'",
                            fileName
                    ),
                    e
            );
        }
    }

    private Instant getTime() {
        return LocalDateTime.now(fileStorageProperties.getTimeZone())
                .plusMinutes(fileStorageProperties.getUrlLifeTimeDuration())
                .toInstant(ZoneOffset.UTC);
    }
}

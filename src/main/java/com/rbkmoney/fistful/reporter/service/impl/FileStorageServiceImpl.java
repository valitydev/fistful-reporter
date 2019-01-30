package com.rbkmoney.fistful.reporter.service.impl;

import com.rbkmoney.file.storage.FileStorageSrv;
import com.rbkmoney.file.storage.NewFileResult;
import com.rbkmoney.fistful.reporter.config.properties.FileStorageProperties;
import com.rbkmoney.fistful.reporter.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.FileEntity;
import org.apache.thrift.TException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
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
    private final HttpClient httpClient;

    @Override
    public String saveFile(Path file) throws IOException {
        // create new file

        checkStorageConnection();

        log.info("Trying to create new empty file in storage");
        String fileName = file.getFileName().toString();
        NewFileResult result = createFileInFileStorage(fileName);

        // upload by ceph url
        String uploadUrl = result.getUploadUrl();

        uploadUrl = checkTestSignature(uploadUrl);

        log.info("Trying to upload report file to storage");
        HttpPut requestPut = new HttpPut(uploadUrl);
        requestPut.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, StandardCharsets.UTF_8.name()));
        requestPut.setEntity(new FileEntity(file.toFile()));

        HttpResponse response = httpClient.execute(requestPut);

        checkResponse(result.getFileDataId(), response);

        log.info("Report file has been successfuly uploaded, fileDataId={}", result.getFileDataId());
        return result.getFileDataId();
    }

    private NewFileResult createFileInFileStorage(String fileName) {
        NewFileResult result;
        try {
            result = fileStorageClient.createNewFile(Collections.emptyMap(), getTime().toString());
        } catch (TException e) {
            throw new RuntimeException(
                    String.format(
                            "Failed to create new file, file name='%s'",
                            fileName
                    ),
                    e
            );
        }
        return result;
    }

    private Instant getTime() {
        return LocalDateTime.now(fileStorageProperties.getTimeZone())
                .plusMinutes(fileStorageProperties.getUrlLifeTimeDuration())
                .toInstant(ZoneOffset.UTC);
    }

    private void checkStorageConnection() {
        log.info("Trying to connect to storage");
        try {
            URI uri = fileStorageProperties.getHealthCheckUrl().getURI();
            HttpResponse httpResponse = httpClient.execute(new HttpGet(uri));
            if (httpResponse.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                throw new RuntimeException("Failed to connect to storage");
            }
        } catch (Exception ex) {
            throw new RuntimeException("Failed to connect to storage");
        }
    }

    private void checkResponse(String fileDataId, HttpResponse response) {
        if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
            throw new RuntimeException(
                    String.format(
                            "Failed to upload report file, fileDataId='%s'",
                            fileDataId
                    )
            );
        }
    }

    // костыль для FileStorageServiceImplTest >.<
    // тк при генерации ссылки для загрузки в url вставляется то, что указано в {storage.endpoint: "ceph-test:80"}
    // {network_mode: host} не работает на Docker For Mac
    private String checkTestSignature(String uploadUrl) throws IOException {
        if (uploadUrl.contains("ceph-test-container:80")) {
            uploadUrl = uploadUrl.replaceAll("ceph-test-container:80", fileStorageProperties.getCephEndpoint());
        }
        return uploadUrl;
    }
}

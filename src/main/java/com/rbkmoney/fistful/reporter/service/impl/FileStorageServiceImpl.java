package com.rbkmoney.fistful.reporter.service.impl;

import com.rbkmoney.file.storage.FileStorageSrv;
import com.rbkmoney.file.storage.NewFileResult;
import com.rbkmoney.fistful.reporter.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.FileEntity;
import org.apache.thrift.TException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Collections;

@Service
@Slf4j
@RequiredArgsConstructor
public class FileStorageServiceImpl implements FileStorageService {

    private final FileStorageSrv.Iface fileStorageClient;
    private final HttpClient httpClient;

    @Value("${fileStorage.timeZone:Europe/Moscow}")
    private ZoneId timeZone;

    @Value("${fileStorage.urlLifeTimeDuration:360}")
    private Long urlLifeTime;

    @Override
    public String saveFile(Path file) throws IOException {
        // create new file
        String fileName = file.getFileName().toString();
        NewFileResult result = createFileInFileStorage(fileName);

        // upload by ceph url
        HttpPut requestPut = new HttpPut(result.getUploadUrl());
        requestPut.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, StandardCharsets.UTF_8.name()));
        requestPut.setEntity(new FileEntity(file.toFile()));

        HttpResponse response = httpClient.execute(requestPut);

        if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
            throw new RuntimeException(
                    String.format(
                            "Failed to upload report file, file name='%s'",
                            fileName
                    )
            );
        }

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
        Instant instant = Instant.now();
        // меняем на UTC , сдвигаем на тайм зону file-storage сервиса
        return LocalDateTime.ofInstant(instant, ZoneOffset.UTC)
                .plusMinutes(urlLifeTime)
                .toInstant(timeZone.getRules().getOffset(instant));
    }
}

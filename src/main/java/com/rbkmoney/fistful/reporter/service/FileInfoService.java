package com.rbkmoney.fistful.reporter.service;

import com.rbkmoney.fistful.reporter.exception.StorageException;

import java.util.List;

public interface FileInfoService {

    List<Long> save(long reportId, List<String> fileDataIds) throws StorageException;

    Long save(long reportId, String fileDataId) throws StorageException;

    List<String> getFileDataIds(long reportId) throws StorageException;
}

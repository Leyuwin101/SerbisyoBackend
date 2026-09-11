package com.example.serbisyofullstack.storage;

import java.io.InputStream;
import java.util.Set;

/**
 * Storage boundary for all file persistence (provider documents, service
 * images, avatars, message attachments). Business services depend on this
 * interface only — never on S3/MinIO/local FS clients — so the backing
 * implementation can change without touching business logic.
 */
public interface FileStorageService {

    /**
     * Validate and store a stream under a safe generated key.
     *
     * @throws com.example.serbisyofullstack.exception.StorageException on
     * size/MIME violations or IO failures
     */
    StoredFile upload(InputStream inputStream, String filename, String contentType, long contentLength);

    InputStream download(String storageKey);

    void delete(String storageKey);

    boolean exists(String storageKey);

    Set<String> allowedMimeTypes();

    long maxFileSizeBytes();
}

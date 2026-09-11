package com.example.serbisyofullstack.storage;

/**
 * Metadata about a stored object. The database stores only {@code storageKey};
 * the content lives in the object store.
 */
public record StoredFile(
        String storageKey,
        String originalFilename,
        String contentType,
        long sizeBytes
        ) {

}

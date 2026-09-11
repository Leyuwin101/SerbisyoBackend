package com.example.serbisyofullstack.storage;

import com.example.serbisyofullstack.exception.StorageException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Set;

/**
 * Local-filesystem implementation of {@link FileStorageService} for development
 * and small deployments. Enforces MIME allow-list, max size and safe keys; swap
 * with an S3/MinIO implementation for production scale.
 */
@Service
@RequiredArgsConstructor
public class LocalFileStorageService implements FileStorageService {

    private static final Set<String> ALLOWED_MIME_TYPES = Set.of(
            "image/jpeg", "image/png", "image/webp", "image/gif", "application/pdf");
    private static final long MAX_FILE_SIZE_BYTES = 5 * 1024 * 1024; // 5 MB

    private final StoragePathResolver pathResolver;

    @Value("${app.storage.local.root-dir:./uploads}")
    private String rootDir;

    @Override
    public StoredFile upload(InputStream inputStream, String filename, String contentType, long contentLength) {
        if (inputStream == null) {
            throw new StorageException("No file content supplied");
        }
        if (contentType == null || !ALLOWED_MIME_TYPES.contains(contentType.toLowerCase())) {
            throw new StorageException("File type not allowed: " + contentType);
        }
        if (contentLength > MAX_FILE_SIZE_BYTES) {
            throw new StorageException("File exceeds the maximum allowed size of "
                    + MAX_FILE_SIZE_BYTES + " bytes");
        }
        String namespace = "misc";
        long ownerId = 0;
        // Namespace/owner come from the caller via the key; services build keys
        // through the resolver. Here we generate the key deterministically.
        String key = pathResolver.keyFor(namespace, ownerId, null, filename);
        if (!pathResolver.isSafe(key)) {
            throw new StorageException("Generated storage key is not safe");
        }
        try {
            Path target = Path.of(rootDir).toAbsolutePath().normalize().resolve(key).normalize();
            if (!target.startsWith(Path.of(rootDir).toAbsolutePath().normalize())) {
                throw new StorageException("Resolved path escapes the storage root");
            }
            Files.createDirectories(target.getParent());
            try (InputStream in = inputStream) {
                Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
            }
            long size = Files.size(target);
            return new StoredFile(key, filename, contentType, size);
        } catch (IOException ex) {
            throw new StorageException("Failed to store file: " + ex.getMessage());
        }
    }

    @Override
    public InputStream download(String storageKey) {
        try {
            Path target = resolveExisting(storageKey);
            return Files.newInputStream(target);
        } catch (IOException ex) {
            throw new StorageException("Failed to read stored file: " + ex.getMessage());
        }
    }

    @Override
    public void delete(String storageKey) {
        try {
            Files.deleteIfExists(resolveExisting(storageKey));
        } catch (IOException ex) {
            throw new StorageException("Failed to delete stored file: " + ex.getMessage());
        }
    }

    @Override
    public boolean exists(String storageKey) {
        if (!pathResolver.isSafe(storageKey)) {
            return false;
        }
        return Files.exists(Path.of(rootDir).toAbsolutePath().normalize().resolve(storageKey).normalize());
    }

    @Override
    public Set<String> allowedMimeTypes() {
        return ALLOWED_MIME_TYPES;
    }

    @Override
    public long maxFileSizeBytes() {
        return MAX_FILE_SIZE_BYTES;
    }

    private Path resolveExisting(String storageKey) {
        if (!pathResolver.isSafe(storageKey)) {
            throw new StorageException("Unsafe storage key");
        }
        Path target = Path.of(rootDir).toAbsolutePath().normalize().resolve(storageKey).normalize();
        if (!target.startsWith(Path.of(rootDir).toAbsolutePath().normalize())) {
            throw new StorageException("Resolved path escapes the storage root");
        }
        if (!Files.exists(target)) {
            throw new StorageException("File not found: " + storageKey);
        }
        return target;
    }
}

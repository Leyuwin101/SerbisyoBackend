package com.example.serbisyofullstack.storage;

import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.UUID;

/**
 * Default key builder: {@code <namespace>/<ownerId>[/<parentId>]/<uuid><ext>}.
 * Keys are validated against traversal and absolute paths; only known-safe
 * extensions pass.
 */
@Component
public class DefaultStoragePathResolver implements StoragePathResolver {

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(
            "jpg", "jpeg", "png", "webp", "gif", "pdf");

    @Override
    public String keyFor(String namespace, Long ownerId, Long parentId, String originalFilename) {
        String ext = extensionOf(originalFilename);
        StringBuilder key = new StringBuilder(namespace).append('/').append(ownerId);
        if (parentId != null) {
            key.append('/').append(parentId);
        }
        return key.append('/').append(UUID.randomUUID()).append(ext).toString();
    }

    @Override
    public boolean isSafe(String storageKey) {
        if (storageKey == null || storageKey.isBlank()) {
            return false;
        }
        return !storageKey.contains("..")
                && !storageKey.startsWith("/")
                && !storageKey.contains("\\")
                && !storageKey.contains("\0");
    }

    @Override
    public Set<String> allowedExtensions() {
        return ALLOWED_EXTENSIONS;
    }

    private String extensionOf(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        String ext = filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();
        return ALLOWED_EXTENSIONS.contains(ext) ? "." + ext : "";
    }
}

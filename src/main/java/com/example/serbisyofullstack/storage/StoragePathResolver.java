package com.example.serbisyofullstack.storage;

import java.util.Set;

/**
 * Builds safe, logically-namespaced storage keys. Original filenames are never
 * used as keys (they can contain traversal or unsafe characters); keys are
 * {@code <namespace>/<ownerId>/<uuid>.<ext>} shaped.
 */
public interface StoragePathResolver {

    String keyFor(String namespace, Long ownerId, Long parentId, String originalFilename);

    /**
     * Reject keys that escape the storage root or contain unsafe segments.
     */
    boolean isSafe(String storageKey);

    Set<String> allowedExtensions();
}

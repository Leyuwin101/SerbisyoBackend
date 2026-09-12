package com.example.serbisyofullstack.api;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

/**
 * Caps client-supplied pagination so no endpoint can be made to return an
 * unbounded number of rows. Controllers wrap incoming Pageable values with
 * this guard (readme Phase 9: size <= 100).
 */
public final class PaginationGuard {

    private PaginationGuard() {}

    public static final int MAX_PAGE_SIZE = 100;

    public static Pageable cap(Pageable pageable) {
        if (pageable == null) {
            return PageRequest.of(0, 20);
        }
        int page = Math.max(0, pageable.getPageNumber());
        int size = pageable.getPageSize() <= 0 ? 20 : Math.min(pageable.getPageSize(), MAX_PAGE_SIZE);
        Sort sort = pageable.getSort();
        return PageRequest.of(page, size, sort);
    }
}

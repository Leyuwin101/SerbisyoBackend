package com.example.serbisyofullstack.search;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Provider discovery/search boundary. Kept separate from
 * {@code ProviderService} so the provider domain does not grow a giant
 * search implementation, and so the backing engine (PostgreSQL/PostGIS
 * today, a search engine later) can be swapped without touching callers.
 */
public interface ProviderSearchService {

    Page<ProviderSearchResult> search(ProviderSearchCriteria criteria, Pageable pageable, SearchSort sort);
}

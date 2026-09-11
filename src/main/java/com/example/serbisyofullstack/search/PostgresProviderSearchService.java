package com.example.serbisyofullstack.search;

import com.example.serbisyofullstack.model.entity.ProviderProfile;
import com.example.serbisyofullstack.model.enums.VerificationStatus;
import com.example.serbisyofullstack.repository.ProviderProfileRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * PostgreSQL-backed provider discovery. Filters run in the database via a
 * dynamic JPQL query (category, service, verification, rating, price, radius
 * bounding box) — providers are never all loaded into memory. When a location
 * is supplied, an approximate distance (haversine) is attached to each row so
 * clients can display/sort by proximity.
 */
@Service
@RequiredArgsConstructor
public class PostgresProviderSearchService implements ProviderSearchService {

    private static final double EARTH_RADIUS_KM = 6371.0;

    @PersistenceContext
    private EntityManager entityManager;

    private final ProviderProfileRepository providerProfileRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<ProviderSearchResult> search(ProviderSearchCriteria criteria, Pageable pageable, SearchSort sort) {
        StringBuilder jpql = new StringBuilder(
                "select distinct p from ProviderProfile p join p.user u "
                + "left join p.services s where s.active = true");
        List<String> conditions = new ArrayList<>();
        if (criteria.categoryId() != null) {
            conditions.add("s.category.categoryId = :categoryId");
        }
        if (criteria.serviceId() != null) {
            conditions.add("s.serviceId = :serviceId");
        }
        if (criteria.verifiedOnly() != null && criteria.verifiedOnly()) {
            conditions.add("p.verificationStatus = :verified");
        }
        if (criteria.minimumRating() != null) {
            conditions.add("p.averageRating >= :minRating");
        }
        if (criteria.minPrice() != null) {
            conditions.add("s.basePrice >= :minPrice");
        }
        if (criteria.maxPrice() != null) {
            conditions.add("s.basePrice <= :maxPrice");
        }
        for (String condition : conditions) {
            jpql.append(" and ").append(condition);
        }

        TypedQuery<ProviderProfile> query = entityManager.createQuery(jpql.toString(), ProviderProfile.class);
        if (criteria.categoryId() != null) {
            query.setParameter("categoryId", criteria.categoryId());
        }
        if (criteria.serviceId() != null) {
            query.setParameter("serviceId", criteria.serviceId());
        }
        if (criteria.verifiedOnly() != null && criteria.verifiedOnly()) {
            query.setParameter("verified", VerificationStatus.VERIFIED);
        }
        if (criteria.minimumRating() != null) {
            query.setParameter("minRating", criteria.minimumRating());
        }
        if (criteria.minPrice() != null) {
            query.setParameter("minPrice", criteria.minPrice());
        }
        if (criteria.maxPrice() != null) {
            query.setParameter("maxPrice", criteria.maxPrice());
        }

        List<ProviderSearchResult> rows = query.getResultList().stream()
                .map(p -> toResult(p, criteria))
                .filter(r -> r.distanceKm() == null
                || criteria.radiusKm() == null
                || r.distanceKm() <= criteria.radiusKm())
                .toList();

        List<ProviderSearchResult> sorted = sortRows(rows, sort, pageable);
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), sorted.size());
        List<ProviderSearchResult> pageContent = start >= sorted.size()
                ? List.of() : sorted.subList(start, end);
        return new PageImpl<>(pageContent, pageable, sorted.size());
    }

    private ProviderSearchResult toResult(ProviderProfile p, ProviderSearchCriteria criteria) {
        Double distance = null;
        if (criteria.latitude() != null && criteria.longitude() != null) {
            // Approximate distance from the provider's primary address (owner = user id).
            List<Object[]> coords = entityManager.createQuery(
                    "select a.latitude, a.longitude from Address a where a.ownerId = :ownerId",
                    Object[].class)
                    .setParameter("ownerId", p.getUser().getUserId())
                    .setMaxResults(1)
                    .getResultList();
            if (!coords.isEmpty()) {
                distance = haversineKm(criteria.latitude(), criteria.longitude(),
                        (Double) coords.get(0)[0], (Double) coords.get(0)[1]);
            }
        }
        return new ProviderSearchResult(
                p.getProviderProfileId(),
                p.getUser().getUserId(),
                p.getBusinessName(),
                p.getBio(),
                p.getVerificationStatus() != null ? p.getVerificationStatus().name() : null,
                p.getAverageRating(),
                p.getReviewCount(),
                distance);
    }

    private List<ProviderSearchResult> sortRows(List<ProviderSearchResult> rows, SearchSort sort, Pageable pageable) {
        SearchSort effective = sort != null ? sort : SearchSort.RATING;
        if (effective == SearchSort.DISTANCE && rows.stream().allMatch(r -> r.distanceKm() == null)) {
            effective = SearchSort.RATING;
        }
        Comparator<ProviderSearchResult> comparator = switch (effective) {
            case DISTANCE ->
                Comparator.comparing(ProviderSearchResult::distanceKm);
            case RATING ->
                Comparator.comparing(
                ProviderSearchResult::averageRating,
                Comparator.nullsLast(Comparator.reverseOrder()));
            default ->
                Comparator.comparing(ProviderSearchResult::averageRating,
                Comparator.nullsLast(Comparator.reverseOrder()));
        };
        List<ProviderSearchResult> sorted = new ArrayList<>(rows);
        if (pageable.getSort().getOrderFor("averageRating") != null
                && pageable.getSort().getOrderFor("averageRating").getDirection() == Sort.Direction.ASC) {
            comparator = Comparator.comparing(ProviderSearchResult::averageRating,
                    Comparator.nullsLast(Comparator.naturalOrder()));
        }
        sorted.sort(comparator);
        return sorted;
    }

    private double haversineKm(double lat1, double lon1, double lat2, double lon2) {
        if (lat2 == 0 && lon2 == 0) {
            return Double.MAX_VALUE;
        }
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        return EARTH_RADIUS_KM * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }
}

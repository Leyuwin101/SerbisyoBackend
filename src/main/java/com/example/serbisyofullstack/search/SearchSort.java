package com.example.serbisyofullstack.search;

/**
 * Supported sort orders for provider search. Distance is only meaningful when
 * the criteria include a location.
 */
public enum SearchSort {
    DISTANCE,
    RATING,
    PRICE_LOW_TO_HIGH,
    PRICE_HIGH_TO_LOW,
    RELEVANCE
}

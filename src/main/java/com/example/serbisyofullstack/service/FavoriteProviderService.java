package com.example.serbisyofullstack.service;

import com.example.serbisyofullstack.dto.request.favorite.FavoriteProviderRequest;
import com.example.serbisyofullstack.dto.nested.FavoriteProviderDto;
import com.example.serbisyofullstack.dto.response.favorite.FavoriteProviderResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Customer favorites: add/remove/list saved providers.
 */
public interface FavoriteProviderService {

    FavoriteProviderResponse addFavorite(Long customerUserId, FavoriteProviderRequest request);

    void removeFavorite(Long customerUserId, Long providerId);

    Page<FavoriteProviderDto> listFavorites(Long customerUserId, Pageable pageable);
}

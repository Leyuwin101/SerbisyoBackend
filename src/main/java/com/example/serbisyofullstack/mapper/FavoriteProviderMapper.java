package com.example.serbisyofullstack.mapper;

import com.example.serbisyofullstack.dto.nested.FavoriteProviderDto;
import com.example.serbisyofullstack.dto.request.favorite.FavoriteProviderRequest;
import com.example.serbisyofullstack.model.entity.FavoriteProvider;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

/**
 * Maps {@link FavoriteProvider} to {@link FavoriteProviderDto}. Favorites are
 * toggled by the service, so only entity-to-DTO mapping is exposed here.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface FavoriteProviderMapper {

    @Mapping(source = "favoriteProviderId", target = "id")
    @Mapping(source = "customer.customerProfileId", target = "customerId")
    @Mapping(source = "provider.providerProfileId", target = "providerId")
    FavoriteProviderDto toDto(FavoriteProvider entity);

    @Mapping(target = "favoriteProviderId", ignore = true)
    @Mapping(target = "customer", ignore = true)   // from the authenticated principal
    @Mapping(target = "provider", ignore = true)   // resolved by the service
    @Mapping(target = "createdAt", ignore = true)
    FavoriteProvider toEntity(FavoriteProviderRequest request);
}

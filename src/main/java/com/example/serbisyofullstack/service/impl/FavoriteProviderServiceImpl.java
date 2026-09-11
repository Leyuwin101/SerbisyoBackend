package com.example.serbisyofullstack.service.impl;

import com.example.serbisyofullstack.dto.nested.FavoriteProviderDto;
import com.example.serbisyofullstack.dto.nested.ProviderSummaryDto;
import com.example.serbisyofullstack.dto.request.favorite.FavoriteProviderRequest;
import com.example.serbisyofullstack.dto.response.favorite.FavoriteProviderResponse;
import com.example.serbisyofullstack.exception.ConflictException;
import com.example.serbisyofullstack.exception.ResourceNotFoundException;
import com.example.serbisyofullstack.mapper.FavoriteProviderMapper;
import com.example.serbisyofullstack.mapper.ProviderProfileMapper;
import com.example.serbisyofullstack.model.entity.CustomerProfile;
import com.example.serbisyofullstack.model.entity.FavoriteProvider;
import com.example.serbisyofullstack.model.entity.ProviderProfile;
import com.example.serbisyofullstack.repository.CustomerProfileRepository;
import com.example.serbisyofullstack.repository.FavoriteProviderRepository;
import com.example.serbisyofullstack.repository.ProviderProfileRepository;
import com.example.serbisyofullstack.service.FavoriteProviderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Customer favorites. Only the authenticated customer's own list is ever
 * touched: the customer profile is resolved from the passed user id, never from
 * the request body.
 */
@Service
@RequiredArgsConstructor
public class FavoriteProviderServiceImpl implements FavoriteProviderService {

    private final FavoriteProviderRepository favoriteProviderRepository;
    private final CustomerProfileRepository customerProfileRepository;
    private final ProviderProfileRepository providerProfileRepository;
    private final FavoriteProviderMapper favoriteProviderMapper;
    private final ProviderProfileMapper providerProfileMapper;

    @Override
    @Transactional
    public FavoriteProviderResponse addFavorite(Long customerUserId, FavoriteProviderRequest request) {
        CustomerProfile customer = customerProfileRepository.findByUserId(customerUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer profile not found for the current user"));
        ProviderProfile provider = providerProfileRepository.findById(request.getProviderId())
                .orElseThrow(() -> new ResourceNotFoundException("Provider not found"));
        if (favoriteProviderRepository.existsByCustomerIdAndProviderId(
                customer.getCustomerProfileId(), provider.getProviderProfileId())) {
            throw new ConflictException("Provider is already in your favorites");
        }
        FavoriteProvider favorite = new FavoriteProvider();
        favorite.setCustomer(customer);
        favorite.setProvider(provider);
        favorite = favoriteProviderRepository.save(favorite);

        FavoriteProviderResponse response = new FavoriteProviderResponse();
        response.setFavorite(toDtoWithSummary(favorite));
        response.setCreatedAt(favorite.getCreatedAt());
        return response;
    }

    @Override
    @Transactional
    public void removeFavorite(Long customerUserId, Long providerId) {
        CustomerProfile customer = customerProfileRepository.findByUserId(customerUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer profile not found for the current user"));
        if (!favoriteProviderRepository.existsByCustomerIdAndProviderId(
                customer.getCustomerProfileId(), providerId)) {
            throw new ResourceNotFoundException("Provider is not in your favorites");
        }
        favoriteProviderRepository.deleteByCustomerIdAndProviderId(customer.getCustomerProfileId(), providerId);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<FavoriteProviderDto> listFavorites(Long customerUserId, Pageable pageable) {
        CustomerProfile customer = customerProfileRepository.findByUserId(customerUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer profile not found for the current user"));
        List<FavoriteProvider> favorites = favoriteProviderRepository.findByCustomerId(customer.getCustomerProfileId());
        List<FavoriteProviderDto> dtos = favorites.stream().map(this::toDtoWithSummary).toList();
        return new PageImpl<>(dtos, pageable, dtos.size());
    }

    private FavoriteProviderDto toDtoWithSummary(FavoriteProvider favorite) {
        FavoriteProviderDto dto = favoriteProviderMapper.toDto(favorite);
        dto.setProvider(providerProfileMapper.toDto(favorite.getProvider()));
        return dto;
    }
}

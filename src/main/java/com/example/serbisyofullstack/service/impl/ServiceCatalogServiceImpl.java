package com.example.serbisyofullstack.service.impl;

import com.example.serbisyofullstack.dto.nested.ServiceCategoryDto;
import com.example.serbisyofullstack.dto.nested.ServiceSummaryDto;
import com.example.serbisyofullstack.dto.request.category.CreateServiceCategoryRequest;
import com.example.serbisyofullstack.dto.request.category.UpdateServiceCategoryRequest;
import com.example.serbisyofullstack.dto.request.service.CreateServiceRequest;
import com.example.serbisyofullstack.dto.request.service.UpdateServiceRequest;
import com.example.serbisyofullstack.dto.response.category.CreateServiceCategoryResponse;
import com.example.serbisyofullstack.dto.response.category.UpdateServiceCategoryResponse;
import com.example.serbisyofullstack.dto.response.service.CreateServiceResponse;
import com.example.serbisyofullstack.dto.response.service.UpdateServiceResponse;
import com.example.serbisyofullstack.exception.ForbiddenException;
import com.example.serbisyofullstack.exception.ResourceNotFoundException;
import com.example.serbisyofullstack.exception.ValidationException;
import com.example.serbisyofullstack.mapper.ServiceCategoryMapper;
import com.example.serbisyofullstack.mapper.ServiceMapper;
import com.example.serbisyofullstack.model.entity.ProviderProfile;
import com.example.serbisyofullstack.model.entity.Service;
import com.example.serbisyofullstack.model.entity.ServiceCategory;
import com.example.serbisyofullstack.repository.ProviderProfileRepository;
import com.example.serbisyofullstack.repository.ServiceCategoryRepository;
import com.example.serbisyofullstack.repository.ServiceRepository;
import com.example.serbisyofullstack.service.ServiceCatalogService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * Catalog business rules: category management and provider-owned services.
 * Client-supplied providerId/price/active status are never trusted — they are
 * resolved from the authenticated user and the database.
 */
@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class ServiceCatalogServiceImpl implements ServiceCatalogService {

    private final ServiceRepository serviceRepository;
    private final ServiceCategoryRepository categoryRepository;
    private final ProviderProfileRepository providerProfileRepository;
    private final ServiceMapper serviceMapper;
    private final ServiceCategoryMapper categoryMapper;

    @Override
    @Transactional
    public CreateServiceResponse createService(Long providerUserId, CreateServiceRequest request) {
        ProviderProfile provider = requireOwnProvider(providerUserId);
        ServiceCategory category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ValidationException("Invalid category"));
        validatePricing(request.getBasePrice());

        Service service = serviceMapper.toEntity(request);
        service.setProvider(provider);
        service.setCategory(category);
        service.setActive(true); // authoritative default; never client-controlled
        service = serviceRepository.save(service);

        CreateServiceResponse response = new CreateServiceResponse();
        response.setService(serviceMapper.toDto(service));
        return response;
    }

    @Override
    @Transactional
    public UpdateServiceResponse updateService(Long providerUserId, Long serviceId, UpdateServiceRequest request) {
        Service service = requireOwnedService(providerUserId, serviceId);
        serviceMapper.toUpdate(request, service);
        validatePricing(service.getBasePrice());
        service = serviceRepository.save(service);
        UpdateServiceResponse response = new UpdateServiceResponse();
        response.setService(serviceMapper.toDto(service));
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public ServiceSummaryDto getService(Long serviceId) {
        return serviceRepository.findById(serviceId)
                .map(serviceMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Service not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ServiceSummaryDto> listServicesByCategory(Long categoryId, Pageable pageable) {
        return serviceRepository.findByCategoryId(categoryId, pageable).map(serviceMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ServiceSummaryDto> listOwnServices(Long providerUserId, Pageable pageable) {
        ProviderProfile provider = requireOwnProvider(providerUserId);
        return serviceRepository.findByProviderId(provider.getProviderProfileId(), pageable)
                .map(serviceMapper::toDto);
    }

    @Override
    @Transactional
    public void activateService(Long providerUserId, Long serviceId) {
        setServiceActive(providerUserId, serviceId, true);
    }

    @Override
    @Transactional
    public void deactivateService(Long providerUserId, Long serviceId) {
        setServiceActive(providerUserId, serviceId, false);
    }

    @Override
    @Transactional
    public void deleteService(Long providerUserId, Long serviceId) {
        Service service = requireOwnedService(providerUserId, serviceId);
        serviceRepository.delete(service);
    }

    @Override
    @Transactional
    public CreateServiceCategoryResponse createCategory(CreateServiceCategoryRequest request) {
        ServiceCategory category = categoryMapper.toEntity(request);
        category = categoryRepository.save(category);
        CreateServiceCategoryResponse response = new CreateServiceCategoryResponse();
        response.setCategory(categoryMapper.toDto(category));
        return response;
    }

    @Override
    @Transactional
    public UpdateServiceCategoryResponse updateCategory(Long categoryId, UpdateServiceCategoryRequest request) {
        ServiceCategory category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        categoryMapper.toUpdate(request, category);
        category = categoryRepository.save(category);
        UpdateServiceCategoryResponse response = new UpdateServiceCategoryResponse();
        response.setCategory(categoryMapper.toDto(category));
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ServiceCategoryDto> listCategories(Pageable pageable) {
        return categoryRepository.findAll(pageable).map(categoryMapper::toDto);
    }

    @Override
    public void assertOwnership(Service service, Long currentUserId) {
        if (service == null || service.getProvider() == null || service.getProvider().getUser() == null
                || !service.getProvider().getUser().getUserId().equals(currentUserId)) {
            throw new ForbiddenException("You do not own this service");
        }
    }

    // ---------- helpers ----------
    private void setServiceActive(Long providerUserId, Long serviceId, boolean active) {
        Service service = requireOwnedService(providerUserId, serviceId);
        service.setActive(active);
        serviceRepository.save(service);
    }

    private Service requireOwnedService(Long providerUserId, Long serviceId) {
        Service service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new ResourceNotFoundException("Service not found"));
        assertOwnership(service, providerUserId);
        return service;
    }

    private ProviderProfile requireOwnProvider(Long providerUserId) {
        return providerProfileRepository.findByUserId(providerUserId)
                .orElseThrow(() -> new ResourceNotFoundException("No provider profile for the current user"));
    }

    private void validatePricing(BigDecimal price) {
        if (price == null || price.signum() < 0) {
            throw new ValidationException("Price must be a non-negative amount");
        }
        if (price.scale() > 2) {
            throw new ValidationException("Price must not have more than 2 decimal places");
        }
    }
}

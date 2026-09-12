package com.example.serbisyofullstack.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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
import com.example.serbisyofullstack.model.entity.Service;

/**
 * Service catalog use cases: category management and provider-owned services.
 * Client-supplied providerId/price/active status are never trusted; they are
 * resolved from the authenticated user and the database.
 */
public interface ServiceCatalogService {

    CreateServiceResponse createService(Long providerUserId, CreateServiceRequest request);

    UpdateServiceResponse updateService(Long providerUserId, Long serviceId, UpdateServiceRequest request);

    ServiceSummaryDto getService(Long serviceId);

    Page<ServiceSummaryDto> listServicesByCategory(Long categoryId, Pageable pageable);

    Page<ServiceSummaryDto> browseServices(Pageable pageable);

    Page<ServiceSummaryDto> searchServices(String keywords, Pageable pageable);

    Page<ServiceSummaryDto> listOwnServices(Long providerUserId, Pageable pageable);

    void activateService(Long providerUserId, Long serviceId);

    void deactivateService(Long providerUserId, Long serviceId);

    void deleteService(Long providerUserId, Long serviceId);

    CreateServiceCategoryResponse createCategory(CreateServiceCategoryRequest request);

    UpdateServiceCategoryResponse updateCategory(Long categoryId, UpdateServiceCategoryRequest request);

    Page<ServiceCategoryDto> listCategories(Pageable pageable);

    void assertOwnership(Service service, Long currentUserId);
}

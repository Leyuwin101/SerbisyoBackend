package com.example.serbisyofullstack.dto.response.service;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

import com.example.serbisyofullstack.dto.nested.ServiceImageDto;
import com.example.serbisyofullstack.dto.nested.ServiceSummaryDto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Response body for creating a service. The nested {@link ServiceSummaryDto}
 * carries the service core; images are returned as a {@link ServiceImageDto}
 * list with their final gallery ordering.
 */
@Getter
@Setter
@NoArgsConstructor
public class CreateServiceResponse {

    @Schema(description = "The created service, including its server-generated id.")
    private ServiceSummaryDto service;

    @Schema(description = "Stored gallery images in display order.")
    private List<ServiceImageDto> images;
}

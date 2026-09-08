package com.example.serbisyofullstack.dto.nested;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ServiceImageDto {

    private Long id;

    private Long serviceId;

    private String imageUrl;

    private Integer displayOrder;
}

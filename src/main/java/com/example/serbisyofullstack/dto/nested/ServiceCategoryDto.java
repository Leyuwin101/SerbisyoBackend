package com.example.serbisyofullstack.dto.nested;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ServiceCategoryDto {

    private Long id;

    private String name;

    private String description;

    private Boolean active;
}

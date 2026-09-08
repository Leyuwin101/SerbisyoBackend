package com.example.serbisyofullstack.dto.nested;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
public class AddressDto {

    private Long id;

    private String label;

    private String addressLine;

    private String locality;

    private String city;

    private String region;

    private String postalCode;

    private String country;

    private BigDecimal latitude;

    private BigDecimal longitude;
}

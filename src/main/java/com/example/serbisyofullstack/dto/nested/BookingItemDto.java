package com.example.serbisyofullstack.dto.nested;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
public class BookingItemDto {

    private Long id;

    private Long bookingId;

    private Long serviceId;

    private String name;

    private Integer quantity;

    private BigDecimal unitPrice;

    private BigDecimal totalPrice;
}

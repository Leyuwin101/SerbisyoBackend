package com.example.serbisyofullstack.dto.request.notification;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ApplyCouponRequest {

    @NotBlank(message = "Coupon code is required")
    @Size(max = 50)
    private String code;

}

package com.example.serbisyofullstack.service;

import com.example.serbisyofullstack.dto.request.coupon.ApplyCouponRequest;
import com.example.serbisyofullstack.dto.request.coupon.CreateCouponRequest;
import com.example.serbisyofullstack.dto.request.coupon.UpdateCouponRequest;
import com.example.serbisyofullstack.dto.nested.CouponDto;
import com.example.serbisyofullstack.dto.response.coupon.ApplyCouponResponse;
import com.example.serbisyofullstack.dto.response.coupon.CreateCouponResponse;
import com.example.serbisyofullstack.dto.response.coupon.UpdateCouponResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;

/**
 * Coupons: admin CRUD plus customer application with validity/limit checks.
 * Concurrency on usageCount is protected by optimistic locking.
 */
public interface CouponService {

    CreateCouponResponse createCoupon(CreateCouponRequest request);

    UpdateCouponResponse updateCoupon(UpdateCouponRequest request);

    void deactivateCoupon(String code);

    Page<CouponDto> listCoupons(Pageable pageable);

    ApplyCouponResponse applyCoupon(Long bookingId, ApplyCouponRequest request);

    BigDecimal calculateDiscount(String code, BigDecimal orderAmount);
}

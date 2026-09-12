package com.example.serbisyofullstack.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.serbisyofullstack.api.PaginationGuard;
import com.example.serbisyofullstack.dto.nested.CouponDto;
import com.example.serbisyofullstack.dto.request.coupon.ApplyCouponRequest;
import com.example.serbisyofullstack.dto.request.coupon.CreateCouponRequest;
import com.example.serbisyofullstack.dto.request.coupon.UpdateCouponRequest;
import com.example.serbisyofullstack.dto.response.coupon.ApplyCouponResponse;
import com.example.serbisyofullstack.dto.response.coupon.CreateCouponResponse;
import com.example.serbisyofullstack.dto.response.coupon.UpdateCouponResponse;
import com.example.serbisyofullstack.security.CurrentUserService;
import com.example.serbisyofullstack.service.CouponService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * Coupon endpoints: admin CRUD plus customer application during checkout.
 * Coupon application belongs to the booking owner (enforced in
 * {@link com.example.serbisyofullstack.service.impl.CouponServiceImpl}).
 */
@Tag(name = "Coupons", description = "Coupon management (admin) and checkout application (customer)")
@RestController
@RequestMapping("/api/v1/coupons")
@RequiredArgsConstructor
public class CouponController {

    private final CouponService couponService;
    private final CurrentUserService currentUserService;

    // ---------- admin ----------
    @Operation(summary = "Create a coupon (admin/moderator)")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Coupon created"),
        @ApiResponse(responseCode = "400", description = "Validation failed"),
        @ApiResponse(responseCode = "401", description = "Not authenticated"),
        @ApiResponse(responseCode = "403", description = "Caller is not an admin/moderator"),
        @ApiResponse(responseCode = "409", description = "Coupon code already exists")
    })
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','MODERATOR')")
    public ResponseEntity<CreateCouponResponse> createCoupon(
            @Valid @RequestBody CreateCouponRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(couponService.createCoupon(request));
    }

    @Operation(summary = "Update a coupon (admin/moderator)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Coupon updated"),
        @ApiResponse(responseCode = "400", description = "Validation failed"),
        @ApiResponse(responseCode = "401", description = "Not authenticated"),
        @ApiResponse(responseCode = "403", description = "Caller is not an admin/moderator"),
        @ApiResponse(responseCode = "404", description = "Coupon not found")
    })
    @PutMapping
    @PreAuthorize("hasAnyRole('ADMIN','MODERATOR')")
    public ResponseEntity<UpdateCouponResponse> updateCoupon(
            @Valid @RequestBody UpdateCouponRequest request) {
        return ResponseEntity.ok(couponService.updateCoupon(request));
    }

    @Operation(summary = "Deactivate a coupon by code (admin/moderator)")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Coupon deactivated"),
        @ApiResponse(responseCode = "401", description = "Not authenticated"),
        @ApiResponse(responseCode = "403", description = "Caller is not an admin/moderator"),
        @ApiResponse(responseCode = "404", description = "Coupon not found")
    })
    @DeleteMapping("/{code}")
    @PreAuthorize("hasAnyRole('ADMIN','MODERATOR')")
    public ResponseEntity<Void> deactivateCoupon(@PathVariable String code) {
        couponService.deactivateCoupon(code);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "List coupons (admin/moderator)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Coupons retrieved"),
        @ApiResponse(responseCode = "401", description = "Not authenticated"),
        @ApiResponse(responseCode = "403", description = "Caller is not an admin/moderator")
    })
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','MODERATOR')")
    public ResponseEntity<Page<CouponDto>> listCoupons(
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(couponService.listCoupons(PaginationGuard.cap(pageable)));
    }

    // ---------- customer ----------
    @Operation(summary = "Apply a coupon to a booking")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Coupon applied"),
        @ApiResponse(responseCode = "400", description = "Validation failed or coupon invalid/expired"),
        @ApiResponse(responseCode = "401", description = "Not authenticated"),
        @ApiResponse(responseCode = "403", description = "Booking does not belong to the caller"),
        @ApiResponse(responseCode = "404", description = "Booking or coupon not found"),
        @ApiResponse(responseCode = "409", description = "Coupon usage limit reached")
    })
    @PostMapping("/bookings/{bookingId}/apply")
    public ResponseEntity<ApplyCouponResponse> applyCoupon(
            @PathVariable Long bookingId,
            @Valid @RequestBody ApplyCouponRequest request) {
        // Identity of the caller is the authenticated user; the coupon service
        // verifies the booking belongs to them before applying anything.
        return ResponseEntity.ok(couponService.applyCoupon(bookingId, request));
    }
}

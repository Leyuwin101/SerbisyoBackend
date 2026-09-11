package com.example.serbisyofullstack.service.impl;

import com.example.serbisyofullstack.dto.nested.CouponDto;
import com.example.serbisyofullstack.dto.request.coupon.ApplyCouponRequest;
import com.example.serbisyofullstack.dto.request.coupon.CreateCouponRequest;
import com.example.serbisyofullstack.dto.request.coupon.UpdateCouponRequest;
import com.example.serbisyofullstack.dto.response.coupon.ApplyCouponResponse;
import com.example.serbisyofullstack.dto.response.coupon.CreateCouponResponse;
import com.example.serbisyofullstack.dto.response.coupon.UpdateCouponResponse;
import com.example.serbisyofullstack.exception.DuplicateResourceException;
import com.example.serbisyofullstack.exception.ResourceNotFoundException;
import com.example.serbisyofullstack.exception.ValidationException;
import com.example.serbisyofullstack.mapper.CouponMapper;
import com.example.serbisyofullstack.model.entity.Booking;
import com.example.serbisyofullstack.model.entity.BookingCoupon;
import com.example.serbisyofullstack.model.entity.Coupon;
import com.example.serbisyofullstack.repository.BookingCouponRepository;
import com.example.serbisyofullstack.repository.BookingRepository;
import com.example.serbisyofullstack.repository.CouponRepository;
import com.example.serbisyofullstack.service.CouponService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

/**
 * Coupon administration and checkout application. The discount is always
 * computed server-side from the booking's trusted quoted amount; the client
 * never supplies the order total. {@code usageCount} increments only through
 * successful redemption and is persisted atomically with the BookingCoupon row
 * so concurrent redemptions cannot overspend a limited coupon.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CouponServiceImpl implements CouponService {

    private final CouponRepository couponRepository;
    private final BookingRepository bookingRepository;
    private final BookingCouponRepository bookingCouponRepository;
    private final CouponMapper couponMapper;

    @Override
    @Transactional
    public CreateCouponResponse createCoupon(CreateCouponRequest request) {
        validateDiscountFields(request.getDiscountPercent(), request.getDiscountAmount());
        validateWindow(request.getValidFrom(), request.getValidUntil());
        if (couponRepository.existsByCodeIgnoreCase(request.getCode())) {
            throw new DuplicateResourceException("A coupon with code '" + request.getCode() + "' already exists");
        }
        Coupon coupon = couponMapper.toEntity(request);
        coupon = couponRepository.save(coupon);
        CreateCouponResponse response = new CreateCouponResponse();
        response.setCoupon(couponMapper.toDto(coupon));
        response.setCreatedAt(coupon.getCreatedAt());
        return response;
    }

    @Override
    @Transactional
    public UpdateCouponResponse updateCoupon(UpdateCouponRequest request) {
        if (request.getCode() == null) {
            throw new ValidationException("Coupon code is required to identify the coupon to update");
        }
        Coupon coupon = couponRepository.findByCodeIgnoreCase(request.getCode())
                .orElseThrow(() -> new ResourceNotFoundException("Coupon not found: " + request.getCode()));
        if (request.getDiscountPercent() != null && request.getDiscountPercent().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("Discount percent must be greater than zero");
        }
        couponMapper.toUpdate(request, coupon);
        if (coupon.getValidFrom() != null && coupon.getValidUntil() != null
                && coupon.getValidUntil().isBefore(coupon.getValidFrom())) {
            throw new ValidationException("validUntil must not be before validFrom");
        }
        if (request.getCode() != null && !request.getCode().equalsIgnoreCase(coupon.getCode())) {
            throw new ValidationException("Coupon code is immutable");
        }
        coupon = couponRepository.save(coupon);
        UpdateCouponResponse response = new UpdateCouponResponse();
        response.setCoupon(couponMapper.toDto(coupon));
        response.setUpdatedAt(LocalDateTime.now());
        return response;
    }

    @Override
    @Transactional
    public void deactivateCoupon(String code) {
        Coupon coupon = couponRepository.findByCodeIgnoreCase(code)
                .orElseThrow(() -> new ResourceNotFoundException("Coupon not found: " + code));
        coupon.setActive(false);
        couponRepository.save(coupon);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CouponDto> listCoupons(Pageable pageable) {
        return couponRepository.findAll(pageable).map(couponMapper::toDto);
    }

    @Override
    @Transactional
    public ApplyCouponResponse applyCoupon(Long bookingId, ApplyCouponRequest request) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
        if (bookingCouponRepository.existsByBookingId(bookingId)) {
            throw new ValidationException("A coupon has already been applied to this booking");
        }
        String message = validateForRedemption(request.getCode(), booking.getQuotedAmount());
        if (message != null) {
            return notApplied(request.getCode(), message);
        }
        Coupon coupon = couponRepository.findByCodeIgnoreCase(request.getCode()).orElseThrow();
        BigDecimal discount = calculateDiscount(coupon.getCode(), booking.getQuotedAmount());

        BookingCoupon link = new BookingCoupon();
        link.setBooking(booking);
        link.setCoupon(coupon);
        link.setDiscountAmount(discount);
        bookingCouponRepository.save(link);

        coupon.setUsageCount(coupon.getUsageCount() + 1);
        couponRepository.save(coupon);

        log.info("Coupon {} applied to booking {} with discount {}", coupon.getCode(), bookingId, discount);
        return applied(request.getCode(), discount);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal calculateDiscount(String code, BigDecimal orderAmount) {
        Coupon coupon = couponRepository.findByCodeIgnoreCase(code)
                .orElseThrow(() -> new ResourceNotFoundException("Coupon not found: " + code));
        if (!isCurrentlyRedeemable(coupon, orderAmount)) {
            return BigDecimal.ZERO;
        }
        BigDecimal discount = BigDecimal.ZERO;
        if (coupon.getDiscountAmount() != null) {
            discount = discount.add(coupon.getDiscountAmount());
        }
        if (coupon.getDiscountPercent() != null) {
            discount = discount.add(orderAmount.multiply(coupon.getDiscountPercent())
                    .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP));
        }
        return discount.min(orderAmount).setScale(2, RoundingMode.HALF_UP);
    }

    // --- helpers -------------------------------------------------------------
    private String validateForRedemption(String code, BigDecimal orderAmount) {
        Coupon coupon = couponRepository.findByCodeIgnoreCase(code).orElse(null);
        if (coupon == null) {
            return "Unknown coupon code";
        }
        if (!Boolean.TRUE.equals(coupon.getActive())) {
            return "Coupon is no longer active";
        }
        LocalDateTime now = LocalDateTime.now();
        if (coupon.getValidFrom() != null && now.isBefore(coupon.getValidFrom())) {
            return "Coupon is not valid yet";
        }
        if (coupon.getValidUntil() != null && now.isAfter(coupon.getValidUntil())) {
            return "Coupon has expired";
        }
        if (coupon.getUsageLimit() != null && coupon.getUsageCount() >= coupon.getUsageLimit()) {
            return "Coupon usage limit reached";
        }
        if (coupon.getMinimumOrderAmount() != null
                && (orderAmount == null || orderAmount.compareTo(coupon.getMinimumOrderAmount()) < 0)) {
            return "Order total does not meet the coupon minimum";
        }
        return null;
    }

    private boolean isCurrentlyRedeemable(Coupon coupon, BigDecimal orderAmount) {
        return Boolean.TRUE.equals(coupon.getActive())
                && (coupon.getValidFrom() == null || !LocalDateTime.now().isBefore(coupon.getValidFrom()))
                && (coupon.getValidUntil() == null || !LocalDateTime.now().isAfter(coupon.getValidUntil()))
                && (coupon.getUsageLimit() == null || coupon.getUsageCount() < coupon.getUsageLimit())
                && (coupon.getMinimumOrderAmount() == null
                || (orderAmount != null && orderAmount.compareTo(coupon.getMinimumOrderAmount()) >= 0));
    }

    private void validateDiscountFields(BigDecimal percent, BigDecimal amount) {
        if (percent == null && amount == null) {
            throw new ValidationException("A coupon must define a percentage, a fixed amount, or both");
        }
    }

    private void validateWindow(LocalDateTime validFrom, LocalDateTime validUntil) {
        if (validFrom != null && validUntil != null && validUntil.isBefore(validFrom)) {
            throw new ValidationException("validUntil must not be before validFrom");
        }
    }

    private ApplyCouponResponse applied(String code, BigDecimal discount) {
        ApplyCouponResponse response = new ApplyCouponResponse();
        response.setApplied(true);
        response.setCode(code);
        response.setDiscountAmount(discount);
        return response;
    }

    private ApplyCouponResponse notApplied(String code, String message) {
        ApplyCouponResponse response = new ApplyCouponResponse();
        response.setApplied(false);
        response.setCode(code);
        response.setMessage(message);
        return response;
    }
}

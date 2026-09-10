package com.example.serbisyofullstack.mapper;

import com.example.serbisyofullstack.dto.nested.BookingCouponDto;
import com.example.serbisyofullstack.model.entity.BookingCoupon;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

/**
 * Maps {@link BookingCoupon} to {@link BookingCouponDto}. Coupon application is
 * a service-layer decision, so only entity-to-DTO mapping exists.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BookingCouponMapper {

    @Mapping(source = "bookingCouponId", target = "bookingCouponId")
    @Mapping(source = "booking.bookingId", target = "bookingId")
    @Mapping(source = "coupon.couponId", target = "couponId")
    @Mapping(source = "coupon.code", target = "couponCode")
    BookingCouponDto toDto(BookingCoupon entity);
}

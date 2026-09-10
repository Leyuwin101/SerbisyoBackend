package com.example.serbisyofullstack.mapper;

import com.example.serbisyofullstack.dto.nested.CouponDto;
import com.example.serbisyofullstack.dto.request.coupon.CreateCouponRequest;
import com.example.serbisyofullstack.dto.request.coupon.UpdateCouponRequest;
import com.example.serbisyofullstack.model.entity.Coupon;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

/**
 * Maps between {@link Coupon} and {@link CouponDto}.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CouponMapper extends BaseMapper<Coupon, CreateCouponRequest, UpdateCouponRequest, CouponDto> {

    @Override
    @Mapping(source = "couponId", target = "id")
    CouponDto toDto(Coupon entity);

    @Override
    @Mapping(target = "couponId", ignore = true)
    @Mapping(target = "usageCount", ignore = true)   // always starts at 0
    @Mapping(target = "createdAt", ignore = true)
    Coupon toEntity(CreateCouponRequest request);

    @Override
    @Mapping(target = "couponId", ignore = true)
    @Mapping(target = "usageCount", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Coupon toUpdate(UpdateCouponRequest request, @MappingTarget Coupon entity);
}

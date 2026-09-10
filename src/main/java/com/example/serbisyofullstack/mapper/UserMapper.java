package com.example.serbisyofullstack.mapper;

import com.example.serbisyofullstack.dto.nested.UserSummaryDto;
import com.example.serbisyofullstack.dto.request.auth.RegisterRequest;
import com.example.serbisyofullstack.dto.request.user.UpdateUserRequest;
import com.example.serbisyofullstack.model.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

/**
 * Maps between {@link User} and its DTOs. Credentials (password hashing) and
 * role assignment are service concerns — intentionally not mapped here.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper extends BaseMapper<User, RegisterRequest, UpdateUserRequest, UserSummaryDto> {

    @Override
    @Mapping(source = "userId", target = "id")
    UserSummaryDto toDto(User entity);

    @Override
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "password", ignore = true)   // hashed in the service before persisting
    @Mapping(target = "status", ignore = true)     // set by registration flow
    @Mapping(target = "lastLoginAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    User toEntity(RegisterRequest request);

    @Override
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "lastLoginAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    User toUpdate(UpdateUserRequest request, @MappingTarget User entity);
}

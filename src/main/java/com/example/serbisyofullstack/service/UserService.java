package com.example.serbisyofullstack.service;

import com.example.serbisyofullstack.dto.request.user.UpdateUserRequest;
import com.example.serbisyofullstack.dto.nested.PageMetadataDto;
import com.example.serbisyofullstack.dto.nested.UserSummaryDto;
import com.example.serbisyofullstack.dto.response.user.UpdateUserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * General user lifecycle: profile retrieval, updates and account status.
 */
public interface UserService {

    UserSummaryDto getUser(Long userId);

    UpdateUserResponse updateUser(Long userId, UpdateUserRequest request);

    void deactivateAccount(Long userId);

    Page<UserSummaryDto> listUsers(Pageable pageable);
}

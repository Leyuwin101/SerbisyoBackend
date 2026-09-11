package com.example.serbisyofullstack.service.impl;

import com.example.serbisyofullstack.dto.nested.UserSummaryDto;
import com.example.serbisyofullstack.dto.request.user.UpdateUserRequest;
import com.example.serbisyofullstack.dto.response.user.UpdateUserResponse;
import com.example.serbisyofullstack.exception.ForbiddenException;
import com.example.serbisyofullstack.exception.ResourceNotFoundException;
import com.example.serbisyofullstack.mapper.UserMapper;
import com.example.serbisyofullstack.model.enums.Status;
import com.example.serbisyofullstack.repository.UserRepository;
import com.example.serbisyofullstack.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * User lifecycle: profile retrieval/update and account status. Role and
 * verification changes are NOT here — they belong to AdminService.
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    @Transactional(readOnly = true)
    public UserSummaryDto getUser(Long userId) {
        return userRepository.findById(userId)
                .map(userMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    @Override
    @Transactional
    public UpdateUserResponse updateUser(Long userId, UpdateUserRequest request) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        userMapper.toUpdate(request, user);
        user = userRepository.save(user);
        UpdateUserResponse response = new UpdateUserResponse();
        response.setUser(userMapper.toDto(user));
        return response;
    }

    @Override
    @Transactional
    public void deactivateAccount(Long userId) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setStatus(Status.INACTIVE);
        userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserSummaryDto> listUsers(Pageable pageable) {
        return userRepository.findAll(pageable).map(userMapper::toDto);
    }
}

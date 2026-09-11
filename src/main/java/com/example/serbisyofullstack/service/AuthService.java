package com.example.serbisyofullstack.service;

import com.example.serbisyofullstack.dto.request.auth.LoginRequest;
import com.example.serbisyofullstack.dto.request.auth.RefreshTokenRequest;
import com.example.serbisyofullstack.dto.request.auth.RegisterRequest;
import com.example.serbisyofullstack.dto.response.auth.ChangePasswordResponse;
import com.example.serbisyofullstack.dto.response.auth.LoginResponse;
import com.example.serbisyofullstack.dto.response.auth.RefreshTokenResponse;
import com.example.serbisyofullstack.dto.response.auth.RegisterResponse;

/**
 * Authentication use cases: registration, login, token refresh/rotation,
 * logout/revocation and password changes.
 */
public interface AuthService {

    RegisterResponse register(RegisterRequest request);

    LoginResponse login(LoginRequest request);

    RefreshTokenResponse refresh(RefreshTokenRequest request);

    void logout(String refreshToken);

    ChangePasswordResponse changePassword(Long userId, String currentPassword, String newPassword);
}

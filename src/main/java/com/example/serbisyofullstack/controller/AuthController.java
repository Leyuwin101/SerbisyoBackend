package com.example.serbisyofullstack.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.serbisyofullstack.api.RateLimiter;
import com.example.serbisyofullstack.dto.request.auth.ChangePasswordRequest;
import com.example.serbisyofullstack.dto.request.auth.LoginRequest;
import com.example.serbisyofullstack.dto.request.auth.RefreshTokenRequest;
import com.example.serbisyofullstack.dto.request.auth.RegisterRequest;
import com.example.serbisyofullstack.dto.response.auth.ChangePasswordResponse;
import com.example.serbisyofullstack.dto.response.auth.LoginResponse;
import com.example.serbisyofullstack.dto.response.auth.RefreshTokenResponse;
import com.example.serbisyofullstack.dto.response.auth.RegisterResponse;
import com.example.serbisyofullstack.exception.TooManyRequestsException;
import com.example.serbisyofullstack.security.CurrentUserService;
import com.example.serbisyofullstack.service.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * Authentication endpoints. Register/login/refresh are public and rate limited
 * per client IP; password change requires authentication (user id comes from
 * the security context, never the body).
 */
@Tag(name = "Authentication", description = "Register, login, token refresh, logout, password change")
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final CurrentUserService currentUserService;
    private final RateLimiter rateLimiter;

    @Operation(summary = "Register a new account")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Account created"),
        @ApiResponse(responseCode = "400", description = "Validation failed (e.g. passwords do not match)"),
        @ApiResponse(responseCode = "409", description = "Email already registered"),
        @ApiResponse(responseCode = "429", description = "Rate limit exceeded")
    })
    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(
            @Valid @RequestBody RegisterRequest request,
            HttpServletRequest httpRequest) {
        requireClientSlot("auth:register", httpRequest, 10, 60_000);
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
    }

    @Operation(summary = "Log in with email and password")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Logged in; returns access and refresh tokens"),
        @ApiResponse(responseCode = "400", description = "Validation failed"),
        @ApiResponse(responseCode = "401", description = "Invalid credentials"),
        @ApiResponse(responseCode = "429", description = "Rate limit exceeded")
    })
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest httpRequest) {
        requireClientSlot("auth:login", httpRequest, 10, 60_000);
        return ResponseEntity.ok(authService.login(request));
    }

    @Operation(summary = "Rotate the refresh token")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "New token pair issued"),
        @ApiResponse(responseCode = "400", description = "Validation failed"),
        @ApiResponse(responseCode = "401", description = "Refresh token invalid or expired"),
        @ApiResponse(responseCode = "429", description = "Rate limit exceeded")
    })
    @PostMapping("/refresh")
    public ResponseEntity<RefreshTokenResponse> refresh(
            @Valid @RequestBody RefreshTokenRequest request,
            HttpServletRequest httpRequest) {
        requireClientSlot("auth:refresh", httpRequest, 30, 60_000);
        return ResponseEntity.ok(authService.refresh(request));
    }

    @Operation(summary = "Revoke the refresh token")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Refresh token revoked"),
        @ApiResponse(responseCode = "400", description = "Validation failed"),
        @ApiResponse(responseCode = "429", description = "Rate limit exceeded")
    })
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @Valid @RequestBody RefreshTokenRequest request,
            HttpServletRequest httpRequest) {
        requireClientSlot("auth:logout", httpRequest, 30, 60_000);
        authService.logout(request.getToken());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Change the authenticated user's password")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Password changed"),
        @ApiResponse(responseCode = "400", description = "Validation failed"),
        @ApiResponse(responseCode = "401", description = "Not authenticated or wrong current password")
    })
    @PostMapping("/change-password")
    public ResponseEntity<ChangePasswordResponse> changePassword(
            @Valid @RequestBody ChangePasswordRequest request) {
        // The client cannot confirm its identity via the body; the new password
        // applies only to the authenticated principal.
        return ResponseEntity.ok(authService.changePassword(
                currentUserService.getCurrentUserId(),
                request.getCurrentPassword(),
                request.getNewPassword()));
    }

    private void requireClientSlot(String action, HttpServletRequest request, int limit, long windowMillis) {
        String ip = request.getRemoteAddr();
        if (!rateLimiter.tryAcquire(action + ":" + ip, limit, windowMillis)) {
            throw new TooManyRequestsException("Too many requests, please try again later");
        }
    }
}

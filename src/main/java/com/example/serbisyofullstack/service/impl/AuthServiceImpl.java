package com.example.serbisyofullstack.service.impl;

import com.example.serbisyofullstack.dto.request.auth.LoginRequest;
import com.example.serbisyofullstack.dto.request.auth.RefreshTokenRequest;
import com.example.serbisyofullstack.dto.request.auth.RegisterRequest;
import com.example.serbisyofullstack.dto.response.auth.ChangePasswordResponse;
import com.example.serbisyofullstack.dto.response.auth.LoginResponse;
import com.example.serbisyofullstack.dto.response.auth.RefreshTokenResponse;
import com.example.serbisyofullstack.dto.response.auth.RegisterResponse;
import com.example.serbisyofullstack.dto.nested.UserSummaryDto;
import com.example.serbisyofullstack.exception.ConflictException;
import com.example.serbisyofullstack.exception.ResourceNotFoundException;
import com.example.serbisyofullstack.exception.UnauthorizedException;
import com.example.serbisyofullstack.mapper.UserMapper;
import com.example.serbisyofullstack.model.entity.RefreshToken;
import com.example.serbisyofullstack.model.entity.Role;
import com.example.serbisyofullstack.model.entity.User;
import com.example.serbisyofullstack.model.entity.UserRole;
import com.example.serbisyofullstack.model.enums.RoleEnum;
import com.example.serbisyofullstack.model.enums.Status;
import com.example.serbisyofullstack.repository.RefreshTokenRepository;
import com.example.serbisyofullstack.repository.RoleRepository;
import com.example.serbisyofullstack.repository.UserRepository;
import com.example.serbisyofullstack.security.JwtService;
import com.example.serbisyofullstack.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HexFormat;
import java.util.List;
import java.util.Set;

/**
 * Authentication business rules: registration, credential verification,
 * access-token issuance, refresh-token rotation with revocation, logout and
 * password changes. Password hashes and tokens are never logged.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserMapper userMapper;
    private final com.example.serbisyofullstack.config.ApplicationProperties properties;

    private static final SecureRandom RANDOM = new SecureRandom();

    @Override
    @Transactional
    public RegisterResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException("Email is already registered");
        }
        if (userRepository.existsByPhone(request.getPhone())) {
            throw new ConflictException("Phone number is already registered");
        }

        User user = new User();
        user.setEmail(request.getEmail().trim().toLowerCase());
        user.setPhone(request.getPhone());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setStatus(Status.ACTIVE);
        user = userRepository.save(user);

        Set<RoleEnum> requested = (request.getRoles() == null || request.getRoles().isEmpty())
                ? Set.of(RoleEnum.CUSTOMER)
                : request.getRoles();
        for (RoleEnum code : requested) {
            Role role = roleRepository.findByName(code)
                    .orElseThrow(() -> new ResourceNotFoundException("Role not found: " + code));
            UserRole userRole = new UserRole();
            userRole.setUser(user);
            userRole.setRole(role);
            user.getRoles().add(userRole);
        }

        List<String> roleCodes = user.getRoles().stream()
                .map(ur -> ur.getRole().getName().name()).toList();
        String access = jwtService.generateToken(user.getUserId(), user.getEmail(), roleCodes);
        String refresh = issueRefreshToken(user);

        RegisterResponse response = new RegisterResponse();
        response.setAccessToken(access);
        response.setRefreshToken(refresh);
        response.setUser(userMapper.toDto(user));
        return response;
    }

    @Override
    @Transactional
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail().trim().toLowerCase())
                .orElseThrow(() -> new UnauthorizedException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            // Same message for unknown user and bad password — no account enumeration.
            throw new UnauthorizedException("Invalid email or password");
        }
        if (user.getStatus() != Status.ACTIVE) {
            throw new UnauthorizedException("Account is not active");
        }

        user.setLastLoginAt(LocalDateTime.now());
        List<String> roleCodes = user.getRoles().stream()
                .map(ur -> ur.getRole().getName().name()).toList();
        String access = jwtService.generateToken(user.getUserId(), user.getEmail(), roleCodes);
        String refresh = issueRefreshToken(user);

        LoginResponse response = new LoginResponse();
        response.setAccessToken(access);
        response.setRefreshToken(refresh);
        response.setUser(userMapper.toDto(user));
        return response;
    }

    @Override
    @Transactional
    public RefreshTokenResponse refresh(RefreshTokenRequest request) {
        String hash = hashToken(request.getToken());
        RefreshToken stored = refreshTokenRepository.findByTokenHash(hash)
                .orElseThrow(() -> new UnauthorizedException("Invalid refresh token"));

        if (stored.getRevokedAt() != null) {
            // Reuse of a revoked token: revoke the whole family defensively.
            revokeAllForUser(stored.getUser());
            throw new UnauthorizedException("Refresh token has been revoked");
        }
        if (stored.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new UnauthorizedException("Refresh token has expired");
        }

        // Rotation: revoke the used token, issue a new one.
        stored.setRevokedAt(LocalDateTime.now());
        User user = stored.getUser();
        String newRefresh = issueRefreshToken(user);

        List<String> roleCodes = user.getRoles().stream()
                .map(ur -> ur.getRole().getName().name()).toList();
        String access = jwtService.generateToken(user.getUserId(), user.getEmail(), roleCodes);

        RefreshTokenResponse response = new RefreshTokenResponse();
        response.setAccessToken(access);
        response.setRefreshToken(newRefresh);
        return response;
    }

    @Override
    @Transactional
    public void logout(String refreshToken) {
        refreshTokenRepository.findByTokenHash(hashToken(refreshToken))
                .ifPresent(token -> token.setRevokedAt(LocalDateTime.now()));
    }

    @Override
    @Transactional
    public ChangePasswordResponse changePassword(Long userId, String currentPassword, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new UnauthorizedException("Current password is incorrect");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        revokeAllForUser(user);
        ChangePasswordResponse response = new ChangePasswordResponse();
        response.setMessage("Password updated successfully");
        response.setChangedAt(LocalDateTime.now());
        return response;
    }

    private String issueRefreshToken(User user) {
        byte[] bytes = new byte[32];
        RANDOM.nextBytes(bytes);
        String raw = HexFormat.of().formatHex(bytes);
        RefreshToken token = new RefreshToken();
        token.setUser(user);
        token.setTokenHash(hashToken(raw));
        token.setExpiresAt(LocalDateTime.now().plusDays(properties.getSecurity().getRefreshTokenDays()));
        refreshTokenRepository.save(token);
        return raw; // only the hash is stored
    }

    private void revokeAllForUser(User user) {
        refreshTokenRepository.findByUserAndRevokedAtIsNull(user)
                .forEach(t -> t.setRevokedAt(LocalDateTime.now()));
    }

    private String hashToken(String raw) {
        return passwordEncoder.encode(raw);
    }
}

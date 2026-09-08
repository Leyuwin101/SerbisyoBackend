package com.example.serbisyofullstack.dto.nested;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class RefreshTokenDto {

    private Long refreshTokenId;

    private Long userId;

    private LocalDateTime expiresAt;

    private LocalDateTime revokedAt;

    private LocalDateTime createdAt;

    private String ipAddress;

    private String userAgent;

}

package com.example.serbisyofullstack.security;

import com.example.serbisyofullstack.config.ApplicationProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.List;

/**
 * Issue and verify short-lived JWT access tokens. The secret is loaded from
 * configuration (environment variable) — never hardcoded and never logged.
 */
@Slf4j
@Component
public class JwtService {

    private static final String CLAIM_ROLES = "roles";

    private final SecretKey key;
    private final long expirationMinutes;

    public JwtService(ApplicationProperties properties) {
        String secret = properties.getSecurity().getJwtSecret();
        if (secret == null || secret.length() < 32) {
            throw new IllegalStateException(
                    "app.security.jwt-secret must be set (>= 32 chars) via environment variable JWT_SECRET");
        }
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMinutes = properties.getSecurity().getJwtExpirationMinutes();
    }

    public String generateToken(Long userId, String email, List<String> roles) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("email", email)
                .claim(CLAIM_ROLES, roles)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(expirationMinutes, java.time.temporal.ChronoUnit.MINUTES)))
                .signWith(key)
                .compact();
    }

    /**
     * @return the claims of a valid token, or null when invalid/expired.
     */
    public Claims parseToken(String token) {
        try {
            return Jwts.parser().verifyWith(key).build()
                    .parseSignedClaims(token).getPayload();
        } catch (JwtException | IllegalArgumentException e) {
            log.debug("Rejected invalid JWT: {}", e.getMessage());
            return null;
        }
    }

    public Long extractUserId(Claims claims) {
        return Long.valueOf(claims.getSubject());
    }

    @SuppressWarnings("unchecked")
    public List<String> extractRoles(Claims claims) {
        return claims.get(CLAIM_ROLES, List.class) == null
                ? List.of() : claims.get(CLAIM_ROLES, List.class);
    }
}

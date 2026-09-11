package com.example.serbisyofullstack.repository;

import com.example.serbisyofullstack.model.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByTokenHash(String tokenHash);

    List<RefreshToken> findByUserAndRevokedAtIsNull(com.example.serbisyofullstack.model.entity.User user);

    void deleteByUserId(Long userId);
}

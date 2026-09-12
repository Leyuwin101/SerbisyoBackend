package com.example.serbisyofullstack.repository;

import com.example.serbisyofullstack.model.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByTokenHash(String tokenHash);

    List<RefreshToken> findByUserAndRevokedAtIsNull(com.example.serbisyofullstack.model.entity.User user);

    @Modifying
    @Query("DELETE FROM RefreshToken rt WHERE rt.user.userId = :userId")
    void deleteByUserId(@Param("userId") Long userId);
}

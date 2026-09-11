package com.example.serbisyofullstack.repository;

import com.example.serbisyofullstack.model.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<Notification> findByUserIdAndReadAtIsNullOrderByCreatedAtDesc(Long userId);

    List<Notification> findByUserIdAndReadAtIsNull(Long userId);

    Page<Notification> findByUserId(Long userId, Pageable pageable);

    Page<Notification> findByUserIdAndReadAtIsNull(Long userId, Pageable pageable);

    long countByUserIdAndReadAtIsNull(Long userId);
}

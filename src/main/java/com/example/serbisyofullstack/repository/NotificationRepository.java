package com.example.serbisyofullstack.repository;

import com.example.serbisyofullstack.model.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByUser_UserIdOrderByCreatedAtDesc(Long userId);

    List<Notification> findByUser_UserIdAndReadAtIsNullOrderByCreatedAtDesc(Long userId);

    List<Notification> findByUser_UserIdAndReadAtIsNull(Long userId);

    Page<Notification> findByUser_UserId(Long userId, Pageable pageable);

    Page<Notification> findByUser_UserIdAndReadAtIsNull(Long userId, Pageable pageable);

    long countByUser_UserIdAndReadAtIsNull(Long userId);

    @Query("SELECT n FROM Notification n WHERE n.user.userId = :userId")
    Page<Notification> findByUserId(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT n FROM Notification n WHERE n.user.userId = :userId AND n.readAt IS NULL")
    Page<Notification> findByUserIdAndReadAtIsNull(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT n FROM Notification n WHERE n.user.userId = :userId AND n.readAt IS NULL")
    List<Notification> findByUserIdAndReadAtIsNull(@Param("userId") Long userId);

    @Query("SELECT COUNT(n) FROM Notification n WHERE n.user.userId = :userId AND n.readAt IS NULL")
    long countByUserIdAndReadAtIsNull(@Param("userId") Long userId);
}

package com.example.serbisyofullstack.model.entity;

import com.example.serbisyofullstack.model.enums.Status;
import lombok.Getter;
import lombok.Setter;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(
        name = "users",
        indexes = {
                @Index(name = "idx_users_email", columnList = "email"),
                @Index(name = "idx_users_status", columnList = "status")
        }
)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "phone", nullable = false, unique = true)
    private String phone;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    @Column(name = "created_at")
    public LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
    private Set<UserRole> roles = new HashSet<>();


    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }



    /**
     * Maps this account to its API response representation
     * (see {@link com.example.serbisyofullstack.dto.response.user.UpdateUserResponse}).
     */
    public com.example.serbisyofullstack.dto.response.user.UpdateUserResponse toUpdateUserResponse() {
        com.example.serbisyofullstack.dto.response.user.UpdateUserResponse response =
                new com.example.serbisyofullstack.dto.response.user.UpdateUserResponse();
        com.example.serbisyofullstack.dto.nested.UserSummaryDto user =
                new com.example.serbisyofullstack.dto.nested.UserSummaryDto();
        user.setId(userId);
        user.setEmail(email);
        user.setPhone(phone);
        user.setStatus(status);
        response.setUser(user);
        return response;
    }
}

package com.example.serbisyofullstack.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "provider_payout_accounts")
public class ProviderPayoutAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payout_account_id")
    private Long payoutAccountId;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "provider_id", nullable = false, unique = true)
    private ProviderProfile provider;

    @Column(name = "gateway_account_reference", nullable = false)
    private String gatewayAccountReference;

    @Column(name = "payout_method")
    private String payoutMethod;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PayoutAccountStatus status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
        updatedAt = createdAt;
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}

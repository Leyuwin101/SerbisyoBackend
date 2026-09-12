package com.example.serbisyofullstack.model.entity;

import com.example.serbisyofullstack.model.enums.VerificationStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(
        name = "provider_profiles",
        indexes = {
                @Index(name = "idx_provider_user", columnList = "user_id"),
                @Index(name = "idx_provider_verification_rating", columnList = "verification_status, average_rating")
        })
public class ProviderProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "provider_profile_id")
    private Long providerProfileId;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "business_name", nullable = false)
    private String businessName;

    @Column(name = "bio", columnDefinition = "TEXT")
    private String bio;

    @Enumerated(EnumType.STRING)
    @Column(name = "verification_status", nullable = false)
    private VerificationStatus verificationStatus;

    @Column(name = "average_rating")
    private Double averageRating = 0.0;

    @Column(name = "review_count")
    private Integer reviewCount = 0;

    @OneToMany(mappedBy = "provider", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProviderDocument> documents = new ArrayList<>();

    @OneToMany(mappedBy = "provider", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Service> services = new ArrayList<>();

    @OneToMany(mappedBy = "provider", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AvailabilitySchedule> availabilitySchedules = new ArrayList<>();

    @OneToMany(mappedBy = "provider", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AvailabilityException> availabilityExceptions = new ArrayList<>();

    @OneToMany(mappedBy = "provider")
    private List<Review> reviews = new ArrayList<>();

    @OneToMany(mappedBy = "provider")
    private List<Payout> payouts = new ArrayList<>();

    @OneToOne(mappedBy = "provider")
    private ProviderPayoutAccount payoutAccount;
}

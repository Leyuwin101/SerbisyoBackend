package com.example.serbisyofullstack.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(
        name = "addresses",
        indexes = {
                @Index(name = "idx_addresses_owner", columnList = "owner")
        }
)
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "address_id")
    private Long addressId;

    @Column(name = "owner_id", nullable = false)
    private Long ownerId;

    @Column(name = "label")
    private String label;

    @Column(name = "address_line", nullable = false)
    private String addressLine;

    @Column(name = "locality")
    private String locality;

    @Column(name = "city", nullable = false)
    private String city;

    @Column(name = "region")
    private String region;

    @Column(name = "postal_code")
    private String postalCode;

    @Column(name = "country", nullable = false)
    private String country;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;
}

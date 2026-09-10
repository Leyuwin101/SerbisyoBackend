package com.example.serbisyofullstack.model.entity;

import com.example.serbisyofullstack.model.enums.ExceptionType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@Entity
@Table(name = "availability_exceptions")
public class AvailabilityException {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "availability_exception_id")
    private Long availabilityExceptionId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "provider_id", nullable = false)
    private ProviderProfile provider;

    @Column(name = "exception_date", nullable = false)
    private LocalDate exceptionDate;

    @Column(name = "start_time")
    private LocalTime startTime;

    @Column(name = "end_time")
    private LocalTime endTime;

    @Column(name = "available", nullable = false)
    private Boolean available;

    @Column(name = "exception_type")
    private ExceptionType exceptionType;

    @Column(name = "reason")
    private String reason;

}

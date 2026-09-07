package com.example.serbisyofullstack.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.DayOfWeek;
import java.time.LocalTime;


@Getter
@Setter
@Entity
@Table(name = "availability_schedules")
public class AvailabilitySchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "availability_schedule_id")
    private Long availabilityScheduleId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "provider_id", nullable = false)
    private ProviderProfile provider;

    @Enumerated(EnumType.STRING)
    @Column(name = "weekday", nullable = false)
    private DayOfWeek weekday;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @Column(name = "timezone", nullable = false)
    private String timezone;

    @Column(name = "active", nullable = false)
    private Boolean active = true;


}

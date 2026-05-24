package com.uber.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Entity
@Table(name="rideRate")
@Getter
@Setter
@FieldDefaults(level= AccessLevel.PRIVATE)
public class RideRating {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    Long id;

    @OneToOne
    @JoinColumn(name="ride_id")
    Ride ride;

    @OneToOne
    @JoinColumn(name = "rider_id")
    User rider;

    @OneToOne
    @JoinColumn(name="driver_id")
    Driver driver;

    Integer driverRating;

    Integer riderRating;

    String driverComment;
    String riderComment;

    LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
    }

}

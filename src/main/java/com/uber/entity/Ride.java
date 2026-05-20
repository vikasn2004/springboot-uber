package com.uber.entity;

import com.uber.Status;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Entity
@Table(name="rides")
@Getter
@Setter
@FieldDefaults(level= AccessLevel.PRIVATE)
public class Ride {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne()
    Driver driver;

    @ManyToOne()
    User rider;


    @Column(nullable = false)
    String pickupLocation;

    @Column(nullable = false)
    String dropOffLocation;

    @Column(nullable = false)
    LocalDateTime pickupTime;
    @Column(nullable = false)
    String latitudePickUpLocation;
    @Column(nullable = false)
    String longitudePickUpLocation;



    @Column(nullable = false)
    String latitudeDropOfLocation;
    @Column(nullable = false)
    String longitudePDropOfLocation;


    @Enumerated(EnumType.STRING)
    Status status= Status.REQUESTED;

    @Column(nullable = false)
    Double distance;
    @Column(nullable = false)
    Double duration;
    @Column(nullable = false)
    Double fare;

    LocalDateTime createdTime;

    @PrePersist
    public void prePersist() {
        createdTime = LocalDateTime.now();
    }

    @Column(nullable = false)
    LocalDateTime dropOfTime;
}

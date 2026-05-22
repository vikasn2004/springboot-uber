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
    @JoinColumn(name="driver_id")
    Driver driver;

    @ManyToOne()
    @JoinColumn(name="user_id")
    User rider;


    @Column(nullable = false)
    String pickupLocation;

    @Column(nullable = false)
    String dropOffLocation;


    @Column(nullable = false)
    String latitudePickUpLocation;
    @Column(nullable = false)
    String longitudePickUpLocation;
    @Column(nullable = false)
    LocalDateTime pickupTime;



    @Column(nullable = false)
    String latitudeDropOffLocation;
    @Column(nullable = false)
    String longitudePDropOffLocation;
    @Column(nullable = false)
    LocalDateTime dropOfTime;


    @Enumerated(EnumType.STRING)
    Status status= Status.REQUESTED;

    Double distance;
    Double duration;
    Double fare;

    LocalDateTime createdTime;

    @PrePersist
    public void prePersist() {
        createdTime = LocalDateTime.now();
    }
  @OneToOne(mappedBy = "ride")
    RideRating rideRating;

}

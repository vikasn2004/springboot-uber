package com.uber.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.uber.Roles;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name="drivers")
@Getter
@Setter
@FieldDefaults(level= AccessLevel.PRIVATE)
public class Driver {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false)
    String name;

    @Column(nullable = false, unique = true)
    String email;

    @Column(nullable = false)
    String password;


    @Column(nullable = false, unique = true)
    String phoneNumber;

    @Column(nullable = false)
    String vehicleName;

    @Column(nullable = false,unique = true)
    String vehicleNumber;

    @Column(nullable = false,unique = true)
    String dlNumber;

    @Column(nullable = false)
    LocalDate dlExpiryDate;

    boolean isAvailable=true;

    LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    @OneToMany(mappedBy = "driver")
    List<Ride>  rides;

    @Enumerated(EnumType.STRING)
    Roles role= Roles.DRIVER;


}

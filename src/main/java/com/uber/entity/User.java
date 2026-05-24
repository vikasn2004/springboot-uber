package com.uber.entity;

import com.uber.Roles;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;


import java.time.LocalDateTime;
import java.util.List;


@Entity
@Table(name="users")
@FieldDefaults(level= AccessLevel.PRIVATE)
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
     Long id;

    @Column(nullable = false, unique = true)
    String name;

    @Column(nullable = false)
    String password;

    @Column(nullable = false, unique = true)
     String email;

    @Column(nullable = false, unique = true)
     String phoneNumber;

    LocalDateTime createdAt;
    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    @OneToMany(mappedBy = "rider")
    List<Ride> allRides;

    @Enumerated(EnumType.STRING)
    Roles role=Roles.USER;
}

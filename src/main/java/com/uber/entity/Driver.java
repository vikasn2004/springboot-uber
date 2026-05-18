package com.uber.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name="driver")
@Getter
@Setter
@FieldDefaults(level= AccessLevel.PRIVATE)
public class Driver {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="id")
    Long id;

    @NotBlank
    @Column(nullable = false)
    String driverName;

    @NotBlank
    @Column(nullable = false, unique = true)
    @Email(message = "ivalid email format")
    String email;

    @NotBlank
    @Column(nullable = false)
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&]).{8,}$",
            message="cannot be less than 8 and must have atleast A-Z or a-z and special characters"
    )
    String password;

    @NotBlank
    @Column(nullable = false, unique = true)
    @Pattern(regexp = "^\\d{10}$", message = "Phone number must be 10 digits")
    String phoneNumber;

    @NotBlank
    @Column(nullable = false)
    String vehicleName;

    @NotBlank
    @Column(nullable = false)
    String vehicleNumber;

    @NotBlank
    @Column(nullable = false,unique = true)
    String DL_NUMBER;

    @NotNull
    @Column(nullable = false)
    LocalDate DL_EXPIRY_DATE;

    LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}

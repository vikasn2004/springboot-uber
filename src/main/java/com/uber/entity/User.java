package com.uber.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDateTime;


@Entity
@Table(name="users")
@FieldDefaults(level= AccessLevel.PRIVATE)
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private int id;

    @NotBlank
    @Column(nullable = false, unique = true)
     String username;

    @NotBlank
    @Column(nullable = false)
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&]).{8,}$",
            message = "Password must be 8+ chars, include uppercase, lowercase, number, and special character"
    )
     String password;

    @NotBlank
    @Column(nullable = false, unique = true)
    @Email(message = "invalid email!")
     String email;

    @NotBlank
    @Column(nullable = false, unique = true)
    @Pattern(regexp = "^([0-9]){10}$",message = "phone numeber must be 10 digit")
     String phoneNumber;

    LocalDateTime createdAt;
    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

}

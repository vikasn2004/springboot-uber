package com.uber.DTO;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class GetDriverDetails {
    Long id;

    String name;

    String email;

    String phoneNumber;

    String vehicleName;

    String vehicleNumber;

    String dlNumber;

    LocalDate dlExpiryDate;

    boolean isAvailable;

    LocalDateTime createdAt;

}

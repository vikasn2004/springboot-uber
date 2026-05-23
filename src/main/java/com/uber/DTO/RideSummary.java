package com.uber.DTO;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RideSummary {
    private Long id;
    private String pickupLocation;
    private String dropOffLocation;
    private double fare;
    private double distance;
    private LocalDateTime pickupTime;
    private LocalDateTime dropOffTime;
}
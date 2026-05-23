package com.uber.DTO;

import com.uber.Status;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EndRideDTO {
    private Long Id;
    private String pickupLocation;
    private String dropOffLocation;
    private double fare;
    private double distance;
    private double duration;
    private LocalDateTime pickupTime;
    private LocalDateTime dropOffTime;
    private Status status;
}
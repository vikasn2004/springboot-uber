package com.uber.DTO;

import com.uber.Status;
import lombok.Data;

@Data
public class AcceptRideResponseDTO {
    private Long rideId;
    private Long customerId;
    private String customerName;
    private String pickupLocation;
    private String dropOffLocation;
    private double fare;
    private Status status;
}

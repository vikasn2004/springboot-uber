package com.uber.DTO;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@JsonPropertyOrder({"rideId", "pickupLocation", "dropOffLocation",
        "pickupLatitude", "pickupLongitude",
        "dropOffLatitude", "dropOffLongitude",
        "distance", "fare", "status", "createdTime"})
public class RideRequestResponseDTO {
    private Long rideId;

    private String pickupLocation;
    private String dropOffLocation;

    private Double pickupLatitude;
    private Double pickupLongitude;

    private Double dropOffLatitude;
    private Double dropOffLongitude;

    private Double distance;
    private Double fare;

    private String status;

    private LocalDateTime createdTime;
}

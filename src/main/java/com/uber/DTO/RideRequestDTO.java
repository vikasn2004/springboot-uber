package com.uber.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RideRequestDTO {

    @NotBlank(message = "Pickup location is required")
    private String pickupLocation;

    @NotBlank(message = "Drop off location is required")
    private String dropOffLocation;

    @NotNull(message = "Pickup latitude is required")
    private Double pickupLatitude;

    @NotNull(message = "Pickup longitude is required")
    private Double pickupLongitude;

    @NotNull(message = "Drop off latitude is required")
    private Double dropOffLatitude;

    @NotNull(message = "Drop off longitude is required")
    private Double dropOffLongitude;


}
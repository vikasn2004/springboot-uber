package com.uber.DTO;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

@Data
@JsonPropertyOrder({"id","pickupLocation","dropOffLocation","fare","distance"})
public class AllPendingRidesDTO {
    private Long id;
    private String pickupLocation;
    private String dropOffLocation;
    private double fare;
    private double distance;
}

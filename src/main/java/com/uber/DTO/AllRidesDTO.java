package com.uber.DTO;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@JsonPropertyOrder({"id","pickupLocation","dropOffLocation","fare","distance","duration","dropOffTime","pickupTime"})
public class AllRidesDTO {
    private Long id;
    private String pickupLocation;
    private String dropOffLocation;
    private double fare;
    private double distance;
    private double duration;
    private LocalDateTime dropOffTime;
    private  LocalDateTime pickupTime;
}

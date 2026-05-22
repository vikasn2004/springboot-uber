package com.uber.DTO;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@JsonPropertyOrder({
        "distance", "fare", "Time"})
public class RideFareDTO {
    private Double distance;
    private Double fare;
    private LocalDateTime Time;
}

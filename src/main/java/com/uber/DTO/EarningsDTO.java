package com.uber.DTO;

import com.uber.entity.Ride;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class EarningsDTO {
    private Long periodDays;
    private Double totalEarnings;
    private int totalRides;
    private List<RideSummary> rides;
}

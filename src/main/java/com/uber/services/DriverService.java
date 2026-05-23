package com.uber.services;

import com.uber.DTO.AcceptRideResponseDTO;
import com.uber.DTO.AllPendingRidesDTO;
import com.uber.DTO.AllRidesDTO;
import com.uber.DTO.EndRideDTO;

import java.util.List;

public interface DriverService {
     List<AllPendingRidesDTO> getAllPendingRides();

     AcceptRideResponseDTO rideAccept(Long rideId);

     String startedRide(Long rideId);

     EndRideDTO endRide(Long rideId);

    String cancelRide(Long rideId);

     List<AllRidesDTO> getAllRides();
}

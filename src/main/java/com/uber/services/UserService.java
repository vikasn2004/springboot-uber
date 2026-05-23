package com.uber.services;

import com.uber.DTO.AllRidesDTO;
import com.uber.DTO.RideFareDTO;
import com.uber.DTO.RideRequestDTO;
import com.uber.DTO.RideRequestResponseDTO;
import org.jspecify.annotations.Nullable;

import java.util.List;

public interface UserService {
    RideRequestResponseDTO rideRequest(RideRequestDTO rideRequest);

    RideFareDTO getRideFare(double pickupLatitude, double pickupLongitude, double dropOffLatitude, double dropOffLongitude);

     String cancelRide(Long rideId);

    List<AllRidesDTO> getallRides(Long userId);
}

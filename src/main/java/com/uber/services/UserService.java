package com.uber.services;

import com.uber.DTO.RideFareDTO;
import com.uber.DTO.RideRequestDTO;
import com.uber.DTO.RideRequestResponseDTO;

public interface UserService {
    RideRequestResponseDTO rideRequest(RideRequestDTO rideRequest);

    RideFareDTO getRideFare(double pickupLatitude, double pickupLongitude, double dropOffLatitude, double dropOffLongitude);
}

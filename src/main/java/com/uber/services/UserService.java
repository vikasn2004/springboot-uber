package com.uber.services;

import com.uber.DTO.*;

import java.util.List;

public interface UserService {
    RideRequestResponseDTO rideRequest(RideRequestDTO rideRequest);

    RideFareDTO getRideFare(double pickupLatitude, double pickupLongitude, double dropOffLatitude, double dropOffLongitude);

     String cancelRide(Long rideId);

    List<AllRidesDTO> getallRides(Long userId);

     String giveDriverRating(Long rideId, RatingDTO ratingDTO);
}

package com.uber.services;

import com.uber.DTO.*;

import java.util.List;

public interface DriverService {
     List<AllPendingRidesDTO> getAllPendingRides();

     AcceptRideResponseDTO rideAccept(Long rideId);

     String startedRide(Long rideId);

     EndRideDTO endRide(Long rideId);

    String cancelRide(Long rideId);

     List<AllRidesDTO> getAllRides();

    EarningsDTO getEarnings(Long days);

    String giveRatingForRider(Long rideId,RatingDTO ratingDTO);
}

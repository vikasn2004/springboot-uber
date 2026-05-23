package com.uber.services;

import com.uber.DTO.AllRidesDTO;
import com.uber.DTO.RideFareDTO;
import com.uber.DTO.RideRequestDTO;
import com.uber.DTO.RideRequestResponseDTO;
import com.uber.Status;
import com.uber.entity.Ride;
import com.uber.entity.User;
import com.uber.repository.RideRepo;
import com.uber.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final ModelMapper modelMapper;
    private final RideRepo rideRepo;
    private final UserRepo userRepo;

    @Value("${earth.radius}")
    Double earthRadius;

    @Override
    public RideRequestResponseDTO rideRequest(RideRequestDTO rideRequest) {
        Ride ride = modelMapper.map(rideRequest, Ride.class);
        String email= SecurityContextHolder.getContext().getAuthentication().getName();
        User user=userRepo.findByEmail(email).orElseThrow(()->new RuntimeException("user not found"));
        double lat1Rad = Math.toRadians(ride.getPickupLatitude());
        double lat2Rad = Math.toRadians(ride.getDropOffLatitude());
        double deltaLat = Math.toRadians(ride.getDropOffLatitude() - ride.getPickupLatitude());
        double deltaLon = Math.toRadians(ride.getDropOffLongitude() - ride.getPickupLongitude());


        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2) +
                Math.cos(lat1Rad) * Math.cos(lat2Rad) *
                        Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = earthRadius * c;
        ride.setDistance(distance);
        ride.setFare(50 + distance * 10);
        ride.setRider(user);
        ride.setStatus(Status.REQUESTED);
        Ride currRide = rideRepo.save(ride);
        RideRequestResponseDTO responseDTO = modelMapper.map(ride, RideRequestResponseDTO.class);
        responseDTO.setRideId(currRide.getId());
        return responseDTO;
    }

    @Override
    public RideFareDTO getRideFare(double pickupLatitude, double pickupLongitude,
                                   double dropOffLatitude, double dropOffLongitude) {

        double lat1Rad = Math.toRadians(pickupLatitude);
        double lat2Rad = Math.toRadians(dropOffLatitude);
        double deltaLat = Math.toRadians(dropOffLatitude - pickupLatitude);
        double deltaLon = Math.toRadians(dropOffLongitude - pickupLongitude);

        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2) +
                Math.cos(lat1Rad) * Math.cos(lat2Rad) *
                        Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = earthRadius * c;
        double fare = 50 + distance * 10;
        RideFareDTO rideFareDTO = new RideFareDTO();
        rideFareDTO.setFare(fare);
        rideFareDTO.setDistance(distance);
        rideFareDTO.setTime(LocalDateTime.now());
        return rideFareDTO;
    }

    @Override
    public String cancelRide(Long rideId) {
        Ride ride=rideRepo.findById(rideId).orElseThrow(()->new RuntimeException("ride not found"));
        ride.setStatus(Status.CANCELLED);
        rideRepo.save(ride);
        return "ride cancelled";
    }

    @Override
    public List<AllRidesDTO> getallRides(Long userId) {
        User user=userRepo.findById(userId).orElseThrow(()->new RuntimeException("user not found"));
        List<AllRidesDTO> allRidesDTOS = rideRepo.findByRider(user)
                .stream()
                .map(ride -> modelMapper.map(ride, AllRidesDTO.class))
                .collect(Collectors.toList());
        return allRidesDTOS;
    }

}

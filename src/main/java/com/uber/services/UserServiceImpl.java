package com.uber.services;

import com.uber.DTO.*;
import com.uber.Status;
import com.uber.entity.Ride;
import com.uber.entity.RideRating;
import com.uber.entity.User;
import com.uber.exceptions.*;
import com.uber.kafka.RideCancelledProducer;
import com.uber.kafka.RideRequestProducer;
import com.uber.repository.RideRatingRepo;
import com.uber.repository.RideRepo;
import com.uber.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final ModelMapper modelMapper;
    private final RideRepo rideRepo;
    private final UserRepo userRepo;
    private final RideRatingRepo rideRatingRepo;
    private final RideRequestProducer rideRequestProducer;
    private final RideCancelledProducer rideCancelledProducer;


    @Value("${earth.radius}")
    Double earthRadius;

    @Override
    public RideRequestResponseDTO rideRequest(RideRequestDTO rideRequest) {
        Ride ride = modelMapper.map(rideRequest, Ride.class);
        String email= SecurityContextHolder.getContext().getAuthentication().getName();
        User user=userRepo.findByEmail(email).orElseThrow(()->new UserNotFoundException("user not found"));
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
        rideRequestProducer.sendRideRequest("RIDE REQUEST POSTED WITH "+currRide.getId());
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
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Ride ride=rideRepo.findById(rideId).orElseThrow(()->new RideUnavailableException("ride not found"));
        if(!ride.getRider().getEmail().equals(email)) {
            throw new BadCredentialsException("Unauthorized user");
        }
        ride.setStatus(Status.CANCELLED);
        rideRepo.save(ride);
        rideCancelledProducer.sendRideCancelled("RIDE CANCELLED WITH rideId " + rideId);
        return "ride cancelled";
    }

    @Override
    public List<AllRidesDTO> getallRides(Long userId) {
        User user=userRepo.findById(userId).orElseThrow(()->new UserNotFoundException("user not found"));
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        if(!user.getEmail().equals(email)) {
           throw new BadCredentialsException("Unauthorized user");
        }
        List<AllRidesDTO> allRidesDTOS = rideRepo.findByRider(user)
                .stream()
                .map(ride -> modelMapper.map(ride, AllRidesDTO.class))
                .collect(Collectors.toList());
        return allRidesDTOS;
    }

    @Override
    public String giveDriverRating(Long rideId, RatingDTO ratingDTO) {
        String email=SecurityContextHolder.getContext().getAuthentication().getName();
        User user=userRepo.findByEmail(email).orElseThrow(()->new UserNotFoundException("user not found"));
        Ride ride=rideRepo.findById(rideId).orElseThrow(()->new RideUnavailableException("ride not found"));
        Integer riderRating=ratingDTO.getRating();
        String riderComment=ratingDTO.getComment();
        if (ride.getStatus() != Status.COMPLETED) {
            throw new RideUnavailableException("rider can only rate a completed ride");
        }

        if (!ride.getRider().getEmail().equals(email)) {
            throw new UserNotFoundException("You can only rate your own ride");
        }

        if(riderRating<1 || riderRating>5) {
            throw new InvalidRateException("Invalid user rating must be between 1 and 5");
        }
        RideRating rideRating = ride.getRideRating() != null
                ? ride.getRideRating()
                : new RideRating();

        if (rideRating.getRiderRating()!= null) {
            throw new RatingAlreadyExistsException("Rate already exists");
        }
        rideRating.setRide(ride);
        rideRating.setRider(user);
        rideRating.setRiderRating(riderRating);
        rideRating.setRiderComment(riderComment);
        rideRatingRepo.save(rideRating);

        return "THANK YOU FOR YOUR VALUABLE FEEDBACK";
    }

}

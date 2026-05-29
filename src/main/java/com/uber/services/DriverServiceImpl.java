package com.uber.services;

import com.uber.DTO.*;
import com.uber.Status;
import com.uber.entity.Driver;
import com.uber.entity.Ride;
import com.uber.entity.RideRating;
import com.uber.exceptions.*;
import com.uber.kafka.RideAcceptProducer;
import com.uber.kafka.RideCancelledProducer;
import com.uber.kafka.RideCompletedProducer;
import com.uber.repository.DriverRepo;
import com.uber.repository.RideRatingRepo;
import com.uber.repository.RideRepo;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DriverServiceImpl implements DriverService {
    private final RideRepo rideRepo;
    private final ModelMapper modelMapper;
    private final DriverRepo driverRepo;
    private final RideRatingRepo rideRatingRepo;
    private final RideAcceptProducer rideAcceptProducer;
    private final RideCompletedProducer rideCompletedProducer;
    private final RideAcceptProducer rideAcceptedProducer;
    private final RideCancelledProducer rideCancelledProducer;


    @Override
    public List<AllPendingRidesDTO> getAllPendingRides() {
        return rideRepo.findByStatus(Status.REQUESTED)
                .stream()
                .map(ride -> modelMapper.map(ride, AllPendingRidesDTO.class))
                .toList();

    }

    @Transactional
    @Override
    public AcceptRideResponseDTO rideAccept(Long rideId) {
        Ride ride = rideRepo.findById(rideId).orElseThrow(() -> new RideUnavailableException("Ride not found"));
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Driver driver = driverRepo.findByEmail(email).orElseThrow(() -> new DriverNotFoundException("Driver not found"));
        if(!driver.isAvailable())
            throw new DriverUnavailableException("Driver you have already accpeted a ride");
        if (ride.getStatus() != Status.REQUESTED ) {
            throw new RideUnavailableException("Ride not requested or Ride was cancelled");
        }
        ride.setStatus(Status.ACCEPTED);
        ride.setDriver(driver);
        rideRepo.save(ride);
        driver.setAvailable(false);
        AcceptRideResponseDTO responseDTO = new AcceptRideResponseDTO();
        responseDTO.setRideId(rideId);
        responseDTO.setCustomerId(ride.getRider().getId());
        responseDTO.setCustomerName(ride.getRider().getName());
        responseDTO.setPickupLocation(ride.getPickupLocation());
        responseDTO.setDropOffLocation(ride.getDropOffLocation());
        responseDTO.setFare(ride.getFare());
        responseDTO.setStatus(ride.getStatus());
        rideAcceptedProducer.rideAccept("RIDE ACCEPTED WITH rideId " + rideId);
        return responseDTO;
    }
    @Override
    public String cancelRide(Long rideId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Ride ride = rideRepo.findById(rideId).orElseThrow(() -> new RideUnavailableException("Ride not found"));
        if(ride.getStatus() != Status.ACCEPTED) {
            throw new RideUnavailableException("Ride not accepted");
        }
        Driver driver=driverRepo.findByEmail(email).orElseThrow(() -> new DriverNotFoundException("Driver not found"));
        if(!ride.getDriver().getEmail().equals(driver.getEmail())) {  // ADD THIS
            throw new DriverUnavailableException("You can only cancel your own ride");
        }
        driver.setAvailable(true);
        ride.setStatus(Status.REQUESTED);
        ride.setDriver(null);
        rideRepo.save(ride);
        rideCancelledProducer.sendRideCancelled("THE RIDE WAS CANCELLED WITH rideId " + rideId);
        return "Ride cancelled,back in queue";
    }

    @Override
    public List<AllRidesDTO> getAllRides() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Driver driver = driverRepo.findByEmail(email)
                .orElseThrow(() -> new DriverNotFoundException("Driver not found"));

        return rideRepo.findByDriver(driver)
                .stream()
                .map(ride -> modelMapper.map(ride, AllRidesDTO.class))
                .toList();
    }

    @Override
    public String startedRide(Long rideId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Ride ride = rideRepo.findById(rideId).orElseThrow(() -> new RideUnavailableException("Ride not found"));
        if(ride.getStatus() != Status.ACCEPTED) {
            throw new RideUnavailableException("Ride not accepted");
        }
        Driver driver = driverRepo.findByEmail(email).orElseThrow(() -> new DriverNotFoundException("Driver not found"));  // ADD
        if(!ride.getDriver().getEmail().equals(driver.getEmail())) {
            throw new DriverUnavailableException("You can only start your own ride");
        }
        ride.setStatus(Status.ONGOING);
        ride.setPickupTime(LocalDateTime.now());
        rideRepo.save(ride);
        return "ride started";
    }

    @Override
    public EndRideDTO endRide(Long rideId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Ride ride = rideRepo.findById(rideId).orElseThrow(() -> new RideUnavailableException("Ride not found"));
        if(ride.getStatus() != Status.ONGOING) {
            throw new RideUnavailableException("Ride is not ongoing");
        }
        Driver driver = driverRepo.findByEmail(email).orElseThrow(() -> new DriverNotFoundException("Driver not found"));  // ADD
        if(!ride.getDriver().getEmail().equals(driver.getEmail())) {
            throw new DriverUnavailableException("You can only end your own ride");
        }
        ride.setDropOffTime(LocalDateTime.now());
        Double durationMinutes = (double) Duration.between(ride.getPickupTime(), ride.getDropOffTime()).toMinutes();
        ride.setDuration(durationMinutes);
        ride.setStatus(Status.COMPLETED);
        ride.getDriver().setAvailable(true);
        Ride savedRide = rideRepo.save(ride);
        rideCompletedProducer.sendRideCompleted("THE RIDE WAS COMPLETED WITH rideId " + rideId);
        return modelMapper.map(savedRide, EndRideDTO.class);
    }

    @Override
    public EarningsDTO getEarnings(Long days) {
      String email = SecurityContextHolder.getContext().getAuthentication().getName();
      Driver driver=driverRepo.findByEmail(email).orElseThrow(() -> new DriverNotFoundException("Driver not found"));
      LocalDateTime startDate = LocalDateTime.now().minusDays(days);
      List<Ride> rides=rideRepo.findCompletedRidesAfter(driver.getId(), startDate,Status.COMPLETED);

      double totalEarnings=rides.stream().mapToDouble(Ride::getFare).sum();
        List<RideSummary> rideSummaries = rides.stream()
                .map(ride -> modelMapper.map(ride, RideSummary.class))
                .toList();
      return new EarningsDTO(
              days,
              totalEarnings,
              rides.size(),
              rideSummaries
      );
    }

    @Override
    public String giveRatingForRider(Long rideId,RatingDTO ratingDTO) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Driver driver=driverRepo.findByEmail(email).orElseThrow(() -> new DriverNotFoundException("Driver not found"));
        Ride ride=rideRepo.findById(rideId).orElseThrow(() -> new RideUnavailableException("Ride not found"));
        Integer driverRating=ratingDTO.getRating();
        String driverComment=ratingDTO.getComment();
        if(ride.getStatus() != Status.COMPLETED) {
            throw new RideUnavailableException("Ride not completed");
        }
        if(!ride.getDriver().getEmail().equals(driver.getEmail())) {
            throw new DriverNotFoundException("Driver you can rate only your ride");
        }
        if (driverRating < 1 || driverRating > 5) {
            throw new InvalidRateException("Rating must be between 1 and 5");
        }

        RideRating rideRating = ride.getRideRating() != null
                ? ride.getRideRating()
                : new RideRating();
        if(rideRating.getDriverRating()!=null)
            throw new RatingAlreadyExistsException("rating is already done");

        rideRating.setRide(ride);
        rideRating.setDriver(ride.getDriver());
        rideRating.setDriverRating(driverRating);
        rideRating.setDriverComment(driverComment);
        rideRatingRepo.save(rideRating);

        return "THANK YOU FOR YOUR VALUABLE FEEDBACK";
    }

}


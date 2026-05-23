package com.uber.services;

import com.uber.DTO.AcceptRideResponseDTO;
import com.uber.DTO.AllPendingRidesDTO;
import com.uber.DTO.AllRidesDTO;
import com.uber.DTO.EndRideDTO;
import com.uber.Status;
import com.uber.entity.Driver;
import com.uber.entity.Ride;
import com.uber.repository.DriverRepo;
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
        Ride ride = rideRepo.findById(rideId).orElseThrow(() -> new RuntimeException("Ride not found"));
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Driver driver = driverRepo.findByEmail(email).orElseThrow(() -> new RuntimeException("Driver not found"));
        if(!driver.isAvailable())
            throw new RuntimeException("Driver is not available");
        if (ride.getStatus() != Status.REQUESTED ) {
            throw new RuntimeException("Ride not requested ");
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
        return responseDTO;
    }
    @Override
    public String cancelRide(Long rideId) {
        Ride ride = rideRepo.findById(rideId).orElseThrow(() -> new RuntimeException("Ride not found"));
        if(ride.getStatus() != Status.ACCEPTED) {
            throw new RuntimeException("Ride not accepted");
        }
        Driver driver=ride.getDriver();
        driver.setAvailable(true);
        ride.setStatus(Status.REQUESTED);
        ride.setDriver(null);
        rideRepo.save(ride);
        return "Ride cancelled,back in queue";
    }

    @Override
    public List<AllRidesDTO> getAllRides() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Driver driver = driverRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Driver not found"));

        return rideRepo.findByDriver(driver)
                .stream()
                .map(ride -> modelMapper.map(ride, AllRidesDTO.class))
                .toList();
    }

    @Override
    public String startedRide(Long rideId) {
        Ride ride = rideRepo.findById(rideId).orElseThrow(() -> new RuntimeException("Ride not found"));
        if (ride.getStatus() != Status.ACCEPTED) {
            throw new RuntimeException("Ride not accepted");
        }
        ride.setStatus(Status.ONGOING);
        ride.setPickupTime(LocalDateTime.now());
        rideRepo.save(ride);
        return "ride started";
    }

    @Override
    public EndRideDTO endRide(Long rideId) {
        Ride ride = rideRepo.findById(rideId)
                .orElseThrow(() -> new RuntimeException("Ride not found"));

        if (ride.getStatus() != Status.ONGOING) {
            throw new RuntimeException("Ride is not ongoing");
        }

        ride.setDropOffTime(LocalDateTime.now());
        Double durationMinutes =(double) Duration.between(ride.getPickupTime(), ride.getDropOffTime()).toMinutes();
        ride.setDuration(durationMinutes);
        ride.setStatus(Status.COMPLETED);
        ride.getDriver().setAvailable(true);
        Ride savedRide = rideRepo.save(ride);

        return modelMapper.map(savedRide, EndRideDTO.class);
    }


}


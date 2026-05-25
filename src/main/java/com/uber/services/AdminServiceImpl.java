package com.uber.services;

import com.uber.DTO.*;
import com.uber.Status;
import com.uber.entity.Driver;
import com.uber.entity.Ride;
import com.uber.entity.User;
import com.uber.exceptions.DriverNotFoundException;
import com.uber.repository.DriverRepo;
import com.uber.repository.RideRepo;
import com.uber.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
    private final UserRepo userRepo;
    private final DriverRepo driverRepo;
    private final ModelMapper modelMapper;
    private final RideRepo rideRepo;

    @Override
    public List<GetAllUsers> getAllUsers() {
        return userRepo.findAll()
                .stream()
                .map(u -> modelMapper.map(u, GetAllUsers.class))
                .collect(Collectors.toList());
    }

    @Override
    public  List<GetAllDrivers> getAllDrivers() {
        return driverRepo.findAll()
                .stream().map(d-> modelMapper.map(d, GetAllDrivers.class)).collect(Collectors.toList());
    }

    @Override
    public GetDriverDetails getDriverDetails(Long driverId) {
        Driver driver=driverRepo.findById(driverId).orElseThrow(()->new DriverNotFoundException("Driver not found"));
        return modelMapper.map(driver, GetDriverDetails.class);
    }

    @Override
    public EarningsDTO getEarnings(Long driverId,Long period) {
        Driver driver=driverRepo.findById(driverId).orElseThrow(()->new DriverNotFoundException("Driver not found"));
        LocalDateTime startDate = LocalDateTime.now().minusDays(period);
        List<Ride> rides=rideRepo.findCompletedRidesAfter(driverId,startDate, Status.COMPLETED);
        Double totalEarnings=rides.stream().mapToDouble(Ride::getFare).sum();
        List<RideSummary> rideSummaries = rides.stream()
                .map(ride -> modelMapper.map(ride, RideSummary.class))
                .toList();
        return new EarningsDTO(
                period,
                totalEarnings,
                rides.size(),
                rideSummaries
        );
    }
}

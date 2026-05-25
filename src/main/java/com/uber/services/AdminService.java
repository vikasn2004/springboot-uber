package com.uber.services;

import com.uber.DTO.EarningsDTO;
import com.uber.DTO.GetAllDrivers;
import com.uber.DTO.GetAllUsers;
import com.uber.DTO.GetDriverDetails;

import java.util.List;

public interface AdminService {
    List<GetAllUsers> getAllUsers();

     List<GetAllDrivers> getAllDrivers();

  GetDriverDetails getDriverDetails(Long driverId);

    EarningsDTO getEarnings(Long driverId, Long period);
}

package com.uber.repository;

import com.uber.DTO.AllRidesDTO;
import com.uber.Status;
import com.uber.entity.Driver;
import com.uber.entity.Ride;
import com.uber.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RideRepo extends JpaRepository<Ride, Long> {

    List<Ride> findByRider(User user);
    List<Ride> findByStatus(Status status);
    List<Ride> findByDriver(Driver driver);

}

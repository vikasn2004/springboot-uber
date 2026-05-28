package com.uber;

import com.uber.DTO.*;
import com.uber.entity.Driver;
import com.uber.entity.Ride;
import com.uber.entity.User;
import com.uber.exceptions.DriverNotFoundException;
import com.uber.exceptions.DriverUnavailableException;
import com.uber.exceptions.RideUnavailableException;
import com.uber.kafka.RideAcceptProducer;
import com.uber.kafka.RideCancelledProducer;
import com.uber.kafka.RideCompletedProducer;
import com.uber.repository.DriverRepo;
import com.uber.repository.RideRatingRepo;
import com.uber.repository.RideRepo;
import com.uber.repository.UserRepo;
import com.uber.services.DriverServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DriverServiceTests {
    @Mock
    DriverRepo driverRepo;
    @InjectMocks
    DriverServiceImpl driverService;
    @Mock
    RideRepo rideRepo;
    @Mock
    UserRepo userRepo;
    @Mock
    RideRatingRepo rideRatingRepo;
    @Mock
    ModelMapper modelMapper;
    Driver driver;
    Ride ride;
    User user;

    @Mock RideAcceptProducer rideAcceptProducer;
    @Mock RideCompletedProducer rideCompletedProducer;
    @Mock RideCancelledProducer rideCancelledProducer;

    private static final String email="exampledriver@gmail.com";
    private static final Long rideId=1L;
    private static final Long driverId=1L;
    @BeforeEach
    public void setup() {
        driver = new Driver();
        ride = new Ride();
        driver.setId(driverId);
        driver.setEmail(email);
        driver.setAvailable(true);
        ride.setId(1L);
        ride.setDriver(driver);
    }
    public void getAuth(String email){
        Authentication authentication= Mockito.mock(Authentication.class);
        when(authentication.getName()).thenReturn(email);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    public void getAllRides_available(){
        AllPendingRidesDTO allPendingRidesDTO = new AllPendingRidesDTO();
        when(rideRepo.findByStatus(Status.REQUESTED)).thenReturn(List.of(ride));
        when(modelMapper.map(any(), eq(AllPendingRidesDTO.class))).thenReturn(allPendingRidesDTO);

        List<AllPendingRidesDTO> result = driverService.getAllPendingRides();

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.size());
    }

    @Test
    public void accept_Ride(){
        user = new User();
        user.setEmail("test@gmail.com");
        user.setId(1L);
        ride.setStatus(Status.REQUESTED);
        ride.setRider(user);
        ride.setFare(0.0);
        getAuth(email);
        when(rideRepo.findById(rideId)).thenReturn(Optional.of(ride));
        when(driverRepo.findByEmail(email)).thenReturn(Optional.of(driver));
        AcceptRideResponseDTO dto=driverService.rideAccept(rideId);
        assertThat(dto).isNotNull();
        assertThat(dto.getStatus()).isEqualTo(Status.ACCEPTED);
        assertThat(ride.getFare()).isEqualTo(0.0);
    }
    @Test
    public void accpetRide_driverNotFound(){
        user = new User();
        user.setId(1L);
        ride.setStatus(Status.REQUESTED);
        ride.setRider(user);
        ride.setFare(0.0);
        getAuth(email);
        when(rideRepo.findById(rideId)).thenReturn(Optional.of(ride));
        when(driverRepo.findByEmail(email)).thenReturn(Optional.empty());
        assertThatThrownBy(()->driverService.rideAccept(rideId)).isInstanceOf(DriverNotFoundException.class).hasMessage("Driver not found");
    }
    @Test
    public void accept_Ride_Driver_unavailable(){
        user = new User();
        user.setId(1L);
        ride.setStatus(Status.REQUESTED);
        ride.setRider(user);
        ride.setFare(0.0);
        getAuth(email);
        driver.setAvailable(false);
        ride.setDriver(driver);
        when(rideRepo.findById(rideId)).thenReturn(Optional.of(ride));
        when(driverRepo.findByEmail(email)).thenReturn(Optional.of(driver));
        assertThatThrownBy(()->driverService.rideAccept(rideId)).isInstanceOf(DriverUnavailableException.class).hasMessage("Driver you have already accpeted a ride");
    }
    @Test
    public void accept_Ride_NotRequested(){
        user = new User();
        user.setId(1L);
        ride.setStatus(Status.CANCELLED);
        ride.setRider(user);
        getAuth(email);
        when(rideRepo.findById(rideId)).thenReturn(Optional.of(ride));
        when(driverRepo.findByEmail(email)).thenReturn(Optional.of(driver));
        assertThatThrownBy(()->driverService.rideAccept(rideId))
                .isInstanceOf(RideUnavailableException.class).hasMessage("Ride not requested or Ride was cancelled");
    }
    @Test
    public void accept_Ride_Cancelled(){
        getAuth(email);
        user = new User();
        user.setId(1L);
        ride.setStatus(Status.ACCEPTED);
        ride.setRider(user);
        ride.setDriver(driver);
        when(rideRepo.findById(rideId)).thenReturn(Optional.of(ride));
        when(driverRepo.findByEmail(email)).thenReturn(Optional.of(driver));
        String message=driverService.cancelRide(rideId);
        assertThat(message).isEqualTo( "Ride cancelled,back in queue");
        assertThat(ride.getStatus()).isEqualTo(Status.REQUESTED);
    }
    @Test
    public void getAllRide_history(){
        getAuth(email);
        AllRidesDTO allRidesDTO = new AllRidesDTO();
        when(driverRepo.findByEmail(email)).thenReturn(Optional.of(driver));
        when(rideRepo.findByDriver(driver)).thenReturn(List.of(ride));
        when(modelMapper.map(ride, AllRidesDTO.class)).thenReturn(allRidesDTO);
        List<AllRidesDTO> result = driverService.getAllRides();
        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.size());
    }
    @Test
    public void ride_started(){
      getAuth(email);
      ride.setStatus(Status.ACCEPTED);
      when(rideRepo.findById(rideId)).thenReturn(Optional.of(ride));
      when(driverRepo.findByEmail(email)).thenReturn(Optional.of(driver));
      String message=driverService.startedRide(rideId);
      assertThat(message).isEqualTo("ride started");
      assertThat(ride.getStatus()).isEqualTo(Status.ONGOING);
    }
    @Test
    public void ride_ended(){
        EndRideDTO endRideDTO = new EndRideDTO();
        getAuth(email);
        ride.setStatus(Status.ONGOING);
        ride.setPickupTime(LocalDateTime.now().minusMinutes(10));
        ride.setDriver(driver);
        when(rideRepo.findById(rideId)).thenReturn(Optional.of(ride));
        when(driverRepo.findByEmail(email)).thenReturn(Optional.of(driver));
        when(rideRepo.save(ride)).thenReturn(ride);
        when(modelMapper.map(ride, EndRideDTO.class)).thenReturn(endRideDTO);
        EndRideDTO result = driverService.endRide(rideId);
        assertThat(result).isNotNull();
        assertThat(ride.getStatus()).isEqualTo(Status.COMPLETED);
    }
    @Test
    public void get_Earnings(){
        ride.setFare(100.0);
        ride.setStatus(Status.COMPLETED);
        RideSummary rideSummary = new RideSummary();
        getAuth(email);
        when(driverRepo.findByEmail(email)).thenReturn(Optional.of(driver));
        when(rideRepo.findCompletedRidesAfter(eq(driverId), any(LocalDateTime.class), eq(Status.COMPLETED)))
                .thenReturn(List.of(ride));
        when(modelMapper.map(ride, RideSummary.class)).thenReturn(rideSummary);
        EarningsDTO result = driverService.getEarnings(7L);
        assertThat(result).isNotNull();
        assertThat(result.getTotalEarnings()).isEqualTo(100.0);
        assertThat(result.getTotalRides()).isEqualTo(1);
        assertThat(result.getPeriodDays()).isEqualTo(7);
    }
    @Test
    public void get_Earnings_NoRides(){
        getAuth(email);
        when(driverRepo.findByEmail(email)).thenReturn(Optional.of(driver));
        when(rideRepo.findCompletedRidesAfter(eq(driverId), any(LocalDateTime.class), eq(Status.COMPLETED)))
                .thenReturn(List.of());
        EarningsDTO result = driverService.getEarnings(7L);

        assertThat(result.getTotalEarnings()).isEqualTo(0.0);
        assertThat(result.getTotalRides()).isEqualTo(0);
    }

}

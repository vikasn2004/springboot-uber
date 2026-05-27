package com.uber;

import com.uber.DTO.*;
import com.uber.entity.Ride;
import com.uber.entity.RideRating;
import com.uber.entity.User;
import com.uber.exceptions.InvalidRateException;
import com.uber.exceptions.RatingAlreadyExistsException;
import com.uber.exceptions.RideUnavailableException;
import com.uber.exceptions.UserNotFoundException;
import com.uber.kafka.RideCancelledProducer;
import com.uber.kafka.RideRequestProducer;
import com.uber.repository.RideRatingRepo;
import com.uber.repository.RideRepo;
import com.uber.repository.UserRepo;
import com.uber.services.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.AssertionsForClassTypes.within;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTests {
   @Mock
    UserRepo userRepo;
   @Mock
   RideRepo rideRepo;
   @Mock
    ModelMapper modelMapper;
   @Mock
    RideRequestProducer rideRequestProducer;
   @Mock
   RideCancelledProducer rideCancelledProducer;
   @Mock
   RideRatingRepo rideRatingRepo;
   @InjectMocks
   UserServiceImpl userService;

   private static final String email="example@gmail.com";
   private static final Long rideId=1L;
   private static final Long userId=1L;
   private static final double earthRadius = 6371.0;

   private User testUser;
   private Ride testRide;

   @BeforeEach
   public void setup() {
       ReflectionTestUtils.setField(userService, "earthRadius", 6371.0);
       testUser = new User();
       testUser.setEmail(email);
       testUser.setId(userId);
       testRide = new Ride();
       testRide.setId(rideId);
       testRide.setRider(testUser);
       testRide.setPickupLatitude(12.9716);
       testRide.setPickupLongitude(77.5946);
       testRide.setDropOffLatitude(13.0827);
       testRide.setDropOffLongitude(80.2707);
       testRide.setStatus(Status.REQUESTED);
   }
   private void mockSecurityContext(String email) {
       Authentication authentication = mock(Authentication.class);
       when(authentication.getName()).thenReturn(email);
       SecurityContext securityContext = mock(SecurityContext.class);
       when(securityContext.getAuthentication()).thenReturn(authentication);
       SecurityContextHolder.setContext(securityContext);
   }
   @Test
   @DisplayName("Rider request ride successful")
    public void rideRequest_succcess() {
       RideRequestDTO rideRequestDTO = new RideRequestDTO();
       rideRequestDTO.setPickupLatitude(12.9716);
       rideRequestDTO.setPickupLongitude(77.5946);
       rideRequestDTO.setDropOffLatitude(13.0827);
       rideRequestDTO.setDropOffLongitude(80.2707);
       RideRequestResponseDTO rideRequestResponseDTO = new RideRequestResponseDTO();
       when(modelMapper.map(rideRequestDTO, Ride.class)).thenReturn(testRide);
       mockSecurityContext(email);
       when(userRepo.findByEmail(email)).thenReturn(Optional.of(testUser));
       when(rideRepo.save(testRide)).thenReturn(testRide);
       when(modelMapper.map(testRide, RideRequestResponseDTO.class)).thenReturn(rideRequestResponseDTO);

       RideRequestResponseDTO result = userService.rideRequest(rideRequestDTO);

       assertThat(result).isNotNull();
       assertThat(testRide.getStatus()).isEqualTo(Status.REQUESTED);
       assertThat(testRide.getFare()).isPositive();
       assertThat(testRide.getDistance()).isPositive();
   }
   @Test
   @DisplayName("when user not found")
    public void userNotFound() {
       RideRequestDTO rideRequestDTO = new RideRequestDTO();
       when(modelMapper.map(rideRequestDTO, Ride.class)).thenReturn(testRide);
       mockSecurityContext(email);
       when(userRepo.findByEmail(email)).thenReturn(Optional.empty());
       assertThatThrownBy(() -> userService.rideRequest(rideRequestDTO)).isInstanceOf(UserNotFoundException.class).hasMessage("user not found");
   }
   @Test
   @DisplayName("calculate the fare 50 is base fare")
    public void ride_Fare(){
       double pickupLatitude = 12.9716;
       double pickupLongitude = 77.5946;
       double dropOffLatitude = 13.0827;
       double dropOffLongitude = 80.2707;
       RideFareDTO result = userService.getRideFare(pickupLatitude, pickupLongitude, dropOffLatitude, dropOffLongitude);
       assertThat(result).isNotNull();
       assertThat(result.getFare()).isEqualTo(50 + 10 * result.getDistance(),within(0.01));
       assertThat(result.getDistance()).isEqualTo(290.17, within(0.01));
   }
   @Test
   @DisplayName("calculate fare for distance 0 but base fare is 50")
    public void ride_Fare_zeroDistance(){
       double pickupLatitude = 0;
       double pickupLongitude = 0;
       double dropOffLatitude = 0;
       double dropOffLongitude = 0;
       RideFareDTO result=userService.getRideFare(pickupLatitude, pickupLongitude, dropOffLatitude, dropOffLongitude);
       assertThat(result).isNotNull();
       assertThat(result.getFare()).isEqualTo(50);
       assertThat(result.getDistance()).isEqualTo(0,within(0.01));
    }
    @Test
    @DisplayName("when user cancels ride")
    public void cancel_ride_success() {
       when(rideRepo.findById(rideId)).thenReturn(Optional.of(testRide));
      when(rideRepo.save(testRide)).thenReturn(testRide);
     String result = userService.cancelRide(rideId);
     assertThat(result).isEqualTo("ride cancelled");
     assertThat(testRide.getStatus()).isEqualTo(Status.CANCELLED);

    }
    @Test
    @DisplayName("when ride not found")
    public void ride_NotFound() {
       when(rideRepo.findById(rideId)).thenReturn(Optional.empty());
       assertThatThrownBy(() -> userService.cancelRide(rideId))
               .isInstanceOf(RideUnavailableException.class).hasMessage("ride not found");
       verify(rideRepo, never()).save(any());
    }
    @Test
    @DisplayName("gell all user past rides")
    public void getAll_Rides_success() {
        AllRidesDTO allRidesDTO = new AllRidesDTO();
        when(userRepo.findById(testUser.getId())).thenReturn(Optional.of(testUser));
        when(rideRepo.findByRider(testUser)).thenReturn(List.of(testRide));
        when(modelMapper.map(testRide, AllRidesDTO.class)).thenReturn(allRidesDTO);
        List<AllRidesDTO> allRides=userService.getallRides(testUser.getId());
        assertThat(allRides).size().isEqualTo(1);
        verify(rideRepo, times(1)).findByRider(testUser);
    }
    @Test
    @DisplayName("when no ride exists for user")
    public void getAll_Rides_0() {
       when(userRepo.findById(testUser.getId())).thenReturn(Optional.of(testUser));
       when(rideRepo.findByRider(testUser)).thenReturn(List.of());
       List<AllRidesDTO> allRides = userService.getallRides(testUser.getId());
       assertThat(allRides).isEmpty();
       verify(rideRepo, times(1)).findByRider(testUser);
    }

    public void completedRide() {
       testRide.setStatus(Status.COMPLETED);

    }
    @Test
    @DisplayName("when user gives rating for driver for first time")
    public void give_driver_rating_success() {
        RatingDTO ratingDTO = new RatingDTO();
        ratingDTO.setRating(4);
        ratingDTO.setComment("good");
       mockSecurityContext(email);
       when(userRepo.findByEmail(email)).thenReturn(Optional.of(testUser));
       completedRide();
       when(rideRepo.findById(rideId)).thenReturn(Optional.of(testRide));
       String result=userService.giveDriverRating(rideId,ratingDTO);
       assertThat(result).isEqualTo("THANK YOU FOR YOUR VALUABLE FEEDBACK");
       verify(rideRatingRepo).save(argThat(r->
               r.getRiderRating()==4 &&
                       r.getRiderComment().equals("good") &&
                       r.getRider().getEmail().equals(testUser.getEmail()) &&
                       r.getRide().getId().equals(rideId)));

    }
    @Test
    @DisplayName("when user try to give rating in a mid way")
    public void give_driver_rating_failure_rideNotCompleted() {
        RatingDTO ratingDTO = new RatingDTO();
        ratingDTO.setRating(4);
        ratingDTO.setComment("good");
        mockSecurityContext(email);
        when(userRepo.findByEmail(email)).thenReturn(Optional.of(testUser));
        when(rideRepo.findById(rideId)).thenReturn(Optional.of(testRide));
        assertThatThrownBy(() -> userService.giveDriverRating(rideId,ratingDTO)).
                isInstanceOf(RideUnavailableException.class)
                .hasMessage("rider can only rate a completed ride");
    }
   @Test
   @DisplayName("when a rider gives multiple rating for single ride")
    public void duplicate_rating(){
       completedRide();
       RideRating rideRating = new RideRating();
       rideRating.setRiderRating(1);
       rideRating.setRiderComment("bad");
       testRide.setRideRating(rideRating);
       mockSecurityContext(email);
       RatingDTO ratingDTO = new RatingDTO();
       ratingDTO.setRating(4);
       ratingDTO.setComment("good");
       when(userRepo.findByEmail(email)).thenReturn(Optional.of(testUser));
       when(rideRepo.findById(rideId)).thenReturn(Optional.of(testRide));
       assertThatThrownBy(() -> userService.giveDriverRating(rideId,ratingDTO))
               .isInstanceOf(RatingAlreadyExistsException.class).hasMessage("Rate already exists");

   }
   @Test
   @DisplayName("when another rider rates other ride rating")
    public void wrong_rider(){
       completedRide();
    mockSecurityContext("test@gmail.com");
    User user=new User();
    user.setEmail("test@gmail.com");
       RatingDTO ratingDTO = new RatingDTO();
       ratingDTO.setRating(4);
       ratingDTO.setComment("good");
    when(userRepo.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
    when(rideRepo.findById(rideId)).thenReturn(Optional.of(testRide));
    assertThatThrownBy(() -> userService.giveDriverRating(rideId,ratingDTO))
            .isInstanceOf(UserNotFoundException.class).hasMessage("You can only rate your own ride");
    }

    @Test
    @DisplayName("when rider gives rating below 1 or above 5")
    public void invalid_rating(){
       mockSecurityContext(email);
       completedRide();
        RatingDTO ratingDTO = new RatingDTO();
        ratingDTO.setRating(0);
         ratingDTO.setComment("bad");
        when(userRepo.findByEmail(email)).thenReturn(Optional.of(testUser));
        when(rideRepo.findById(rideId)).thenReturn(Optional.of(testRide));
        assertThatThrownBy(() -> userService.giveDriverRating(rideId,ratingDTO))
                .isInstanceOf(InvalidRateException.class).hasMessage("Invalid user rating must be between 1 and 5");

    }

}

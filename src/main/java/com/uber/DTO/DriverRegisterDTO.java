package com.uber.DTO;
import jakarta.validation.constraints.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DriverRegisterDTO {

    @NotBlank(message = "name cannot ne empty")
    String name;

   @Email(message = "invalid email format")
   @NotBlank
    String email;

    @NotBlank(message = "passsword cannot be empty")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&]).{8,}$",
            message=" password cannot be less than 8 and must have atleast A-Z or a-z and special characters"
    )
    String password;

   @NotBlank(message = "phone number cannot be empty")
   @Pattern(regexp = "^[0-9]{10}$", message = "must be a valid 10-digit phone number")
   String phoneNumber;

   @NotBlank(message = "vehicle name cannot be empty")
    String vehicleName;

    @NotBlank(message = "vehicle number cannot be empty")
    String vehicleNumber;

    @NotBlank(message = "DL number cannot be empty")
    String dlNumber;

    @NotNull(message = "dl expiry must be provided  in YYYY/MM/DD Format")
    @Future(message = "DL expiry must be a future date")
    LocalDate dlExpiryDate;

    boolean isAvaliable=true;

}

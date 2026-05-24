package com.uber.services;

import com.uber.DTO.DriverRegisterDTO;
import com.uber.DTO.LoginDTO;
import com.uber.DTO.UserRegisterDTO;
import com.uber.Roles;
import com.uber.entity.Driver;
import com.uber.entity.User;
import com.uber.exceptions.DuplicateEmailException;
import com.uber.repository.DriverRepo;
import com.uber.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepo userRepo;
    private final DriverRepo driverRepo;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final JWTUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    @Override
    public String userRegister(UserRegisterDTO userRegisterDTO) {
       if(userRepo.findByEmail(userRegisterDTO.getEmail()).isPresent()){
           throw new DuplicateEmailException("Email already in use");
       }
       User user = modelMapper.map(userRegisterDTO, User.class);
       user.setPassword(passwordEncoder.encode(userRegisterDTO.getPassword()));
        user.setRole(Roles.USER);
       userRepo.save(user);
       return "registered successfully,Please login";
    }

    @Override
    public String driverRegister(DriverRegisterDTO driverRegisterDTO) {
        if(userRepo.findByEmail(driverRegisterDTO.getEmail()).isPresent()){
            throw new DuplicateEmailException("Email already in use");
        }
        Driver driver = modelMapper.map(driverRegisterDTO, Driver.class);
        driver.setPassword(passwordEncoder.encode(driverRegisterDTO.getPassword()));
        driverRepo.save(driver);
        return "registered successfully,Please login";
    }

    @Override
    public String login(LoginDTO loginDTO) {
       Authentication authentication=authenticationManager.authenticate(new UsernamePasswordAuthenticationToken
               (loginDTO.getEmail(),loginDTO.getPassword()));
       if(authentication!=null){
           //the iteration is done becuase of collections(list) and we remove prefix ROLE_ beacuse enum has USER AND THIS HAS ROLE_USER
           String roleStr=authentication.getAuthorities().iterator().next().getAuthority().replace("ROLE_", "");
           //CONVERT BACK STRING ROLE TO ROLE OF ENUM TYPE
           Roles role = Roles.valueOf(roleStr);
           return jwtUtil.generateToken(authentication.getName(),role);
       }
       throw  new BadCredentialsException("Bad credentials or user not found");

    }
}

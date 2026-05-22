package com.uber.services;

import com.uber.entity.Driver;
import com.uber.entity.User;
import com.uber.repository.DriverRepo;
import com.uber.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LoadUserDetailsImple implements UserDetailsService {
    private final UserRepo userRepo;
    private final DriverRepo driverRepo;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<User> user = userRepo.findByEmail(email);
        if (user.isPresent()) {
            return new org.springframework.security.core.userdetails.User(
                    user.get().getEmail(),
                    user.get().getPassword(),
                    List.of(new SimpleGrantedAuthority("ROLE_" + user.get().getRole().name())
            ));
        }

    Optional<Driver> driver = driverRepo.findByEmail(email);
        if (driver.isPresent()) {
            return new org.springframework.security.core.userdetails.User(
                    driver.get().getEmail(),
                    driver.get().getPassword(),
                    List.of(new SimpleGrantedAuthority("ROLE_" + driver.get().getRole().name()))
            );
        }
        throw new UsernameNotFoundException("User not found");
  }
}

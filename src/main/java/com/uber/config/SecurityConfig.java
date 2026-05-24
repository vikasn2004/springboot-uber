package com.uber.config;

import com.uber.filter.JWTFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {

private final JWTFilter jwtFilter;
private final UserDetailsService userDetailsService;
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authorizeRequests -> authorizeRequests
                        // Public endpoints
                        .requestMatchers("/auth/uber/**").permitAll()

                        // Admin endpoints
                        .requestMatchers("/uber/admin/**").hasRole("ADMIN")

                        // User endpoints
                        .requestMatchers("/uber/user/ride/fare").hasRole("USER")
                        .requestMatchers("/uber/user/ride/request").hasRole("USER")
                        .requestMatchers("/uber/user/ride/cancel/**").hasRole("USER")
                        .requestMatchers("/uber/user/ride/history/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/uber/user/rate/driver/**").hasRole("USER")

                        // Driver endpoints
                        .requestMatchers("/uber/driver/pending/rides").hasAnyRole("DRIVER", "ADMIN")
                        .requestMatchers("/uber/driver/ride/accept/**").hasRole("DRIVER")
                        .requestMatchers("/uber/driver/ride/cancel/**").hasRole("DRIVER")
                        .requestMatchers("/uber/driver/ride/started/**").hasRole("DRIVER")
                        .requestMatchers("/uber/driver/ride/end/**").hasRole("DRIVER")
                        .requestMatchers("/uber/driver/ride/history").hasAnyRole("DRIVER", "ADMIN")
                        .requestMatchers("/uber/driver/getEarnings/**").hasAnyRole("DRIVER", "ADMIN")
                        .requestMatchers("/uber/driver/rate/rider/**").hasRole("DRIVER")
                        .anyRequest().authenticated())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManagerBean(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

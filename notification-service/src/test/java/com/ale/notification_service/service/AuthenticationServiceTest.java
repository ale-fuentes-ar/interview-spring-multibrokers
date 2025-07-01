package com.ale.notification_service.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.ale.notification_service.dto.AuthenticationRequest;
import com.ale.notification_service.dto.AuthenticationResponse;
import com.ale.notification_service.dto.RegisterRequest;
import com.ale.notification_service.model.Role;
import com.ale.notification_service.model.User;
import com.ale.notification_service.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class AuthenticationServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtService jwtService;
    @Mock private AuthenticationManager authenticationManager;

    @InjectMocks private AuthenticationService authenticationService;

    @Test
    void register_shouldSaveUserAndReturnToken(){
        // arrange
        RegisterRequest request = new RegisterRequest("newUser", "password123");
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(jwtService.generateToken(any(User.class))).thenReturn("fake-jwt-token");

        // act
        AuthenticationResponse response = authenticationService.register(request);

        // assert
        assertNotNull(response);
        assertEquals("fake-jwt-token", response.getToken());

        verify(userRepository).save(any(User.class));
        verify(passwordEncoder).encode("password123");
        verify(jwtService).generateToken(any(User.class));
    }

    @Test
    void authenticate_shouldAuthenticateAndReturnToken(){
        // arrange
        AuthenticationRequest request = new AuthenticationRequest("existingUser", "password123");
        User user = User.builder()
            .username("existingUser")
            .password("encodedPassword")
            .role(Role.USER)
            .build();

        when(userRepository.findByUsername("existingUser")).thenReturn(Optional.of(user));
        when(jwtService.generateToken(user)).thenReturn("fake-jwt-token-for-login");

        // act
        AuthenticationResponse response = authenticationService.authenticate(request);

        // assert
        assertNotNull(response);
        assertEquals("fake-jwt-token-for-login", response.getToken());

        verify(authenticationManager).authenticate(any());
        verify(userRepository).findByUsername("existingUser");
        verify(jwtService).generateToken(user);
        
    }
}

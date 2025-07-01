package com.ale.notification_service.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;


public class JwtServiceTest {


    private JwtService jwtService;

    @BeforeEach
    void setup(){
        jwtService = new JwtService();

        String testSecretKey = "Yzg4ZjRjNzE3YTY0YmEyZTY5ZDE3ZDNlM2M5ZTlmMmZkNGExYjQwYjU4MjI4YjZkMGEzZjEwMzhhYWFkMTM4Mw==";
        long testExpiration = 36000000;

        ReflectionTestUtils.setField(jwtService, "secretKey", testSecretKey);
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", testExpiration);
    }

    @Test
    void generateToken_shouldCreateValidToken(){
        // arrange
        UserDetails userDetails = new User("testuser", "password", new ArrayList<>());

        // act
        String token = jwtService.generateToken(userDetails);

        // assert
        assertNotNull(token);
        assertFalse(token.isEmpty());
        System.out.println(String.format("%s: %s", "Generated token", token));
    }

    @Test
    void extractUsername_shouldReturnCoorectUsernameFromToken(){
        // arrange
        UserDetails userDetails = new User("testuser", "password", new ArrayList<>());
        String token = jwtService.generateToken(userDetails);

        // act
        String extrateUsername = jwtService.extractUsername(token);

        // assert
        assertEquals("testuser", extrateUsername);
    }

    @Test
    void isTokenValid_shouldReturnTrueForValidToken(){
        // arrange
        UserDetails userDetails = new User("username", "password", new ArrayList<>());
        String token = jwtService.generateToken(userDetails);

        // act
        boolean isValid = jwtService.isTokenValid(token, userDetails);

        // assert
        assertTrue(isValid);
    }

    @Test
    void isTokenValid_shouldReturnFalseForInvalidUser(){
        // arragne
        UserDetails userDetails1 = new User("user1", "password", new ArrayList<>());
        UserDetails userDetails2 = new User("user2", "password", new ArrayList<>());
        String token = jwtService.generateToken(userDetails1);

        // act
        boolean isValid = jwtService.isTokenValid(token, userDetails2);

        // assert
        assertFalse(isValid);
    }

}

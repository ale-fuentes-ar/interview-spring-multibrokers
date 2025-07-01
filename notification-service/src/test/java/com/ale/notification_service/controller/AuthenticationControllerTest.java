package com.ale.notification_service.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.apache.kafka.streams.processor.internals.StateUpdater.ExceptionAndTask;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.ale.notification_service.dto.AuthenticationRequest;
import com.ale.notification_service.dto.AuthenticationResponse;
import com.ale.notification_service.dto.RegisterRequest;
import com.ale.notification_service.service.AuthenticationService;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private AuthenticationService authenticationService;

    @Test
    void register_shouldReturnTokenOnSuccess() throws Exception {
        // arrange
        RegisterRequest registerRequest = new RegisterRequest("testuser", "password");
        AuthenticationResponse authResponse = new AuthenticationResponse("fake-jwt-token");

        when(authenticationService.register(any(RegisterRequest.class))).thenReturn(authResponse);

        // act
        ResultActions result = mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)));

        // assert
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("fake-jwt-token"));
    }

    @Test
    void authenticate_shouldReturntokenOnSuccess() throws Exception {
        // arrange
        AuthenticationRequest authRequest = new AuthenticationRequest("testuser", "password");
        AuthenticationResponse authResponse = new AuthenticationResponse("another-fake-jwt-token");

        when(authenticationService.authenticate(any(AuthenticationRequest.class))).thenReturn(authResponse);

        // act
        ResultActions result = mockMvc.perform(post("/api/auth/authenticate")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(authRequest)));
        
        // assert
        result.andExpect(status().isOk())
            .andExpect(jsonPath("$.token").value("another-fake-jwt-token"));
    }

    @Test
    void authenticate_shouldReturnUnathorizedOnBadCredentials() throws Exception {
        // arrange
        AuthenticationRequest authRequest = new AuthenticationRequest("wronguser", "wrongpassword");

        doThrow(new BadCredentialsException("Credenciales de inicio de sesión inválidas."))
                .when(authenticationService)
                .authenticate(any(AuthenticationRequest.class));

        // act
        ResultActions result = mockMvc.perform(post("/api/auth/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(authRequest)));

        // assert
        result.andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status", is(401)))
                .andExpect(jsonPath("$.error", is("Unauthorized")))
                .andExpect(jsonPath("$.message", is("Credenciales de inicio de sesión inválidas.")));
    }
}

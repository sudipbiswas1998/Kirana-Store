package com.sudip.kiranastore.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sudip.kiranastore.constant.ReasonCodes;
import com.sudip.kiranastore.entity.User;
import com.sudip.kiranastore.helper.TestData;
import com.sudip.kiranastore.repository.UserRepository;
import com.sudip.kiranastore.service.impl.UserServiceImpl;
import com.sudip.kiranastore.util.ResponseUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegisterUser_Success() {
        // Use test data
        User user = TestData.createUser("testUser", "testPassword");

        // Mock userRepository behavior
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.empty());
        when(userRepository.save(user)).thenReturn(user);

        // Test the registerUser method
        Map<String, Object> response = userService.registerUser(user);

        // Verify userRepository.findByUsername is called once
        verify(userRepository, times(1)).findByUsername("testUser");

        // Verify userRepository.save is called once
        verify(userRepository, times(1)).save(user);

        // Verify the response
        assertEquals(ResponseUtils.successResponse("User registered successfully"), response);
    }

    @Test
    void testRegisterUser_Failure_UsernameExists() {
        // Use test data
        User user = TestData.createUser("existingUser", "testPassword");

        // Mock userRepository behavior
        when(userRepository.findByUsername("existingUser")).thenReturn(Optional.of(user));

        // Test the registerUser method
        Map<String, Object> response = userService.registerUser(user);

        // Verify userRepository.findByUsername is called once
        verify(userRepository, times(1)).findByUsername("existingUser");

        // Verify userRepository.save is not called
        verify(userRepository, never()).save(any());

        // Verify the response
        assertEquals(ResponseUtils.failureResponse("Username already exists", ReasonCodes.ALREADY_PRESENT), response);
    }

    // ... (similar adjustments for other test methods)

    @Test
    void testGetUserById_UserFound() {
        // Use test data
        User user = TestData.createUser("testUser", "testPassword");

        // Mock userRepository behavior
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // Test the getUserById method
        Map<String, Object> response = userService.getUserById(1L);

        // Verify userRepository.findById is called once
        verify(userRepository, times(1)).findById(1L);
    }
}

        //

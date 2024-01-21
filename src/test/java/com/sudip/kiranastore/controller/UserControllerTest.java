package com.sudip.kiranastore.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sudip.kiranastore.entity.User;
import com.sudip.kiranastore.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @Autowired
    private ObjectMapper objectMapper;


    @Test
    public void testRegisterUser() throws Exception {
        User user = new User(); // Create a User object with necessary fields

        // Mock the userService.registerUser() method to return a response
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "User registered successfully");
        Mockito.when(userService.registerUser(any(User.class))).thenReturn(response);

        // Perform the registration request and verify the response
        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("success")))
                .andExpect(jsonPath("$.message", is("User registered successfully")));

        // Verify that userService.registerUser() is called once with the provided user
        Mockito.verify(userService, times(1)).registerUser(user);
    }

    @Test
    public void testGetUserById() throws Exception {
        Long userId = 1L; // Specify a user ID for testing

        // Mock the userService.getUserById() method to return a response
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("user", Collections.singletonMap("id", userId)); // Assuming some user details here
        Mockito.when(userService.getUserById(userId)).thenReturn(response);

        // Perform the getUserById request and verify the response
        mockMvc.perform(get("/api/users/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("success")))
                .andExpect(jsonPath("$.user.id", is(userId.intValue())));

        // Verify that userService.getUserById() is called once with the provided userId
        Mockito.verify(userService, times(1)).getUserById(userId);
    }

}


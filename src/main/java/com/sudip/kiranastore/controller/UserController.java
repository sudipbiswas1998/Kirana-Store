package com.sudip.kiranastore.controller;

import com.sudip.kiranastore.entity.User;
import com.sudip.kiranastore.service.UserService;
import com.sudip.kiranastore.util.BLUtil;
import com.sudip.kiranastore.util.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * Registers a new user.
     *
     * @param user The user details for registration.
     * @return ResponseEntity containing the response map.
     * @throws Exception If an exception occurs during user registration.
     */
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> registerUser(@RequestBody User user) throws Exception {
        Map<String, Object> response = userService.registerUser(user);
        return sendResponse(response);
    }

    /**
     * Retrieves user details by user ID.
     *
     * @param userId The ID of the user to retrieve.
     * @return ResponseEntity containing the response map.
     */
    @GetMapping("/{userId}")
    public ResponseEntity<Map<String, Object>> getUserById(@PathVariable Long userId) {
        Map<String, Object> response = userService.getUserById(userId);
        return sendResponse(response);
    }

    /**
     * Helper method to send the response based on the validation of the response status.
     *
     * @param responseMap The response map.
     * @return ResponseEntity containing the response map.
     */
    private ResponseEntity<Map<String, Object>> sendResponse(Map<String, Object> responseMap) {
        if (!BLUtil.validateResponseStatus(responseMap))
            return ResponseEntity.badRequest().body(responseMap);
        return ResponseEntity.ok(responseMap);
    }
}


package com.sudip.kiranastore.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sudip.kiranastore.constant.ReasonCodes;
import com.sudip.kiranastore.entity.User;
import com.sudip.kiranastore.repository.UserRepository;
import com.sudip.kiranastore.service.UserService;
import com.sudip.kiranastore.util.ResponseUtils;
import lombok.extern.slf4j.Slf4j;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;


    /**
     * Registers a new user.
     *
     * @param user The user to be registered.
     * @return A map containing the result of the operation.
     */
    @Override
    public Map<String, Object> registerUser(User user) {
        log.info("Registering new user: {}", user.getUsername());

        // Check if the username is already taken
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            log.warn("Username {} already exists", user.getUsername());
            return ResponseUtils.failureResponse("Username already exists", ReasonCodes.ALREADY_PRESENT);
        }

        // Encrypt the password before saving to the database
        user.setPassword(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()));
        userRepository.save(user);

        log.info("User {} registered successfully", user.getUsername());
        return ResponseUtils.successResponse("User registered successfully");
    }

    /**
     * Retrieves user details by user ID.
     *
     * @param userId The ID of the user to be retrieved.
     * @return A map containing the result of the operation and user details in JSON format.
     */
    @Override
    public Map<String, Object> getUserById(Long userId) {
        log.info("Fetching user details for user ID: {}", userId);

        ObjectMapper objectMapper = new ObjectMapper();
        User user = userRepository.findById(userId).orElse(null);
        if (user != null) {
            Map<String, Object> userMap = objectMapper.convertValue(user,
                    new TypeReference<Map<String, Object>>() {
                    });
            log.info("Fetched user details for user ID: {}", userId);
            return ResponseUtils.successResponse(userMap);
        } else {
            log.warn("User not found for user ID: {}", userId);
            return ResponseUtils.failureResponse("User not found", ReasonCodes.NOT_FOUND);
        }
    }
}

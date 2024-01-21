package com.sudip.kiranastore.service;

import com.sudip.kiranastore.entity.User;

import java.util.Map;

public interface UserService {
    Map<String, Object> registerUser(User user) throws Exception;

    Map<String, Object> getUserById(Long userId);
}

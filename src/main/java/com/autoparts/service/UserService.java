package com.autoparts.service;

import com.autoparts.dto.RegisterDto;
import com.autoparts.entity.User;

import java.util.List;

public interface UserService {
    User registerUser(RegisterDto dto);
    User getUserByUsername(String username);
    User getUserById(Long id);
    List<User> getAllUsers();
    User updateUser(Long id, RegisterDto dto);
    void toggleUserStatus(Long id);
}

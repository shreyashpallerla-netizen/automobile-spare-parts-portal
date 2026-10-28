package com.autoparts.service.impl;

import com.autoparts.dto.RegisterDto;
import com.autoparts.entity.Role;
import com.autoparts.entity.User;
import com.autoparts.exception.ResourceNotFoundException;
import com.autoparts.repository.RoleRepository;
import com.autoparts.repository.UserRepository;
import com.autoparts.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User registerUser(RegisterDto dto) {
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new IllegalArgumentException("Username already taken: " + dto.getUsername());
        }
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Email already registered: " + dto.getEmail());
        }

        Role userRole = roleRepository.findByName(Role.RoleName.ROLE_USER)
            .orElseThrow(() -> new ResourceNotFoundException("Role not found: ROLE_USER"));

        User user = User.builder()
            .username(dto.getUsername())
            .password(passwordEncoder.encode(dto.getPassword()))
            .email(dto.getEmail())
            .fullName(dto.getFullName())
            .enabled(true)
            .roles(Set.of(userRole))
            .build();

        User saved = userRepository.save(user);
        log.info("Registered new user: {}", saved.getUsername());
        return saved;
    }

    @Override
    @Transactional(readOnly = true)
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
    }

    @Override
    @Transactional(readOnly = true)
    public User getUserById(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User updateUser(Long id, RegisterDto dto) {
        User user = getUserById(id);
        user.setFullName(dto.getFullName());
        user.setEmail(dto.getEmail());
        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        }
        return userRepository.save(user);
    }

    @Override
    public void toggleUserStatus(Long id) {
        User user = getUserById(id);
        user.setEnabled(!user.isEnabled());
        userRepository.save(user);
        log.info("User {} status toggled to: {}", user.getUsername(), user.isEnabled());
    }
}

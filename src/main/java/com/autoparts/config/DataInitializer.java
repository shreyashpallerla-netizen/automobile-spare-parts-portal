package com.autoparts.config;

import com.autoparts.entity.Role;
import com.autoparts.entity.User;
import com.autoparts.repository.RoleRepository;
import com.autoparts.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        // Create roles if not exist
        Role adminRole = roleRepository.findByName(Role.RoleName.ROLE_ADMIN)
            .orElseGet(() -> roleRepository.save(Role.builder().name(Role.RoleName.ROLE_ADMIN).build()));

        Role userRole = roleRepository.findByName(Role.RoleName.ROLE_USER)
            .orElseGet(() -> roleRepository.save(Role.builder().name(Role.RoleName.ROLE_USER).build()));

        // Create admin user if not exist
        if (!userRepository.existsByUsername("admin")) {
            User admin = User.builder()
                .username("admin")
                .password(passwordEncoder.encode("Admin@123"))
                .email("admin@autoparts.com")
                .fullName("System Administrator")
                .enabled(true)
                .roles(Set.of(adminRole, userRole))
                .build();
            userRepository.save(admin);
            log.info("Admin user created: admin / Admin@123");
        }

        // Create demo user if not exist
        if (!userRepository.existsByUsername("user1")) {
            User demoUser = User.builder()
                .username("user1")
                .password(passwordEncoder.encode("User@123"))
                .email("user1@autoparts.com")
                .fullName("Demo User")
                .enabled(true)
                .roles(Set.of(userRole))
                .build();
            userRepository.save(demoUser);
            log.info("Demo user created: user1 / User@123");
        }
    }
}

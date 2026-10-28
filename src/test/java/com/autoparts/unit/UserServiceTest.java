package com.autoparts.unit;

import com.autoparts.dto.RegisterDto;
import com.autoparts.entity.Role;
import com.autoparts.entity.User;
import com.autoparts.exception.ResourceNotFoundException;
import com.autoparts.repository.RoleRepository;
import com.autoparts.repository.UserRepository;
import com.autoparts.service.impl.UserServiceImpl;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService Unit Tests")
class UserServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private RoleRepository roleRepository;
    @Mock private PasswordEncoder passwordEncoder;

    @InjectMocks private UserServiceImpl userService;

    private Role userRole;
    private User sampleUser;

    @BeforeEach
    void setUp() {
        userRole = Role.builder().id(1L).name(Role.RoleName.ROLE_USER).build();
        sampleUser = User.builder()
            .id(1L).username("testuser").password("encodedPassword")
            .email("test@example.com").fullName("Test User").enabled(true).build();
    }

    @Test
    @DisplayName("Should register new user successfully")
    void testRegisterUser_Success() {
        RegisterDto dto = RegisterDto.builder()
            .username("newuser").password("NewPass@1")
            .email("new@example.com").fullName("New User").build();

        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(roleRepository.findByName(Role.RoleName.ROLE_USER)).thenReturn(Optional.of(userRole));
        when(passwordEncoder.encode(anyString())).thenReturn("encoded");
        when(userRepository.save(any(User.class))).thenReturn(sampleUser);

        User result = userService.registerUser(dto);

        assertThat(result).isNotNull();
        verify(userRepository).save(any(User.class));
        verify(passwordEncoder).encode("NewPass@1");
    }

    @Test
    @DisplayName("Should throw when username already taken")
    void testRegisterUser_DuplicateUsername() {
        RegisterDto dto = RegisterDto.builder()
            .username("testuser").password("Pass@1").email("new@test.com").fullName("Test").build();

        when(userRepository.existsByUsername("testuser")).thenReturn(true);

        assertThatThrownBy(() -> userService.registerUser(dto))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("testuser");
    }

    @Test
    @DisplayName("Should get user by username")
    void testGetUserByUsername() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(sampleUser));

        User result = userService.getUserByUsername("testuser");

        assertThat(result.getUsername()).isEqualTo("testuser");
    }

    @Test
    @DisplayName("Should throw when user not found by username")
    void testGetUserByUsername_NotFound() {
        when(userRepository.findByUsername("nobody")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserByUsername("nobody"))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Should toggle user active status")
    void testToggleUserStatus() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));
        when(userRepository.save(any(User.class))).thenReturn(sampleUser);

        userService.toggleUserStatus(1L);

        assertThat(sampleUser.isEnabled()).isFalse();
        verify(userRepository).save(sampleUser);
    }
}

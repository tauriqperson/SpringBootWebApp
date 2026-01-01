package com.example.springbootwebapp.service;

import com.example.springbootwebapp.dto.ProfileUpdateRequest;
import com.example.springbootwebapp.dto.RegisterRequest;
import com.example.springbootwebapp.dto.UserResponse;
import com.example.springbootwebapp.model.Role;
import com.example.springbootwebapp.model.User;
import com.example.springbootwebapp.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private PasswordEncoder passwordEncoder;
    
    @InjectMocks
    private UserService userService;
    
    private User testUser;
    private RegisterRequest registerRequest;
    
    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("encodedPassword");
        testUser.setFullName("Test User");
        testUser.setRole(Role.USER);
        
        registerRequest = new RegisterRequest();
        registerRequest.setUsername("newuser");
        registerRequest.setEmail("newuser@example.com");
        registerRequest.setPassword("password123");
        registerRequest.setFullName("New User");
    }
    
    @Test
    void registerUser_Success() {
        //Arrange
        when(userRepository.existsByUsername(registerRequest.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(registerRequest.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        
        //Act
        UserResponse response = userService.registerUser(registerRequest);
        
        //Assert
        assertNotNull(response);
        assertEquals(testUser.getId(), response.getId());
        verify(userRepository, times(1)).save(any(User.class));
    }
    
    @Test
    void registerUser_UsernameExists_ThrowsException() {
        //Arrange
        when(userRepository.existsByUsername(registerRequest.getUsername())).thenReturn(true);
        
        //Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.registerUser(registerRequest);
        });
        
        assertEquals("Username already exists", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }
    
    @Test
    void registerUser_EmailExists_ThrowsException() {
        //Arrange
        when(userRepository.existsByUsername(registerRequest.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(true);
        
        //Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.registerUser(registerRequest);
        });
        
        assertEquals("Email already exists", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }
    
    @Test
    void getUserByUsername_Success() {
        //Arrange
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        
        //Act
        UserResponse response = userService.getUserByUsername("testuser");
        
        //Assert
        assertNotNull(response);
        assertEquals(testUser.getUsername(), response.getUsername());
        assertEquals(testUser.getEmail(), response.getEmail());
    }
    
    @Test
    void getUserByUsername_NotFound_ThrowsException() {
        //Arrange
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());
        
        //Act & Assert
        assertThrows(RuntimeException.class, () -> {
            userService.getUserByUsername("nonexistent");
        });
    }
    
    @Test
    void updateProfile_Success() {
        //Arrange
        ProfileUpdateRequest updateRequest = new ProfileUpdateRequest();
        updateRequest.setFullName("Updated Name");
        updateRequest.setEmail("updated@example.com");
        
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(userRepository.existsByEmail(updateRequest.getEmail())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        
        //Act
        UserResponse response = userService.updateProfile("testuser", updateRequest);
        
        //Assert
        assertNotNull(response);
        verify(userRepository, times(1)).save(any(User.class));
    }
    
    @Test
    void getAllUsers_Success() {
        //Arrange
        User user2 = new User();
        user2.setId(2L);
        user2.setUsername("user2");
        user2.setEmail("user2@example.com");
        user2.setPassword("password");
        user2.setFullName("User Two");
        user2.setRole(Role.USER);
        
        when(userRepository.findAll()).thenReturn(Arrays.asList(testUser, user2));
        
        //Act
        List<UserResponse> users = userService.getAllUsers();
        
        //Assert
        assertNotNull(users);
        assertEquals(2, users.size());
        verify(userRepository, times(1)).findAll();
    }
}

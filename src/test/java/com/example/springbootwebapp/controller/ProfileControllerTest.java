package com.example.springbootwebapp.controller;

import com.example.springbootwebapp.dto.ProfileUpdateRequest;
import com.example.springbootwebapp.dto.UserResponse;
import com.example.springbootwebapp.model.Role;
import com.example.springbootwebapp.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProfileController.class)
class ProfileControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private UserService userService;
    
    @Test
    @WithMockUser(username = "testuser")
    void profilePage_ReturnsProfileView() throws Exception {
        //Arrange
        UserResponse userResponse = new UserResponse(1L, "testuser", "test@example.com", "Test User", Role.USER);
        when(userService.getUserByUsername("testuser")).thenReturn(userResponse);
        
        //Act & Assert
        mockMvc.perform(get("/profile"))
                .andExpect(status().isOk())
                .andExpect(view().name("profile"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attributeExists("profileUpdateRequest"));
    }
    
    @Test
    @WithMockUser(username = "testuser")
    void updateProfile_ValidData_Success() throws Exception {
        //Arrange
        UserResponse userResponse = new UserResponse(1L, "testuser", "updated@example.com", "Updated Name", Role.USER);
        when(userService.updateProfile(eq("testuser"), any(ProfileUpdateRequest.class))).thenReturn(userResponse);
        
        //Act & Assert
        mockMvc.perform(post("/profile/update")
                .with(csrf())
                .param("fullName", "Updated Name")
                .param("email", "updated@example.com"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profile"));
    }
    
    @Test
    @WithMockUser(username = "testuser")
    void updateProfile_InvalidEmail_ReturnsProfileView() throws Exception {
        //Arrange
        UserResponse userResponse = new UserResponse(1L, "testuser", "test@example.com", "Test User", Role.USER);
        when(userService.getUserByUsername("testuser")).thenReturn(userResponse);
        
        //Act & Assert
        mockMvc.perform(post("/profile/update")
                .with(csrf())
                .param("fullName", "Updated Name")
                .param("email", "invalid-email"))
                .andExpect(status().isOk())
                .andExpect(view().name("profile"));
    }
}

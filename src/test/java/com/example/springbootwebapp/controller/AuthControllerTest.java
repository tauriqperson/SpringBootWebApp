package com.example.springbootwebapp.controller;

import com.example.springbootwebapp.dto.RegisterRequest;
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
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
class AuthControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private UserService userService;
    
    @Test
    void loginPage_ReturnsLoginView() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"));
    }
    
    @Test
    void registerPage_ReturnsRegisterView() throws Exception {
        mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attributeExists("registerRequest"));
    }
    
    @Test
    @WithMockUser
    void register_ValidData_Success() throws Exception {
        //Arrange
        UserResponse userResponse = new UserResponse(1L, "testuser", "test@example.com", "Test User", Role.USER);
        when(userService.registerUser(any(RegisterRequest.class))).thenReturn(userResponse);
        
        //Act & Assert
        mockMvc.perform(post("/register")
                .with(csrf())
                .param("username", "testuser")
                .param("email", "test@example.com")
                .param("password", "password123")
                .param("fullName", "Test User"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }
    
    @Test
    @WithMockUser
    void register_InvalidData_ReturnsRegisterView() throws Exception {
        mockMvc.perform(post("/register")
                .with(csrf())
                .param("username", "ab")  
                .param("email", "invalid-email")
                .param("password", "123") 
                .param("fullName", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("register"));
    }
}

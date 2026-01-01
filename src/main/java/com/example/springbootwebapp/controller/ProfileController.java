package com.example.springbootwebapp.controller;

import com.example.springbootwebapp.dto.ProfileUpdateRequest;
import com.example.springbootwebapp.dto.UserResponse;
import com.example.springbootwebapp.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ProfileController {
    
    private final UserService userService;
    
    public ProfileController(UserService userService) {
        this.userService = userService;
    }
    
    @GetMapping("/profile")
    public String profilePage(Authentication authentication, Model model) {
        String username = authentication.getName();
        UserResponse user = userService.getUserByUsername(username);
        
        ProfileUpdateRequest updateRequest = new ProfileUpdateRequest();
        updateRequest.setFullName(user.getFullName());
        updateRequest.setEmail(user.getEmail());
        
        model.addAttribute("user", user);
        model.addAttribute("profileUpdateRequest", updateRequest);
        return "profile";
    }
    
    @PostMapping("/profile/update")
    public String updateProfile(@Valid @ModelAttribute("profileUpdateRequest") ProfileUpdateRequest request,
                               BindingResult bindingResult,
                               Authentication authentication,
                               RedirectAttributes redirectAttributes,
                               Model model) {
        
        if (bindingResult.hasErrors()) {
            String username = authentication.getName();
            UserResponse user = userService.getUserByUsername(username);
            model.addAttribute("user", user);
            return "profile";
        }
        
        try {
            String username = authentication.getName();
            userService.updateProfile(username, request);
            redirectAttributes.addFlashAttribute("successMessage", "Profile updated successfully!");
            return "redirect:/profile";
        } catch (RuntimeException e) {
            String username = authentication.getName();
            UserResponse user = userService.getUserByUsername(username);
            model.addAttribute("user", user);
            model.addAttribute("errorMessage", e.getMessage());
            return "profile";
        }
    }
}

package com.example.apartmentsalesmanagementsystem.controller;

import com.example.apartmentsalesmanagementsystem.entity.User;
import com.example.apartmentsalesmanagementsystem.entity.UserRole;
import com.example.apartmentsalesmanagementsystem.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/users")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("user") User user, 
                              BindingResult result, 
                              RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "register";
        }
        
        // Check if username already exists
        if (userService.existsByUsername(user.getUsername())) {
            result.rejectValue("username", "error.user", "Username already exists");
            return "register";
        }
        
        // Check if email already exists
        if (userService.existsByEmail(user.getEmail())) {
            result.rejectValue("email", "error.user", "Email already exists");
            return "register";
        }
        
        try {
            // Set default role as CLIENT
            user.setRole(UserRole.CLIENT);
            user.setActive(true);
            
            userService.saveUser(user);
            
            redirectAttributes.addFlashAttribute("success", "Registration successful! Please login.");
            return "redirect:/login";
            
        } catch (DataIntegrityViolationException e) {
            // Handle database constraint violations
            String errorMessage = e.getMessage();
            if (errorMessage.contains("username") || errorMessage.contains("CONSTRAINT_INDEX_4")) {
                result.rejectValue("username", "error.user", "Username already exists. Please choose a different username.");
            } else if (errorMessage.contains("email")) {
                result.rejectValue("email", "error.user", "Email already exists. Please use a different email address.");
            } else {
                result.rejectValue("username", "error.user", "Registration failed. Please try again with different information.");
            }
            return "register";
        } catch (Exception e) {
            // Handle any other unexpected errors
            result.rejectValue("username", "error.user", "Registration failed due to an unexpected error. Please try again.");
            return "register";
        }
    }
    
    @GetMapping("/profile")
    public String viewProfile(Model model) {
        // This will be implemented with authentication
        return "user/profile";
    }
    
    @GetMapping("/profile/edit")
    public String editProfile(Model model) {
        // This will be implemented with authentication
        return "user/edit-profile";
    }
    
    @PostMapping("/profile/update")
    public String updateProfile(@Valid @ModelAttribute("user") User user, 
                               BindingResult result, 
                               RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "user/edit-profile";
        }
        
        userService.updateUser(user);
        redirectAttributes.addFlashAttribute("success", "Profile updated successfully!");
        return "redirect:/users/profile";
    }
    
    /**
     * Development endpoint to clear all users (for testing purposes)
     * WARNING: This will delete all user data!
     */
    @PostMapping("/clear-all")
    public String clearAllUsers(RedirectAttributes redirectAttributes) {
        try {
            userService.clearAllUsers();
            redirectAttributes.addFlashAttribute("success", "All users cleared successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to clear users: " + e.getMessage());
        }
        return "redirect:/register";
    }
}

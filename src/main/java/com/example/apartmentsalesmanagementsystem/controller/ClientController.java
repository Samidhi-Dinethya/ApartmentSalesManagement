package com.example.apartmentsalesmanagementsystem.controller;

import com.example.apartmentsalesmanagementsystem.entity.Apartment;
import com.example.apartmentsalesmanagementsystem.entity.User;
import com.example.apartmentsalesmanagementsystem.entity.ManagementRole;
import com.example.apartmentsalesmanagementsystem.service.ApartmentService;
import com.example.apartmentsalesmanagementsystem.service.UserService;
import com.example.apartmentsalesmanagementsystem.service.ReviewService;
import com.example.apartmentsalesmanagementsystem.service.ParkingService;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/client")
public class ClientController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private ApartmentService apartmentService;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private ReviewService reviewService;
    
    @Autowired
    private ParkingService parkingService;
    
    @GetMapping("/dashboard")
    public String clientDashboard(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        
        model.addAttribute("user", currentUser);
        model.addAttribute("myApartments", apartmentService.findApartmentsByOwner(currentUser));
        model.addAttribute("availableApartments", apartmentService.findAvailableApartments());
        model.addAttribute("totalMyApartments", apartmentService.countApartmentsByOwner(currentUser));
        
        return "client/dashboard";
    }
    
    @GetMapping("/apartments")
    public String viewAllApartments(Model model) {
        model.addAttribute("apartments", apartmentService.findAvailableApartments());
        return "client/apartments";
    }
    
    @GetMapping("/apartments/{id}")
    public String viewApartment(@PathVariable Long id, Model model) {
        apartmentService.findById(id).ifPresent(apartment -> {
            model.addAttribute("apartment", apartment);
            
            // Add review data
            model.addAttribute("reviews", reviewService.getApprovedReviewsByApartment(apartment));
            model.addAttribute("averageRating", reviewService.getAverageRatingForApartment(apartment));
            model.addAttribute("totalReviews", reviewService.getTotalReviewCountForApartment(apartment));
        });
        return "client/apartment-detail";
    }
    
    @GetMapping("/my-apartments")
    public String myApartments(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        
        model.addAttribute("apartments", apartmentService.findApartmentsByOwner(currentUser));
        return "client/my-apartments";
    }
    
    @GetMapping("/apartments/add")
    public String addApartmentForm(Model model) {
        model.addAttribute("apartment", new Apartment());
        return "client/add-apartment";
    }
    
    @PostMapping("/apartments/add")
    public String addApartment(@ModelAttribute Apartment apartment) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        
        apartment.setOwner(currentUser);
        apartmentService.saveApartment(apartment);
        
        return "redirect:/client/my-apartments";
    }
    
    @GetMapping("/apartments/{id}/edit")
    public String editApartmentForm(@PathVariable Long id, Model model) {
        apartmentService.findById(id).ifPresent(apartment -> model.addAttribute("apartment", apartment));
        return "client/edit-apartment";
    }
    
    @PostMapping("/apartments/{id}/edit")
    public String editApartment(@PathVariable Long id, @ModelAttribute Apartment apartment) {
        apartmentService.updateApartment(apartment);
        return "redirect:/client/my-apartments";
    }
    
    @PostMapping("/apartments/{id}/delete")
    public String deleteApartment(@PathVariable Long id) {
        apartmentService.deleteApartment(id);
        return "redirect:/client/my-apartments";
    }
    
    @GetMapping("/profile")
    public String viewProfile(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        
        model.addAttribute("user", currentUser);
        
        // Add statistics
        model.addAttribute("totalMyApartments", apartmentService.countApartmentsByOwner(currentUser));
        model.addAttribute("totalReviews", reviewService.getReviewsByUserCount(currentUser));
        
        return "client/profile";
    }
    
    @GetMapping("/agents")
    public String viewAllAgents(Model model) {
        // Get all users with AGENT management role
        List<User> agents = userService.findAllUsers().stream()
                .filter(user -> user.getManagementRoles().contains(ManagementRole.AGENT))
                .collect(Collectors.toList());
        model.addAttribute("agents", agents);
        return "client/agents";
    }
    
    @GetMapping("/agents/{id}")
    public String viewAgent(@PathVariable Long id, Model model) {
        userService.findById(id).ifPresent(agent -> {
            // Only show agents (users with AGENT management role)
            if (agent.getManagementRoles().contains(ManagementRole.AGENT)) {
                model.addAttribute("agent", agent);
            }
        });
        return "client/agent-detail";
    }
    
    @GetMapping("/profile/edit")
    public String editProfileForm(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        
        model.addAttribute("user", currentUser);
        return "client/edit-profile";
    }
    
    @PostMapping("/profile/update")
    public String updateProfile(@RequestParam String firstName,
                               @RequestParam String lastName,
                               @RequestParam String email,
                               @RequestParam(required = false) String phoneNumber,
                               RedirectAttributes redirectAttributes) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User currentUser = (User) authentication.getPrincipal();
            
            // Fetch the user from database to ensure we have the latest version
            Optional<User> userOpt = userService.findById(currentUser.getId());
            if (!userOpt.isPresent()) {
                redirectAttributes.addFlashAttribute("error", "User not found.");
                return "redirect:/client/profile/edit";
            }
            
            User dbUser = userOpt.get();
            
            // Log the current values for debugging
            System.out.println("Current user ID: " + dbUser.getId());
            System.out.println("Current firstName: " + dbUser.getFirstName());
            System.out.println("New firstName: " + firstName);
            
            // Update only the fields that can be changed by the user
            dbUser.setFirstName(firstName);
            dbUser.setLastName(lastName);
            dbUser.setEmail(email);
            dbUser.setPhoneNumber(phoneNumber);
            
            // Save the updated user
            User savedUser = userService.updateUser(dbUser);
            System.out.println("User saved with ID: " + savedUser.getId());
            System.out.println("Updated firstName: " + savedUser.getFirstName());
            
            redirectAttributes.addFlashAttribute("success", "Profile updated successfully!");
            return "redirect:/client/profile";
            
        } catch (Exception e) {
            System.err.println("Error updating profile: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Error updating profile: " + e.getMessage());
            return "redirect:/client/profile/edit";
        }
    }
    
    @PostMapping("/profile/change-password")
    public String changePassword(@RequestParam String currentPassword,
                                @RequestParam String newPassword,
                                @RequestParam String confirmPassword,
                                RedirectAttributes redirectAttributes) {
        try {
            // Validate new password and confirmation match
            if (!newPassword.equals(confirmPassword)) {
                redirectAttributes.addFlashAttribute("error", "New passwords do not match!");
                return "redirect:/client/profile/edit";
            }
            
            // Validate new password length
            if (newPassword.length() < 6) {
                redirectAttributes.addFlashAttribute("error", "New password must be at least 6 characters long!");
                return "redirect:/client/profile/edit";
            }
            
            // Get current authenticated user
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User currentUser = (User) authentication.getPrincipal();
            
            // Fetch the user from database to ensure we have the latest version
            Optional<User> userOpt = userService.findById(currentUser.getId());
            if (!userOpt.isPresent()) {
                redirectAttributes.addFlashAttribute("error", "User not found.");
                return "redirect:/client/profile/edit";
            }
            
            User dbUser = userOpt.get();
            
            // Verify current password matches
            if (!passwordEncoder.matches(currentPassword, dbUser.getPassword())) {
                redirectAttributes.addFlashAttribute("error", "Current password is incorrect!");
                return "redirect:/client/profile/edit";
            }
            
            // Check if new password is different from current password
            if (passwordEncoder.matches(newPassword, dbUser.getPassword())) {
                redirectAttributes.addFlashAttribute("error", "New password must be different from current password!");
                return "redirect:/client/profile/edit";
            }
            
            // Update the password
            dbUser.setPassword(newPassword); // This will be encoded in updateUser method
            userService.updateUser(dbUser);
            
            redirectAttributes.addFlashAttribute("success", "Password changed successfully!");
            return "redirect:/client/profile";
            
        } catch (Exception e) {
            System.err.println("Error changing password: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Error changing password: " + e.getMessage());
            return "redirect:/client/profile/edit";
        }
    }
    
    @GetMapping("/parking-slots")
    public String viewAssignedParkingSlots(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        
        // Get parking slots assigned to the current user
        List<com.example.apartmentsalesmanagementsystem.entity.Parking> assignedParkingSlots = 
            parkingService.getParkingSpacesByTenant(currentUser);
        
        model.addAttribute("user", currentUser);
        model.addAttribute("assignedParkingSlots", assignedParkingSlots);
        model.addAttribute("totalAssignedSlots", assignedParkingSlots.size());
        
        return "client/parking-slots";
    }
}

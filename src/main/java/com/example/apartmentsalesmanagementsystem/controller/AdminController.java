package com.example.apartmentsalesmanagementsystem.controller;

import com.example.apartmentsalesmanagementsystem.entity.User;
import com.example.apartmentsalesmanagementsystem.entity.UserRole;
import com.example.apartmentsalesmanagementsystem.entity.ManagementRole;
import com.example.apartmentsalesmanagementsystem.entity.Apartment;
import com.example.apartmentsalesmanagementsystem.entity.ApartmentStatus;
import com.example.apartmentsalesmanagementsystem.entity.AppointmentStatus;
import com.example.apartmentsalesmanagementsystem.service.ApartmentService;
import com.example.apartmentsalesmanagementsystem.service.UserService;
import com.example.apartmentsalesmanagementsystem.service.AppointmentService;
import com.example.apartmentsalesmanagementsystem.service.AppointmentRequestService;
import com.example.apartmentsalesmanagementsystem.service.ReviewService;
import com.example.apartmentsalesmanagementsystem.service.ParkingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.Optional;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class AdminController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private ApartmentService apartmentService;
    
    @Autowired
    private AppointmentService appointmentService;
    
    @Autowired
    private AppointmentRequestService appointmentRequestService;
    
    @Autowired
    private ReviewService reviewService;
    
    @Autowired
    private ParkingService parkingService;
    
    @GetMapping("/dashboard")
    public String adminDashboard(Model model) {
        model.addAttribute("totalUsers", userService.getTotalUsers());
        model.addAttribute("activeUsers", userService.getActiveUsersCount());
        model.addAttribute("totalClients", userService.countUsersByRole(UserRole.CLIENT));
        model.addAttribute("totalAdmins", userService.countUsersByRole(UserRole.ADMIN));
        model.addAttribute("totalApartments", apartmentService.getTotalApartments());
        model.addAttribute("availableApartments", apartmentService.getAvailableApartmentsCount());
        model.addAttribute("soldApartments", apartmentService.getSoldApartmentsCount());
        model.addAttribute("underContractApartments", apartmentService.getUnderContractApartmentsCount());
        
        // Add agent statistics
        long totalAgents = userService.findAllUsers().stream()
                .filter(user -> user.getManagementRoles().contains(ManagementRole.AGENT))
                .count();
        model.addAttribute("totalAgents", totalAgents);
        
        // Add appointment statistics
        model.addAttribute("totalAppointments", appointmentService.getTotalAppointments());
        model.addAttribute("scheduledAppointments", appointmentService.countAppointmentsByStatus(AppointmentStatus.SCHEDULED));
        model.addAttribute("confirmedAppointments", appointmentService.countAppointmentsByStatus(AppointmentStatus.CONFIRMED));
        model.addAttribute("completedAppointments", appointmentService.countAppointmentsByStatus(AppointmentStatus.COMPLETED));
        model.addAttribute("cancelledAppointments", appointmentService.countAppointmentsByStatus(AppointmentStatus.CANCELLED));
        
        // Add appointment request statistics
        model.addAttribute("totalAppointmentRequests", appointmentRequestService.getTotalRequests());
        model.addAttribute("pendingAppointmentRequests", appointmentRequestService.getPendingRequestsCount());
        model.addAttribute("approvedAppointmentRequests", appointmentRequestService.getApprovedRequestsCount());
        model.addAttribute("rejectedAppointmentRequests", appointmentRequestService.getRejectedRequestsCount());
        model.addAttribute("completedAppointmentRequests", appointmentRequestService.getCompletedRequestsCount());
        
        // Add review statistics
        model.addAttribute("totalReviews", reviewService.getTotalReviews());
        model.addAttribute("approvedReviews", reviewService.getApprovedReviewsCount());
        model.addAttribute("pendingReviews", reviewService.getPendingReviewsCount());
        model.addAttribute("featuredReviews", reviewService.getFeaturedReviewsCount());
        model.addAttribute("verifiedReviews", reviewService.getVerifiedReviewsCount());
        model.addAttribute("highRatedReviews", reviewService.getHighRatedReviewsCount());
        model.addAttribute("lowRatedReviews", reviewService.getLowRatedReviewsCount());
        
        // Add parking statistics
        model.addAttribute("totalParkingSpaces", parkingService.getTotalParkingSpaces());
        model.addAttribute("availableParkingSpaces", parkingService.getAvailableParkingSpacesCount());
        model.addAttribute("occupiedParkingSpaces", parkingService.getOccupiedParkingSpacesCount());
        model.addAttribute("reservedParkingSpaces", parkingService.getReservedParkingSpacesCount());
        model.addAttribute("maintenanceParkingSpaces", parkingService.getMaintenanceParkingSpacesCount());
        model.addAttribute("parkingRevenue", parkingService.getTotalMonthlyRevenue());
        model.addAttribute("parkingOccupancyRate", parkingService.getOccupancyRate());
        
        return "admin/dashboard";
    }
    
    @GetMapping("/users")
    public String manageUsers(Model model) {
        model.addAttribute("users", userService.findAllUsers());
        return "admin/users";
    }
    
    @GetMapping("/users/create")
    public String createUserForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("roles", UserRole.values());
        model.addAttribute("managementRoles", ManagementRole.values());
        return "admin/create-user";
    }
    
    @PostMapping("/users/create")
    public String createUser(@ModelAttribute User user, @RequestParam String confirmPassword, 
                           @RequestParam(value = "managementRole", required = false) String managementRole,
                           RedirectAttributes redirectAttributes) {
        try {
            // Validate password confirmation
            if (!user.getPassword().equals(confirmPassword)) {
                redirectAttributes.addFlashAttribute("error", "Passwords do not match!");
                return "redirect:/admin/users/create";
            }
            
            // Check if username already exists
            if (userService.existsByUsername(user.getUsername())) {
                redirectAttributes.addFlashAttribute("error", "Username '" + user.getUsername() + "' already exists. Please choose a different username.");
                return "redirect:/admin/users/create";
            }
            
            // Check if email already exists
            if (userService.existsByEmail(user.getEmail())) {
                redirectAttributes.addFlashAttribute("error", "Email '" + user.getEmail() + "' already exists. Please use a different email.");
                return "redirect:/admin/users/create";
            }
            
            // Handle management role for administrators
            if (user.getRole() == UserRole.ADMIN && managementRole != null && !managementRole.trim().isEmpty()) {
                try {
                    ManagementRole role = ManagementRole.valueOf(managementRole);
                    user.addManagementRole(role);
                } catch (IllegalArgumentException e) {
                    // Invalid role name, skip it
                }
            }
            
            // Create the user
            userService.saveUser(user);
            redirectAttributes.addFlashAttribute("success", "User created successfully!");
            return "redirect:/admin/users";
        } catch (Exception e) {
            String errorMessage = "Error creating user: ";
            if (e.getMessage().contains("Unique index or primary key violation")) {
                if (e.getMessage().contains("USERNAME")) {
                    errorMessage += "Username already exists. Please choose a different username.";
                } else if (e.getMessage().contains("EMAIL")) {
                    errorMessage += "Email already exists. Please use a different email.";
                } else {
                    errorMessage += "A user with this information already exists.";
                }
            } else {
                errorMessage += e.getMessage();
            }
            redirectAttributes.addFlashAttribute("error", errorMessage);
            return "redirect:/admin/users/create";
        }
    }
    
    @GetMapping("/users/{id}")
    public String viewUser(@PathVariable Long id, Model model) {
        userService.findById(id).ifPresent(user -> model.addAttribute("user", user));
        return "admin/user-detail";
    }
    
    @GetMapping("/users/{id}/edit")
    public String editUser(@PathVariable Long id, Model model) {
        userService.findById(id).ifPresent(user -> model.addAttribute("user", user));
        return "admin/edit-user";
    }
    
    @PostMapping("/users/{id}/update")
    public String updateUser(@PathVariable Long id, @ModelAttribute User user, RedirectAttributes redirectAttributes) {
        userService.updateUser(user);
        redirectAttributes.addFlashAttribute("success", "User updated successfully!");
        return "redirect:/admin/users";
    }
    
    @PostMapping("/users/{id}/deactivate")
    public String deactivateUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        userService.deactivateUser(id);
        redirectAttributes.addFlashAttribute("success", "User deactivated successfully!");
        return "redirect:/admin/users";
    }
    
    @PostMapping("/users/{id}/activate")
    public String activateUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        userService.activateUser(id);
        redirectAttributes.addFlashAttribute("success", "User activated successfully!");
        return "redirect:/admin/users";
    }
    
    @PostMapping("/users/{id}/delete")
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            // Check if user exists
            Optional<User> userOpt = userService.findById(id);
            if (userOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "User not found!");
                return "redirect:/admin/users";
            }
            
            User user = userOpt.get();
            
            // Prevent deletion of the current admin user (you might want to add this check)
            // For now, we'll allow deletion but add a warning for admin users
            
            // Check if user has any apartments (optional safety check)
            // You can add this check if you want to prevent deletion of users with apartments
            
            userService.deleteUser(id);
            redirectAttributes.addFlashAttribute("success", "User '" + user.getUsername() + "' deleted successfully!");
            return "redirect:/admin/users";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error deleting user: " + e.getMessage());
            return "redirect:/admin/users";
        }
    }
    
    @GetMapping("/apartments")
    public String manageApartments(Model model) {
        model.addAttribute("apartments", apartmentService.findAllApartments());
        return "admin/apartments";
    }
    
    @GetMapping("/apartments/create")
    public String createApartmentForm(Model model) {
        model.addAttribute("apartment", new Apartment());
        model.addAttribute("apartmentStatuses", ApartmentStatus.values());
        model.addAttribute("users", userService.findAllUsers());
        return "admin/create-apartment";
    }
    
    @PostMapping("/apartments/create")
    public String createApartment(@ModelAttribute Apartment apartment, 
                                @RequestParam(value = "ownerId", required = false) Long ownerId,
                                @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
                                RedirectAttributes redirectAttributes) {
        try {
            // Set owner if provided
            if (ownerId != null) {
                userService.findById(ownerId).ifPresent(apartment::setOwner);
            }
            
            // Save apartment with image upload support
            apartmentService.saveApartmentWithImage(apartment, imageFile);
            redirectAttributes.addFlashAttribute("success", "Apartment created successfully!");
            return "redirect:/admin/apartments";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error creating apartment: " + e.getMessage());
            return "redirect:/admin/apartments/create";
        }
    }
    
    @GetMapping("/apartments/{id}")
    public String viewApartment(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Optional<Apartment> apartmentOpt = apartmentService.findById(id);
            if (apartmentOpt.isPresent()) {
                model.addAttribute("apartment", apartmentOpt.get());
                return "admin/apartment-detail";
            } else {
                redirectAttributes.addFlashAttribute("error", "Apartment not found!");
                return "redirect:/admin/apartments";
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error accessing apartment: " + e.getMessage());
            return "redirect:/admin/apartments";
        }
    }
    
    @GetMapping("/apartments/{id}/edit")
    public String editApartment(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Optional<Apartment> apartmentOpt = apartmentService.findById(id);
            if (apartmentOpt.isPresent()) {
                Apartment apartment = apartmentOpt.get();
                model.addAttribute("apartment", apartment);
                model.addAttribute("apartmentStatuses", ApartmentStatus.values());
                model.addAttribute("users", userService.findAllUsers());
                return "admin/edit-apartment";
            } else {
                redirectAttributes.addFlashAttribute("error", "Apartment not found!");
                return "redirect:/admin/apartments";
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error accessing apartment: " + e.getMessage());
            return "redirect:/admin/apartments";
        }
    }
    
    @PostMapping("/apartments/{id}/update")
    public String updateApartment(@PathVariable Long id, @ModelAttribute Apartment apartment,
                                @RequestParam(value = "ownerId", required = false) Long ownerId,
                                @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
                                RedirectAttributes redirectAttributes) {
        try {
            // Set owner if provided
            if (ownerId != null) {
                userService.findById(ownerId).ifPresent(apartment::setOwner);
            } else {
                apartment.setOwner(null);
            }
            
            // Update apartment with image upload support
            apartmentService.updateApartmentWithImage(apartment, imageFile);
            redirectAttributes.addFlashAttribute("success", "Apartment updated successfully!");
            return "redirect:/admin/apartments";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error updating apartment: " + e.getMessage());
            return "redirect:/admin/apartments/" + id + "/edit";
        }
    }
    
    @PostMapping("/apartments/{id}/delete")
    public String deleteApartment(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Optional<Apartment> apartmentOpt = apartmentService.findById(id);
            if (apartmentOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Apartment not found!");
                return "redirect:/admin/apartments";
            }
            
            Apartment apartment = apartmentOpt.get();
            // Delete apartment with image cleanup
            apartmentService.deleteApartmentWithImage(id);
            redirectAttributes.addFlashAttribute("success", "Apartment '" + apartment.getTitle() + "' deleted successfully!");
            return "redirect:/admin/apartments";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error deleting apartment: " + e.getMessage());
            return "redirect:/admin/apartments";
        }
    }
    
    @GetMapping("/agents")
    public String manageAgents(Model model) {
        // Get users with AGENT management role
        List<User> agents = userService.findAllUsers().stream()
                .filter(user -> user.getManagementRoles().contains(ManagementRole.AGENT))
                .collect(Collectors.toList());
        model.addAttribute("agents", agents);
        model.addAttribute("allUsers", userService.findAllUsers());
        return "admin/agents";
    }
    
    @GetMapping("/agents/create")
    public String createAgentForm(Model model) {
        model.addAttribute("agent", new User());
        model.addAttribute("userRoles", UserRole.values());
        return "admin/create-agent";
    }
    
    @PostMapping("/agents/create")
    public String createAgent(@ModelAttribute User agent, RedirectAttributes redirectAttributes) {
        try {
            // Set default role and management role for agents
            agent.setRole(UserRole.CLIENT); // Agents are typically clients with special permissions
            agent.setManagementRoles(Set.of(ManagementRole.AGENT));
            
            userService.saveUser(agent);
            redirectAttributes.addFlashAttribute("success", "Agent created successfully!");
            return "redirect:/admin/agents";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error creating agent: " + e.getMessage());
            return "redirect:/admin/agents/create";
        }
    }
    
    @GetMapping("/agents/{id}")
    public String viewAgent(@PathVariable Long id, Model model) {
        userService.findById(id).ifPresent(agent -> model.addAttribute("agent", agent));
        return "admin/agent-detail";
    }
    
    @GetMapping("/agents/{id}/edit")
    public String editAgent(@PathVariable Long id, Model model) {
        userService.findById(id).ifPresent(agent -> {
            model.addAttribute("agent", agent);
            model.addAttribute("userRoles", UserRole.values());
        });
        return "admin/edit-agent";
    }
    
    @PostMapping("/agents/{id}/update")
    public String updateAgent(@PathVariable Long id, @ModelAttribute User agent, RedirectAttributes redirectAttributes) {
        try {
            // Ensure the agent retains the AGENT management role
            User existingAgent = userService.findById(id).orElseThrow();
            Set<ManagementRole> managementRoles = new HashSet<>(existingAgent.getManagementRoles());
            if (!managementRoles.contains(ManagementRole.AGENT)) {
                managementRoles.add(ManagementRole.AGENT);
            }
            agent.setManagementRoles(managementRoles);
            
            userService.updateUser(agent);
            redirectAttributes.addFlashAttribute("success", "Agent updated successfully!");
            return "redirect:/admin/agents";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error updating agent: " + e.getMessage());
            return "redirect:/admin/agents/" + id + "/edit";
        }
    }
    
    @PostMapping("/agents/{id}/delete")
    public String deleteAgent(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Optional<User> agentOpt = userService.findById(id);
            if (agentOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Agent not found!");
                return "redirect:/admin/agents";
            }
            
            User agent = agentOpt.get();
            // Remove AGENT management role but keep the user
            Set<ManagementRole> managementRoles = new HashSet<>(agent.getManagementRoles());
            managementRoles.remove(ManagementRole.AGENT);
            agent.setManagementRoles(managementRoles);
            userService.updateUser(agent);
            
            redirectAttributes.addFlashAttribute("success", "Agent '" + agent.getUsername() + "' removed from agent role successfully!");
            return "redirect:/admin/agents";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error removing agent: " + e.getMessage());
            return "redirect:/admin/agents";
        }
    }
    
    @GetMapping("/reports")
    public String reports(Model model) {
        model.addAttribute("userStats", userService.getTotalUsers());
        model.addAttribute("apartmentStats", apartmentService.getTotalApartments());
        return "admin/reports";
    }
}

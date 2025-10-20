package com.example.apartmentsalesmanagementsystem.controller;

import com.example.apartmentsalesmanagementsystem.entity.User;
import com.example.apartmentsalesmanagementsystem.entity.ManagementRole;
import com.example.apartmentsalesmanagementsystem.service.ApartmentService;
import com.example.apartmentsalesmanagementsystem.service.UserService;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private ApartmentService apartmentService;
    
    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("totalApartments", apartmentService.getTotalApartments());
        model.addAttribute("availableApartments", apartmentService.getAvailableApartmentsCount());
        model.addAttribute("recentApartments", apartmentService.findAvailableApartments().subList(0, Math.min(6, apartmentService.findAvailableApartments().size())));
        
        // Add agents data for the home page showcase
        List<User> agents = userService.findAllUsers().stream()
                .filter(user -> user.getManagementRoles().contains(ManagementRole.AGENT))
                .limit(3) // Show only first 3 agents on home page
                .collect(Collectors.toList());
        model.addAttribute("agents", agents);
        
        return "home";
    }
    
    @GetMapping("/home")
    public String homePage() {
        return "redirect:/";
    }
    
    @GetMapping("/dashboard")
    public String dashboard() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        
        if (user.isAdmin()) {
            return "redirect:/admin/dashboard";
        } else {
            return "redirect:/client/dashboard";
        }
    }
    
    @GetMapping("/login")
    public String login() {
        return "login";
    }
    
    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }
    
    @GetMapping("/agents")
    public String publicAgents(Model model) {
        // Get all users with AGENT management role for public viewing
        List<User> agents = userService.findAllUsers().stream()
                .filter(user -> user.getManagementRoles().contains(ManagementRole.AGENT))
                .collect(Collectors.toList());
        model.addAttribute("agents", agents);
        return "client/agents";
    }
}

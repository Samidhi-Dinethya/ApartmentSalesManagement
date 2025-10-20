package com.example.apartmentsalesmanagementsystem.service;

import com.example.apartmentsalesmanagementsystem.entity.User;
import com.example.apartmentsalesmanagementsystem.entity.UserRole;
import com.example.apartmentsalesmanagementsystem.entity.Parking;
import com.example.apartmentsalesmanagementsystem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import com.example.apartmentsalesmanagementsystem.entity.ManagementRole;

/**
 * UserService handles user management operations including automatic parking slot assignment
 * for new customers. When a new CLIENT role user is created, the system automatically
 * assigns an available parking slot if one exists.
 */
@Service
public class UserService implements UserDetailsService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private ParkingService parkingService;
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isPresent()) {
            return user.get();
        } else {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
    }
    
    public User saveUser(User user) {
        // Double-check username and email uniqueness before saving
        if (existsByUsername(user.getUsername())) {
            throw new DataIntegrityViolationException("Username already exists: " + user.getUsername());
        }
        
        if (existsByEmail(user.getEmail())) {
            throw new DataIntegrityViolationException("Email already exists: " + user.getEmail());
        }
        
        // Encode password before saving
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        // Save the user first
        User savedUser = userRepository.save(user);
        
        // Automatically assign parking slot to new customers
        if (savedUser.getRole() == UserRole.CLIENT) {
            assignParkingSlotToNewCustomer(savedUser);
        }
        
        return savedUser;
    }
    
    public User updateUser(User user) {
        // Don't re-encode password if it's already encoded (starts with $2a$)
        // This prevents password from being double-encoded on profile updates
        if (user.getPassword() != null && !user.getPassword().startsWith("$2a$")) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        return userRepository.save(user);
    }
    
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }
    
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }
    
    public List<User> findUsersByRole(UserRole role) {
        return userRepository.findByRole(role);
    }
    
    public List<User> findActiveUsers() {
        return userRepository.findByIsActive(true);
    }
    
    public List<User> findActiveUsersByRole(UserRole role) {
        return userRepository.findActiveUsersByRole(role);
    }
    
    public List<User> searchUsers(String searchTerm) {
        return userRepository.searchUsers(searchTerm);
    }
    
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }
    
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
    
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
    
    public void deactivateUser(Long id) {
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setActive(false);
            userRepository.save(user);
        }
    }
    
    public void activateUser(Long id) {
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setActive(true);
            userRepository.save(user);
        }
    }
    
    public long countUsersByRole(UserRole role) {
        return userRepository.countByRole(role);
    }
    
    public long getTotalUsers() {
        return userRepository.count();
    }
    
    public long getActiveUsersCount() {
        return userRepository.findByIsActive(true).size();
    }
    
    public List<User> findUsersByManagementRole(ManagementRole managementRole) {
        return userRepository.findByManagementRolesContaining(managementRole);
    }
    
    /**
     * Clear all users from the database (for development/testing purposes)
     * WARNING: This will delete all user data!
     */
    public void clearAllUsers() {
        userRepository.deleteAll();
    }
    
    /**
     * Automatically assign an available parking slot to a new customer
     * This method safely handles cases where no parking slots are available
     * 
     * @param user The new customer user to assign parking to
     * @return true if parking was successfully assigned, false otherwise
     */
    @Transactional
    public boolean assignParkingSlotToNewCustomer(User user) {
        try {
            // Only assign parking to CLIENT role users
            if (user.getRole() != UserRole.CLIENT) {
                return false;
            }
            
            // Check if user already has a parking slot assigned
            List<Parking> existingParking = parkingService.getParkingSpacesByTenant(user);
            if (!existingParking.isEmpty()) {
                // User already has parking assigned, no need to assign another
                return true;
            }
            
            // Get available parking spaces
            List<Parking> availableSpaces = parkingService.getUnassignedAvailableSpaces();
            
            if (availableSpaces.isEmpty()) {
                // No available parking spaces - this is not an error, just log it
                System.out.println("No available parking spaces for new customer: " + user.getUsername());
                return false;
            }
            
            // Assign the first available parking space
            Parking firstAvailable = availableSpaces.get(0);
            parkingService.assignParkingSpaceToTenant(firstAvailable.getId(), user);
            
            System.out.println("Successfully assigned parking space " + firstAvailable.getSpaceNumber() + 
                             " to new customer: " + user.getUsername());
            return true;
            
        } catch (Exception e) {
            // Log the error but don't fail the user creation process
            System.err.println("Error assigning parking slot to new customer " + user.getUsername() + ": " + e.getMessage());
            return false;
        }
    }
}

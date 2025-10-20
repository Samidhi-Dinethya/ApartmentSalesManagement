package com.example.apartmentsalesmanagementsystem.config;

import com.example.apartmentsalesmanagementsystem.entity.Apartment;
import com.example.apartmentsalesmanagementsystem.entity.ApartmentStatus;
import com.example.apartmentsalesmanagementsystem.entity.User;
import com.example.apartmentsalesmanagementsystem.entity.UserRole;
import com.example.apartmentsalesmanagementsystem.entity.Parking;
import com.example.apartmentsalesmanagementsystem.entity.ParkingType;
import com.example.apartmentsalesmanagementsystem.entity.ParkingStatus;
import com.example.apartmentsalesmanagementsystem.service.ApartmentService;
import com.example.apartmentsalesmanagementsystem.service.UserService;
import com.example.apartmentsalesmanagementsystem.service.ParkingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Arrays;

@Component
public class DataInitializer implements CommandLineRunner {
    
    private static volatile boolean initialized = false;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private ApartmentService apartmentService;
    
    @Autowired
    private ParkingService parkingService;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Override
    public void run(String... args) throws Exception {
        // Prevent multiple initializations
        if (initialized) {
            System.out.println("Data initialization already completed. Skipping.");
            return;
        }
        
        try {
            // Wait a bit for database to be ready
            Thread.sleep(3000);
            
            // Check if database is ready
            if (!isDatabaseReady()) {
                System.out.println("Database not ready. Skipping data initialization.");
                return;
            }
            
            // Check if admin user already exists
            boolean adminExists = userService.existsByUsername("admin");
            System.out.println("Admin user exists: " + adminExists);
            
            if (!adminExists) {
                System.out.println("Admin user not found. Initializing sample data...");
                initializeData();
                System.out.println("Sample data initialized successfully!");
                System.out.println("Admin credentials: admin / admin123");
                System.out.println("Client credentials: john.doe / password123");
            } else {
                System.out.println("Admin user already exists. Skipping data initialization.");
                // Still check if we need to create sample apartments
                checkAndCreateSampleApartments();
            }
            
            // Mark as initialized
            initialized = true;
        } catch (Exception e) {
            System.err.println("Error during data initialization: " + e.getMessage());
            System.err.println("This error will not prevent the application from starting.");
            e.printStackTrace();
        }
    }
    
    private boolean isDatabaseReady() {
        try {
            // Try to get total users count as a database connectivity test
            long userCount = userService.getTotalUsers();
            System.out.println("Database is ready. Current user count: " + userCount);
            return true;
        } catch (Exception e) {
            System.err.println("Database not ready: " + e.getMessage());
            return false;
        }
    }
    
    private boolean createAdminUserWithRetry() {
        for (int attempt = 1; attempt <= 3; attempt++) {
            try {
                // Check again if admin exists before creating
                if (userService.existsByUsername("admin")) {
                    System.out.println("Admin user already exists on attempt " + attempt + ". Skipping creation.");
                    return true;
                }
                
                // Create admin user
                User admin = new User();
                admin.setUsername("admin");
                admin.setEmail("admin@apartmentsales.com");
                admin.setPassword("admin123");
                admin.setFirstName("Admin");
                admin.setLastName("User");
                admin.setPhoneNumber("(555) 123-4567");
                admin.setRole(UserRole.ADMIN);
                admin.setActive(true);
                userService.saveUser(admin);
                System.out.println("Admin user created successfully on attempt " + attempt);
                return true;
            } catch (Exception e) {
                System.err.println("Error creating admin user on attempt " + attempt + ": " + e.getMessage());
                if (attempt < 3) {
                    try {
                        Thread.sleep(2000); // Wait 2 seconds before retry
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }
        }
        return false;
    }
    
    private void initializeData() {
        try {
            // Triple-check if admin user already exists with retry logic
            boolean adminExists = false;
            for (int i = 0; i < 3; i++) {
                try {
                    adminExists = userService.existsByUsername("admin");
                    System.out.println("Check " + (i + 1) + ": Admin user exists: " + adminExists);
                    if (adminExists) {
                        break;
                    }
                    Thread.sleep(1000); // Wait 1 second between checks
                } catch (Exception e) {
                    System.err.println("Error checking admin user existence (attempt " + (i + 1) + "): " + e.getMessage());
                }
            }
            
            if (!adminExists) {
                // Create admin user with retry logic
                boolean adminCreated = createAdminUserWithRetry();
                if (!adminCreated) {
                    System.out.println("Failed to create admin user after retries. Skipping further initialization.");
                    return;
                }
            } else {
                System.out.println("Admin user already exists. Skipping creation.");
                return; // Exit early if admin already exists
            }
        } catch (Exception e) {
            System.err.println("Error creating admin user: " + e.getMessage());
            System.err.println("Stack trace: ");
            e.printStackTrace();
            return; // Exit if there's an error
        }
        
        // Create sample client users
        if (!userService.existsByUsername("john.doe")) {
            User client1 = new User();
            client1.setUsername("john.doe");
            client1.setEmail("john.doe@email.com");
            client1.setPassword("password123");
            client1.setFirstName("John");
            client1.setLastName("Doe");
            client1.setPhoneNumber("(555) 234-5678");
            client1.setRole(UserRole.CLIENT);
            client1.setActive(true);
            userService.saveUser(client1);
            System.out.println("Client user 'john.doe' created successfully.");
        } else {
            System.out.println("Client user 'john.doe' already exists. Skipping creation.");
        }
        
        if (!userService.existsByUsername("jane.smith")) {
            User client2 = new User();
            client2.setUsername("jane.smith");
            client2.setEmail("jane.smith@email.com");
            client2.setPassword("password123");
            client2.setFirstName("Jane");
            client2.setLastName("Smith");
            client2.setPhoneNumber("(555) 345-6789");
            client2.setRole(UserRole.CLIENT);
            client2.setActive(true);
            userService.saveUser(client2);
            System.out.println("Client user 'jane.smith' created successfully.");
        } else {
            System.out.println("Client user 'jane.smith' already exists. Skipping creation.");
        }
        
        // Create sample apartments
        createSampleApartments();
        
        // Create sample parking spaces
        createSampleParkingSpaces();
    }
    
    private void checkAndCreateSampleApartments() {
        try {
            // Check if apartments already exist
            long apartmentCount = apartmentService.getTotalApartments();
            System.out.println("Current apartment count: " + apartmentCount);
            
            if (apartmentCount == 0) {
                System.out.println("No apartments found. Creating sample apartments...");
                createSampleApartments();
                System.out.println("Sample apartments created successfully!");
            } else {
                System.out.println("Apartments already exist. Skipping apartment creation.");
            }
            
            // Check and create parking spaces
            checkAndCreateSampleParkingSpaces();
        } catch (Exception e) {
            System.err.println("Error creating sample apartments: " + e.getMessage());
        }
    }
    
    private void createSampleApartments() {
        // Get existing users for apartment ownership
        User johnDoe = userService.findByUsername("john.doe").orElse(null);
        User janeSmith = userService.findByUsername("jane.smith").orElse(null);
        
        // Create sample apartments only if users exist
        if (johnDoe != null) {
            Apartment apartment1 = new Apartment();
            apartment1.setTitle("Luxury Downtown Apartment");
            apartment1.setDescription("Beautiful 2-bedroom apartment in the heart of downtown with stunning city views. Recently renovated with modern amenities.");
            apartment1.setAddress("123 Main Street");
            apartment1.setCity("New York");
            apartment1.setState("NY");
            apartment1.setZipCode("10001");
            apartment1.setPrice(new BigDecimal("750000"));
            apartment1.setBedrooms(2);
            apartment1.setBathrooms(2);
            apartment1.setSquareFeet(1200);
            apartment1.setStatus(ApartmentStatus.AVAILABLE);
            apartment1.setOwner(johnDoe);
            apartmentService.saveApartment(apartment1);
            System.out.println("Apartment 1 created successfully.");
            
            Apartment apartment2 = new Apartment();
            apartment2.setTitle("Cozy Suburban Home");
            apartment2.setDescription("Charming 3-bedroom apartment in a quiet suburban neighborhood. Perfect for families with excellent schools nearby.");
            apartment2.setAddress("456 Oak Avenue");
            apartment2.setCity("Los Angeles");
            apartment2.setState("CA");
            apartment2.setZipCode("90210");
            apartment2.setPrice(new BigDecimal("650000"));
            apartment2.setBedrooms(3);
            apartment2.setBathrooms(2);
            apartment2.setSquareFeet(1500);
            apartment2.setStatus(ApartmentStatus.AVAILABLE);
            apartment2.setOwner(johnDoe);
            apartmentService.saveApartment(apartment2);
            System.out.println("Apartment 2 created successfully.");
            
            Apartment apartment5 = new Apartment();
            apartment5.setTitle("Historic District Apartment");
            apartment5.setDescription("Charming 2-bedroom apartment in a historic building with original architectural details. Located in a vibrant neighborhood.");
            apartment5.setAddress("654 Heritage Lane");
            apartment5.setCity("Boston");
            apartment5.setState("MA");
            apartment5.setZipCode("02101");
            apartment5.setPrice(new BigDecimal("550000"));
            apartment5.setBedrooms(2);
            apartment5.setBathrooms(1);
            apartment5.setSquareFeet(1100);
            apartment5.setStatus(ApartmentStatus.AVAILABLE);
            apartment5.setOwner(johnDoe);
            apartmentService.saveApartment(apartment5);
            System.out.println("Apartment 5 created successfully.");
        }
        
        if (janeSmith != null) {
            Apartment apartment3 = new Apartment();
            apartment3.setTitle("Modern City View Apartment");
            apartment3.setDescription("Contemporary 1-bedroom apartment with floor-to-ceiling windows and panoramic city views. Ideal for young professionals.");
            apartment3.setAddress("789 Park Boulevard");
            apartment3.setCity("Chicago");
            apartment3.setState("IL");
            apartment3.setZipCode("60601");
            apartment3.setPrice(new BigDecimal("450000"));
            apartment3.setBedrooms(1);
            apartment3.setBathrooms(1);
            apartment3.setSquareFeet(800);
            apartment3.setStatus(ApartmentStatus.AVAILABLE);
            apartment3.setOwner(janeSmith);
            apartmentService.saveApartment(apartment3);
            System.out.println("Apartment 3 created successfully.");
            
            Apartment apartment4 = new Apartment();
            apartment4.setTitle("Waterfront Luxury Apartment");
            apartment4.setDescription("Exclusive 4-bedroom waterfront apartment with private balcony and marina access. Premium location with stunning ocean views.");
            apartment4.setAddress("321 Harbor Drive");
            apartment4.setCity("Miami");
            apartment4.setState("FL");
            apartment4.setZipCode("33101");
            apartment4.setPrice(new BigDecimal("1200000"));
            apartment4.setBedrooms(4);
            apartment4.setBathrooms(3);
            apartment4.setSquareFeet(2200);
            apartment4.setStatus(ApartmentStatus.UNDER_CONTRACT);
            apartment4.setOwner(janeSmith);
            apartmentService.saveApartment(apartment4);
            System.out.println("Apartment 4 created successfully.");
        }
    }
    
    private void checkAndCreateSampleParkingSpaces() {
        try {
            // Check if parking spaces already exist
            long parkingCount = parkingService.getTotalParkingSpaces();
            System.out.println("Current parking space count: " + parkingCount);
            
            if (parkingCount == 0) {
                System.out.println("No parking spaces found. Creating sample parking spaces...");
                createSampleParkingSpaces();
                System.out.println("Sample parking spaces created successfully!");
            } else {
                System.out.println("Parking spaces already exist. Skipping parking space creation.");
            }
        } catch (Exception e) {
            System.err.println("Error creating sample parking spaces: " + e.getMessage());
        }
    }
    
    private void createSampleParkingSpaces() {
        try {
            // Get existing users for parking assignment
            User johnDoe = userService.findByUsername("john.doe").orElse(null);
            User janeSmith = userService.findByUsername("jane.smith").orElse(null);
            
            // Create sample parking spaces
            Parking parking1 = new Parking();
            parking1.setSpaceNumber("P-001");
            parking1.setLocation("Building A, Level 1");
            parking1.setMonthlyFee(new BigDecimal("50.00"));
            parking1.setType(ParkingType.STANDARD);
            parking1.setStatus(ParkingStatus.AVAILABLE);
            parking1.setCovered(false);
            parking1.setElectricCharging(false);
            parking1.setMaxVehicleLength(18.0);
            parking1.setMaxVehicleWidth(8.0);
            parking1.setNotes("Standard parking space near main entrance");
            parkingService.createParkingSpace(parking1);
            System.out.println("Parking space P-001 created successfully.");
            
            Parking parking2 = new Parking();
            parking2.setSpaceNumber("P-002");
            parking2.setLocation("Building A, Level 1");
            parking2.setMonthlyFee(new BigDecimal("60.00"));
            parking2.setType(ParkingType.LARGE);
            parking2.setStatus(ParkingStatus.OCCUPIED);
            parking2.setCovered(true);
            parking2.setElectricCharging(false);
            parking2.setMaxVehicleLength(22.0);
            parking2.setMaxVehicleWidth(9.0);
            parking2.setNotes("Covered large parking space");
            if (johnDoe != null) {
                parking2.setAssignedTenant(johnDoe);
            }
            parkingService.createParkingSpace(parking2);
            System.out.println("Parking space P-002 created successfully.");
            
            Parking parking3 = new Parking();
            parking3.setSpaceNumber("P-003");
            parking3.setLocation("Building A, Level 2");
            parking3.setMonthlyFee(new BigDecimal("75.00"));
            parking3.setType(ParkingType.ELECTRIC);
            parking3.setStatus(ParkingStatus.OCCUPIED);
            parking3.setCovered(true);
            parking3.setElectricCharging(true);
            parking3.setMaxVehicleLength(18.0);
            parking3.setMaxVehicleWidth(8.0);
            parking3.setNotes("Electric vehicle charging station");
            if (janeSmith != null) {
                parking3.setAssignedTenant(janeSmith);
            }
            parkingService.createParkingSpace(parking3);
            System.out.println("Parking space P-003 created successfully.");
            
            Parking parking4 = new Parking();
            parking4.setSpaceNumber("P-004");
            parking4.setLocation("Building B, Level 1");
            parking4.setMonthlyFee(new BigDecimal("40.00"));
            parking4.setType(ParkingType.COMPACT);
            parking4.setStatus(ParkingStatus.AVAILABLE);
            parking4.setCovered(false);
            parking4.setElectricCharging(false);
            parking4.setMaxVehicleLength(15.0);
            parking4.setMaxVehicleWidth(7.0);
            parking4.setNotes("Compact parking space for small vehicles");
            parkingService.createParkingSpace(parking4);
            System.out.println("Parking space P-004 created successfully.");
            
            Parking parking5 = new Parking();
            parking5.setSpaceNumber("P-005");
            parking5.setLocation("Building B, Level 1");
            parking5.setMonthlyFee(new BigDecimal("45.00"));
            parking5.setType(ParkingType.HANDICAP);
            parking5.setStatus(ParkingStatus.AVAILABLE);
            parking5.setCovered(false);
            parking5.setElectricCharging(false);
            parking5.setMaxVehicleLength(20.0);
            parking5.setMaxVehicleWidth(9.0);
            parking5.setNotes("Handicap accessible parking space");
            parkingService.createParkingSpace(parking5);
            System.out.println("Parking space P-005 created successfully.");
            
            Parking parking6 = new Parking();
            parking6.setSpaceNumber("P-006");
            parking6.setLocation("Building C, Level 1");
            parking6.setMonthlyFee(new BigDecimal("30.00"));
            parking6.setType(ParkingType.MOTORCYCLE);
            parking6.setStatus(ParkingStatus.AVAILABLE);
            parking6.setCovered(false);
            parking6.setElectricCharging(false);
            parking6.setMaxVehicleLength(8.0);
            parking6.setMaxVehicleWidth(4.0);
            parking6.setNotes("Motorcycle parking space");
            parkingService.createParkingSpace(parking6);
            System.out.println("Parking space P-006 created successfully.");
            
            Parking parking7 = new Parking();
            parking7.setSpaceNumber("P-007");
            parking7.setLocation("Building C, Level 2");
            parking7.setMonthlyFee(new BigDecimal("100.00"));
            parking7.setType(ParkingType.PREMIUM);
            parking7.setStatus(ParkingStatus.RESERVED);
            parking7.setCovered(true);
            parking7.setElectricCharging(true);
            parking7.setMaxVehicleLength(25.0);
            parking7.setMaxVehicleWidth(10.0);
            parking7.setNotes("Premium covered parking with electric charging");
            parkingService.createParkingSpace(parking7);
            System.out.println("Parking space P-007 created successfully.");
            
            Parking parking8 = new Parking();
            parking8.setSpaceNumber("P-008");
            parking8.setLocation("Building A, Level 2");
            parking8.setMonthlyFee(new BigDecimal("55.00"));
            parking8.setType(ParkingType.STANDARD);
            parking8.setStatus(ParkingStatus.MAINTENANCE);
            parking8.setCovered(false);
            parking8.setElectricCharging(false);
            parking8.setMaxVehicleLength(18.0);
            parking8.setMaxVehicleWidth(8.0);
            parking8.setNotes("Under maintenance - lighting repair");
            parkingService.createParkingSpace(parking8);
            System.out.println("Parking space P-008 created successfully.");
            
        } catch (Exception e) {
            System.err.println("Error creating sample parking spaces: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

-- ===========================================
-- MS SQL Server Database Setup Script
-- Skyline Apartments System
-- ===========================================

-- Create the database (if it doesn't exist)
IF NOT EXISTS (SELECT name FROM sys.databases WHERE name = 'apartment_sales_db')
BEGIN
    CREATE DATABASE apartment_sales_db;
    PRINT 'Database apartment_sales_db created successfully.';
END
ELSE
BEGIN
    PRINT 'Database apartment_sales_db already exists.';
END
GO

USE apartment_sales_db;
GO

-- ===========================================
-- Create Tables
-- ===========================================

-- Create users table
IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='users' AND xtype='U')
BEGIN
    CREATE TABLE users (
        id BIGINT IDENTITY(1,1) PRIMARY KEY,
        username NVARCHAR(50) NOT NULL UNIQUE,
        email NVARCHAR(255) NOT NULL UNIQUE,
        password NVARCHAR(255) NOT NULL,
        first_name NVARCHAR(255) NOT NULL,
        last_name NVARCHAR(255) NOT NULL,
        phone_number NVARCHAR(255),
        role NVARCHAR(20) NOT NULL CHECK (role IN ('ADMIN', 'CLIENT')),
        is_active BIT DEFAULT 1,
        created_at DATETIME2 DEFAULT GETDATE(),
        updated_at DATETIME2 DEFAULT GETDATE()
    );
    PRINT 'Table users created successfully.';
END
ELSE
BEGIN
    PRINT 'Table users already exists.';
END
GO

-- Create user_management_roles table (for ElementCollection)
IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='user_management_roles' AND xtype='U')
BEGIN
    CREATE TABLE user_management_roles (
        user_id BIGINT NOT NULL,
        management_role NVARCHAR(50) NOT NULL,
        PRIMARY KEY (user_id, management_role),
        FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
    );
    PRINT 'Table user_management_roles created successfully.';
END
ELSE
BEGIN
    PRINT 'Table user_management_roles already exists.';
END
GO

-- Create apartments table
IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='apartments' AND xtype='U')
BEGIN
    CREATE TABLE apartments (
        id BIGINT IDENTITY(1,1) PRIMARY KEY,
        title NVARCHAR(255) NOT NULL,
        description NVARCHAR(MAX) NOT NULL,
        address NVARCHAR(255) NOT NULL,
        city NVARCHAR(255) NOT NULL,
        state NVARCHAR(255) NOT NULL,
        zip_code NVARCHAR(255) NOT NULL,
        price DECIMAL(10,2) NOT NULL,
        bedrooms INT NOT NULL,
        bathrooms INT NOT NULL,
        square_feet INT NOT NULL,
        status NVARCHAR(20) NOT NULL CHECK (status IN ('AVAILABLE', 'UNDER_CONTRACT', 'SOLD', 'RENTED')),
        image_url NVARCHAR(255),
        owner_id BIGINT,
        created_at DATETIME2 DEFAULT GETDATE(),
        updated_at DATETIME2 DEFAULT GETDATE(),
        FOREIGN KEY (owner_id) REFERENCES users(id)
    );
    PRINT 'Table apartments created successfully.';
END
ELSE
BEGIN
    PRINT 'Table apartments already exists.';
END
GO

-- Create parking_spaces table
IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='parking_spaces' AND xtype='U')
BEGIN
    CREATE TABLE parking_spaces (
        id BIGINT IDENTITY(1,1) PRIMARY KEY,
        space_number NVARCHAR(50) NOT NULL UNIQUE,
        location NVARCHAR(255) NOT NULL,
        monthly_fee DECIMAL(10,2) NOT NULL,
        type NVARCHAR(20) NOT NULL CHECK (type IN ('STANDARD', 'PREMIUM', 'COMPACT', 'MOTORCYCLE')),
        status NVARCHAR(20) NOT NULL CHECK (status IN ('AVAILABLE', 'OCCUPIED', 'RESERVED', 'MAINTENANCE')),
        is_covered BIT DEFAULT 0,
        is_electric_charging BIT DEFAULT 0,
        max_vehicle_length FLOAT,
        max_vehicle_width FLOAT,
        notes NVARCHAR(MAX),
        assigned_tenant_id BIGINT,
        created_at DATETIME2 DEFAULT GETDATE(),
        updated_at DATETIME2 DEFAULT GETDATE(),
        FOREIGN KEY (assigned_tenant_id) REFERENCES users(id)
    );
    PRINT 'Table parking_spaces created successfully.';
END
ELSE
BEGIN
    PRINT 'Table parking_spaces already exists.';
END
GO

-- Create appointments table
IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='appointments' AND xtype='U')
BEGIN
    CREATE TABLE appointments (
        id BIGINT IDENTITY(1,1) PRIMARY KEY,
        appointment_datetime DATETIME2 NOT NULL,
        notes NVARCHAR(MAX) NOT NULL,
        status NVARCHAR(20) NOT NULL CHECK (status IN ('SCHEDULED', 'CONFIRMED', 'COMPLETED', 'CANCELLED', 'NO_SHOW')),
        client_id BIGINT NOT NULL,
        agent_id BIGINT NOT NULL,
        apartment_id BIGINT NOT NULL,
        created_at DATETIME2 DEFAULT GETDATE(),
        updated_at DATETIME2 DEFAULT GETDATE(),
        cancellation_reason NVARCHAR(MAX),
        feedback_rating INT CHECK (feedback_rating >= 1 AND feedback_rating <= 5),
        feedback_comment NVARCHAR(MAX),
        FOREIGN KEY (client_id) REFERENCES users(id),
        FOREIGN KEY (agent_id) REFERENCES users(id),
        FOREIGN KEY (apartment_id) REFERENCES apartments(id)
    );
    PRINT 'Table appointments created successfully.';
END
ELSE
BEGIN
    PRINT 'Table appointments already exists.';
END
GO

-- Create appointment_requests table
IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='appointment_requests' AND xtype='U')
BEGIN
    CREATE TABLE appointment_requests (
        id BIGINT IDENTITY(1,1) PRIMARY KEY,
        client_name NVARCHAR(255) NOT NULL,
        email NVARCHAR(255) NOT NULL,
        phone_number NVARCHAR(255),
        preferred_date NVARCHAR(255) NOT NULL,
        preferred_time NVARCHAR(255) NOT NULL,
        message NVARCHAR(MAX) NOT NULL,
        status NVARCHAR(20) NOT NULL CHECK (status IN ('PENDING', 'APPROVED', 'REJECTED', 'COMPLETED')),
        apartment_id BIGINT,
        client_id BIGINT,
        assigned_agent_id BIGINT,
        admin_reply NVARCHAR(MAX),
        created_at DATETIME2 DEFAULT GETDATE(),
        updated_at DATETIME2 DEFAULT GETDATE(),
        replied_at DATETIME2,
        FOREIGN KEY (apartment_id) REFERENCES apartments(id),
        FOREIGN KEY (client_id) REFERENCES users(id),
        FOREIGN KEY (assigned_agent_id) REFERENCES users(id)
    );
    PRINT 'Table appointment_requests created successfully.';
END
ELSE
BEGIN
    PRINT 'Table appointment_requests already exists.';
END
GO

-- Create reviews table
IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='reviews' AND xtype='U')
BEGIN
    CREATE TABLE reviews (
        id BIGINT IDENTITY(1,1) PRIMARY KEY,
        rating INT NOT NULL CHECK (rating >= 1 AND rating <= 5),
        comment NVARCHAR(MAX),
        reviewer_id BIGINT NOT NULL,
        apartment_id BIGINT NOT NULL,
        created_at DATETIME2 DEFAULT GETDATE(),
        updated_at DATETIME2 DEFAULT GETDATE(),
        FOREIGN KEY (reviewer_id) REFERENCES users(id),
        FOREIGN KEY (apartment_id) REFERENCES apartments(id)
    );
    PRINT 'Table reviews created successfully.';
END
ELSE
BEGIN
    PRINT 'Table reviews already exists.';
END
GO

-- ===========================================
-- Create Indexes for Performance
-- ===========================================

-- Indexes for users table
CREATE NONCLUSTERED INDEX IX_users_username ON users(username);
CREATE NONCLUSTERED INDEX IX_users_email ON users(email);
CREATE NONCLUSTERED INDEX IX_users_role ON users(role);
CREATE NONCLUSTERED INDEX IX_users_is_active ON users(is_active);

-- Indexes for apartments table
CREATE NONCLUSTERED INDEX IX_apartments_status ON apartments(status);
CREATE NONCLUSTERED INDEX IX_apartments_city ON apartments(city);
CREATE NONCLUSTERED INDEX IX_apartments_price ON apartments(price);
CREATE NONCLUSTERED INDEX IX_apartments_owner_id ON apartments(owner_id);

-- Indexes for appointments table
CREATE NONCLUSTERED INDEX IX_appointments_client_id ON appointments(client_id);
CREATE NONCLUSTERED INDEX IX_appointments_agent_id ON appointments(agent_id);
CREATE NONCLUSTERED INDEX IX_appointments_apartment_id ON appointments(apartment_id);
CREATE NONCLUSTERED INDEX IX_appointments_datetime ON appointments(appointment_datetime);
CREATE NONCLUSTERED INDEX IX_appointments_status ON appointments(status);

-- Indexes for parking_spaces table
CREATE NONCLUSTERED INDEX IX_parking_spaces_status ON parking_spaces(status);
CREATE NONCLUSTERED INDEX IX_parking_spaces_type ON parking_spaces(type);
CREATE NONCLUSTERED INDEX IX_parking_spaces_assigned_tenant ON parking_spaces(assigned_tenant_id);

-- Indexes for appointment_requests table
CREATE NONCLUSTERED INDEX IX_appointment_requests_status ON appointment_requests(status);
CREATE NONCLUSTERED INDEX IX_appointment_requests_apartment_id ON appointment_requests(apartment_id);
CREATE NONCLUSTERED INDEX IX_appointment_requests_assigned_agent ON appointment_requests(assigned_agent_id);

-- Indexes for reviews table
CREATE NONCLUSTERED INDEX IX_reviews_apartment_id ON reviews(apartment_id);
CREATE NONCLUSTERED INDEX IX_reviews_reviewer_id ON reviews(reviewer_id);
CREATE NONCLUSTERED INDEX IX_reviews_rating ON reviews(rating);

PRINT 'All indexes created successfully.';
GO

-- ===========================================
-- Insert Sample Data
-- ===========================================

-- Insert test users with BCrypt encoded passwords
-- Password: admin123 (BCrypt encoded)
IF NOT EXISTS (SELECT 1 FROM users WHERE username = 'admin')
BEGIN
    INSERT INTO users (username, email, password, first_name, last_name, phone_number, role, is_active)
    VALUES ('admin', 'admin@apartmentsales.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', 'Admin', 'User', '(555) 123-4567', 'ADMIN', 1);
    PRINT 'Admin user created.';
END

-- Password: password123 (BCrypt encoded)
IF NOT EXISTS (SELECT 1 FROM users WHERE username = 'john.doe')
BEGIN
    INSERT INTO users (username, email, password, first_name, last_name, phone_number, role, is_active)
    VALUES ('john.doe', 'john.doe@email.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', 'John', 'Doe', '(555) 234-5678', 'CLIENT', 1);
    PRINT 'Client user john.doe created.';
END

IF NOT EXISTS (SELECT 1 FROM users WHERE username = 'jane.smith')
BEGIN
    INSERT INTO users (username, email, password, first_name, last_name, phone_number, role, is_active)
    VALUES ('jane.smith', 'jane.smith@email.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', 'Jane', 'Smith', '(555) 345-6789', 'CLIENT', 1);
    PRINT 'Client user jane.smith created.';
END

-- Create a sample agent user
IF NOT EXISTS (SELECT 1 FROM users WHERE username = 'agent.mike')
BEGIN
    INSERT INTO users (username, email, password, first_name, last_name, phone_number, role, is_active)
    VALUES ('agent.mike', 'mike.agent@apartmentsales.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', 'Mike', 'Johnson', '(555) 456-7890', 'CLIENT', 1);
    
    -- Add AGENT management role
    INSERT INTO user_management_roles (user_id, management_role)
    VALUES ((SELECT id FROM users WHERE username = 'agent.mike'), 'AGENT');
    
    PRINT 'Agent user mike.johnson created.';
END

-- Insert sample apartments
IF NOT EXISTS (SELECT 1 FROM apartments WHERE title = 'Luxury Downtown Apartment')
BEGIN
    INSERT INTO apartments (title, description, address, city, state, zip_code, price, bedrooms, bathrooms, square_feet, status, owner_id)
    VALUES ('Luxury Downtown Apartment', 'Beautiful 2-bedroom apartment in the heart of downtown with stunning city views.', '123 Main Street', 'New York', 'NY', '10001', 750000.00, 2, 2, 1200, 'AVAILABLE', (SELECT id FROM users WHERE username = 'john.doe'));
    PRINT 'Sample apartment 1 created.';
END

IF NOT EXISTS (SELECT 1 FROM apartments WHERE title = 'Cozy Suburban Home')
BEGIN
    INSERT INTO apartments (title, description, address, city, state, zip_code, price, bedrooms, bathrooms, square_feet, status, owner_id)
    VALUES ('Cozy Suburban Home', 'Charming 3-bedroom apartment in a quiet suburban neighborhood.', '456 Oak Avenue', 'Los Angeles', 'CA', '90210', 650000.00, 3, 2, 1500, 'AVAILABLE', (SELECT id FROM users WHERE username = 'john.doe'));
    PRINT 'Sample apartment 2 created.';
END

IF NOT EXISTS (SELECT 1 FROM apartments WHERE title = 'Modern City View Apartment')
BEGIN
    INSERT INTO apartments (title, description, address, city, state, zip_code, price, bedrooms, bathrooms, square_feet, status, owner_id)
    VALUES ('Modern City View Apartment', 'Contemporary 1-bedroom apartment with floor-to-ceiling windows.', '789 Park Boulevard', 'Chicago', 'IL', '60601', 450000.00, 1, 1, 800, 'AVAILABLE', (SELECT id FROM users WHERE username = 'jane.smith'));
    PRINT 'Sample apartment 3 created.';
END

-- Insert sample parking spaces
IF NOT EXISTS (SELECT 1 FROM parking_spaces WHERE space_number = 'P001')
BEGIN
    INSERT INTO parking_spaces (space_number, location, monthly_fee, type, status, is_covered, is_electric_charging, max_vehicle_length, max_vehicle_width, notes)
    VALUES ('P001', 'Ground Floor - Near Main Entrance', 150.00, 'STANDARD', 'AVAILABLE', 1, 0, 18.0, 8.0, 'Covered parking space with easy access');
    PRINT 'Sample parking space 1 created.';
END

IF NOT EXISTS (SELECT 1 FROM parking_spaces WHERE space_number = 'P002')
BEGIN
    INSERT INTO parking_spaces (space_number, location, monthly_fee, type, status, is_covered, is_electric_charging, max_vehicle_length, max_vehicle_width, notes)
    VALUES ('P002', 'Basement Level - Electric Charging Station', 200.00, 'PREMIUM', 'AVAILABLE', 1, 1, 20.0, 9.0, 'Premium space with electric vehicle charging');
    PRINT 'Sample parking space 2 created.';
END

-- ===========================================
-- Create Stored Procedures (Optional)
-- ===========================================

-- Procedure to get user statistics
IF EXISTS (SELECT * FROM sys.objects WHERE type = 'P' AND name = 'sp_GetUserStatistics')
    DROP PROCEDURE sp_GetUserStatistics;
GO

CREATE PROCEDURE sp_GetUserStatistics
AS
BEGIN
    SELECT 
        COUNT(*) as total_users,
        SUM(CASE WHEN role = 'ADMIN' THEN 1 ELSE 0 END) as admin_count,
        SUM(CASE WHEN role = 'CLIENT' THEN 1 ELSE 0 END) as client_count,
        SUM(CASE WHEN is_active = 1 THEN 1 ELSE 0 END) as active_users
    FROM users;
END
GO

-- Procedure to get apartment statistics
IF EXISTS (SELECT * FROM sys.objects WHERE type = 'P' AND name = 'sp_GetApartmentStatistics')
    DROP PROCEDURE sp_GetApartmentStatistics;
GO

CREATE PROCEDURE sp_GetApartmentStatistics
AS
BEGIN
    SELECT 
        COUNT(*) as total_apartments,
        SUM(CASE WHEN status = 'AVAILABLE' THEN 1 ELSE 0 END) as available_count,
        SUM(CASE WHEN status = 'SOLD' THEN 1 ELSE 0 END) as sold_count,
        AVG(price) as average_price
    FROM apartments;
END
GO

PRINT 'Stored procedures created successfully.';
GO

-- ===========================================
-- Final Setup Summary
-- ===========================================

PRINT '===========================================';
PRINT 'MS SQL Server Database Setup Completed!';
PRINT '===========================================';
PRINT 'Database: apartment_sales_db';
PRINT 'Tables Created: users, user_management_roles, apartments, parking_spaces, appointments, appointment_requests, reviews';
PRINT 'Indexes: Performance indexes created for all major tables';
PRINT 'Sample Data: Test users and apartments inserted';
PRINT 'Stored Procedures: User and apartment statistics procedures created';
PRINT '';
PRINT 'Test Credentials:';
PRINT 'Admin: admin / admin123';
PRINT 'Client: john.doe / password123';
PRINT 'Client: jane.smith / password123';
PRINT 'Agent: agent.mike / password123';
PRINT '';
PRINT 'Next Steps:';
PRINT '1. Update application.properties with your SQL Server credentials';
PRINT '2. Uncomment MS SQL Server configuration lines';
PRINT '3. Comment out H2 configuration lines';
PRINT '4. Restart your Spring Boot application';
PRINT '===========================================';

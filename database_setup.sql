2025-08-16T19:38:06.858+05:30 ERROR 29008 --- [nio-8083-exec-4] o.h.engine.jdbc.spi.SqlExceptionHelper   : Unique index or primary key violation: "PUBLIC.CONSTRAINT_INDEX_4 ON PUBLIC.USERS(USERNAME NULLS FIRST) VALUES ( /* 1 */ 'admin' )"; SQL statement:
insert into users (created_at,email,first_name,is_active,last_name,password,phone_number,role,updated_at,username,id) values (?,?,?,?,?,?,?,?,?,?,default) [23505-232]-- Create the database (if it doesn't exist)
IF NOT EXISTS (SELECT name FROM sys.databases WHERE name = 'apartment_sales_db')
BEGIN
    CREATE DATABASE apartment_sales_db;
END
GO

USE apartment_sales_db;
GO

-- Create users table
CREATE TABLE users (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    phone_number VARCHAR(255),
    role VARCHAR(20) NOT NULL CHECK (role IN ('ADMIN', 'CLIENT')),
    is_active BIT DEFAULT 1,
    created_at DATETIME2 DEFAULT GETDATE(),
    updated_at DATETIME2 DEFAULT GETDATE()
);

-- Create apartments table
CREATE TABLE apartments (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    address VARCHAR(255) NOT NULL,
    city VARCHAR(255) NOT NULL,
    state VARCHAR(255) NOT NULL,
    zip_code VARCHAR(255) NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    bedrooms INT NOT NULL,
    bathrooms INT NOT NULL,
    square_feet INT NOT NULL,
    status VARCHAR(20) NOT NULL CHECK (status IN ('AVAILABLE', 'UNDER_CONTRACT', 'SOLD', 'RENTED')),
    image_url VARCHAR(255),
    owner_id BIGINT,
    created_at DATETIME2 DEFAULT GETDATE(),
    updated_at DATETIME2 DEFAULT GETDATE(),
    FOREIGN KEY (owner_id) REFERENCES users(id)
);

-- Insert test users with BCrypt encoded passwords
-- Password: admin123 (BCrypt encoded)
INSERT INTO users (username, email, password, first_name, last_name, phone_number, role, is_active)
VALUES ('admin', 'admin@apartmentsales.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', 'Admin', 'User', '(555) 123-4567', 'ADMIN', 1);

-- Password: password123 (BCrypt encoded)
INSERT INTO users (username, email, password, first_name, last_name, phone_number, role, is_active)
VALUES ('john.doe', 'john.doe@email.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', 'John', 'Doe', '(555) 234-5678', 'CLIENT', 1);

INSERT INTO users (username, email, password, first_name, last_name, phone_number, role, is_active)
VALUES ('jane.smith', 'jane.smith@email.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', 'Jane', 'Smith', '(555) 345-6789', 'CLIENT', 1);

-- Insert sample apartments
INSERT INTO apartments (title, description, address, city, state, zip_code, price, bedrooms, bathrooms, square_feet, status, owner_id)
VALUES ('Luxury Downtown Apartment', 'Beautiful 2-bedroom apartment in the heart of downtown with stunning city views.', '123 Main Street', 'New York', 'NY', '10001', 750000.00, 2, 2, 1200, 'AVAILABLE', 2);

INSERT INTO apartments (title, description, address, city, state, zip_code, price, bedrooms, bathrooms, square_feet, status, owner_id)
VALUES ('Cozy Suburban Home', 'Charming 3-bedroom apartment in a quiet suburban neighborhood.', '456 Oak Avenue', 'Los Angeles', 'CA', '90210', 650000.00, 3, 2, 1500, 'AVAILABLE', 2);

INSERT INTO apartments (title, description, address, city, state, zip_code, price, bedrooms, bathrooms, square_feet, status, owner_id)
VALUES ('Modern City View Apartment', 'Contemporary 1-bedroom apartment with floor-to-ceiling windows.', '789 Park Boulevard', 'Chicago', 'IL', '60601', 450000.00, 1, 1, 800, 'AVAILABLE', 3);

PRINT 'Database setup completed successfully!';
PRINT 'Test credentials:';
PRINT 'Admin: admin / admin123';
PRINT 'Client: john.doe / password123';
PRINT 'Client: jane.smith / password123';

# Database Setup Guide - MS SQL Server Integration

## Overview
This guide will help you safely connect your Skyline Apartments System to MS SQL Server database while maintaining the existing H2 development environment.

## Prerequisites
- MS SQL Server 2019 or later (Express, Standard, or Enterprise)
- SQL Server Management Studio (SSMS) or Azure Data Studio
- Java 17 or higher
- Maven 3.6 or higher

## Step 1: MS SQL Server Installation & Configuration

### Install MS SQL Server
1. Download SQL Server from Microsoft's official website
2. Install with default settings or customize as needed
3. Enable SQL Server Authentication (Mixed Mode)
4. Note down your server name, port (default: 1433), and credentials

### Create Database User (Recommended)
```sql
-- Connect to SQL Server as administrator
-- Create a dedicated user for the application
CREATE LOGIN apartment_app_user WITH PASSWORD = 'YourSecurePassword123!';
USE apartment_sales_db;
CREATE USER apartment_app_user FOR LOGIN apartment_app_user;
ALTER ROLE db_owner ADD MEMBER apartment_app_user;
```

## Step 2: Database Setup

### Run the Setup Script
1. Open SQL Server Management Studio (SSMS)
2. Connect to your SQL Server instance
3. Open and execute the `mssql_database_setup.sql` file
4. Verify that all tables, indexes, and sample data are created

### Verify Setup
```sql
-- Check if database exists
SELECT name FROM sys.databases WHERE name = 'apartment_sales_db';

-- Check tables
USE apartment_sales_db;
SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_TYPE = 'BASE TABLE';

-- Check sample data
SELECT COUNT(*) as user_count FROM users;
SELECT COUNT(*) as apartment_count FROM apartments;
```

## Step 3: Application Configuration

### Update application.properties
1. Open `src/main/resources/application.properties`
2. **For Production (MS SQL Server):**
   - Uncomment the MS SQL Server configuration lines (lines 6-9)
   - Update the connection details:
     ```properties
     spring.datasource.url=jdbc:sqlserver://YOUR_SERVER:1433;databaseName=apartment_sales_db;encrypt=true;trustServerCertificate=true
     spring.datasource.username=apartment_app_user
     spring.datasource.password=YourSecurePassword123!
     ```
   - Uncomment the MS SQL Server JPA configuration (lines 35-39)
   - Comment out the H2 configuration lines (lines 15-18, 27-30)

3. **For Development (H2):**
   - Keep H2 configuration active
   - Keep MS SQL Server configuration commented out

### Example Production Configuration
```properties
# PRODUCTION: MS SQL Server Configuration
spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=apartment_sales_db;encrypt=true;trustServerCertificate=true
spring.datasource.driver-class-name=com.microsoft.sqlserver.jdbc.SQLServerDriver
spring.datasource.username=apartment_app_user
spring.datasource.password=YourSecurePassword123!

# JPA Configuration for MS SQL Server
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.SQLServerDialect
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.properties.hibernate.hbm2ddl.auto=update
```

## Step 4: Build and Test

### Build the Application
```bash
# Clean and build the project
mvn clean compile

# Run the application
mvn spring-boot:run
```

### Test Database Connection
1. Start the application
2. Check the console logs for successful database connection
3. Access the application at `http://localhost:8083`
4. Login with test credentials:
   - Admin: `admin` / `admin123`
   - Client: `john.doe` / `password123`
   - Agent: `agent.mike` / `password123`

## Step 5: Verification Checklist

### ✅ Database Connection
- [ ] Application starts without database connection errors
- [ ] No Hibernate dialect warnings in logs
- [ ] Database tables are created/updated successfully

### ✅ Functionality Testing
- [ ] User login works correctly
- [ ] Admin dashboard loads with statistics
- [ ] Apartment management functions work
- [ ] Agent management functions work
- [ ] Appointment scheduling works
- [ ] Parking space management works

### ✅ Data Integrity
- [ ] Sample data is accessible
- [ ] User roles and permissions work
- [ ] Foreign key relationships are maintained
- [ ] Indexes are created for performance

## Troubleshooting

### Common Issues

#### Connection Refused
```
Error: Connection refused
Solution: 
- Verify SQL Server is running
- Check firewall settings (port 1433)
- Verify server name and port
```

#### Authentication Failed
```
Error: Login failed for user
Solution:
- Verify username and password
- Check if user has database permissions
- Ensure SQL Server Authentication is enabled
```

#### Dialect Issues
```
Error: Unknown database dialect
Solution:
- Ensure MS SQL Server dependency is in pom.xml
- Verify dialect configuration in application.properties
```

#### Table Creation Issues
```
Error: Table already exists
Solution:
- Use `spring.jpa.hibernate.ddl-auto=update` instead of `create-drop`
- Or drop existing tables manually
```

### Performance Optimization

#### Connection Pooling
Add to `application.properties`:
```properties
# Connection Pool Settings
spring.datasource.hikari.maximum-pool-size=20
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.connection-timeout=30000
spring.datasource.hikari.idle-timeout=600000
spring.datasource.hikari.max-lifetime=1800000
```

#### Query Optimization
- Indexes are automatically created by the setup script
- Monitor slow queries using SQL Server Profiler
- Consider adding composite indexes for complex queries

## Security Considerations

### Database Security
1. **Use dedicated database user** (not sa account)
2. **Enable encryption** in connection string
3. **Use strong passwords** for database accounts
4. **Limit database user permissions** to only required operations
5. **Enable SQL Server audit logging**

### Application Security
1. **Store database credentials** in environment variables or secure configuration
2. **Use connection encryption** (already configured)
3. **Regular security updates** for SQL Server and JDBC driver
4. **Monitor database access logs**

## Environment-Specific Configuration

### Development Environment
- Use H2 in-memory database for quick testing
- Enable SQL logging for debugging
- Use `create-drop` for schema management

### Production Environment
- Use MS SQL Server with proper connection pooling
- Disable SQL logging for performance
- Use `update` for schema management
- Implement proper backup and recovery procedures

## Backup and Recovery

### Database Backup
```sql
-- Full database backup
BACKUP DATABASE apartment_sales_db 
TO DISK = 'C:\Backups\apartment_sales_db_full.bak'
WITH FORMAT, INIT, NAME = 'Full Backup of apartment_sales_db';

-- Transaction log backup (for point-in-time recovery)
BACKUP LOG apartment_sales_db 
TO DISK = 'C:\Backups\apartment_sales_db_log.trn';
```

### Recovery
```sql
-- Restore database
RESTORE DATABASE apartment_sales_db 
FROM DISK = 'C:\Backups\apartment_sales_db_full.bak'
WITH REPLACE;
```

## Support and Maintenance

### Regular Maintenance Tasks
1. **Monitor database size** and growth
2. **Update statistics** for query optimization
3. **Check index fragmentation** and rebuild if needed
4. **Review and clean up** old appointment and request data
5. **Monitor application logs** for database-related errors

### Monitoring Queries
```sql
-- Check database size
SELECT 
    DB_NAME() AS DatabaseName,
    SUM(CAST(FILEPROPERTY(name, 'SpaceUsed') AS bigint) * 8192.) / 1024 / 1024 AS UsedSpaceMB,
    SUM(CAST(size AS bigint) * 8192.) / 1024 / 1024 AS TotalSpaceMB
FROM sys.database_files;

-- Check active connections
SELECT 
    session_id,
    login_name,
    host_name,
    program_name,
    login_time,
    last_request_start_time
FROM sys.dm_exec_sessions 
WHERE database_id = DB_ID('apartment_sales_db');
```

## Conclusion

Your Skyline Apartments System is now successfully configured to work with MS SQL Server while maintaining the flexibility to switch back to H2 for development. The setup includes:

- ✅ Complete database schema with all entities
- ✅ Performance indexes for optimal query execution
- ✅ Sample data for testing
- ✅ Stored procedures for common operations
- ✅ Comprehensive security configuration
- ✅ Backup and recovery procedures

The system is production-ready and can handle real-world apartment sales management operations with proper scalability and performance.

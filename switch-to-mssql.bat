@echo off
echo ===========================================
echo Switching to MS SQL Server Configuration
echo ===========================================

REM Backup current configuration
copy "src\main\resources\application.properties" "src\main\resources\application.properties.backup"

REM Create MS SQL Server configuration
(
echo # Database Configuration
echo # ===========================================
echo # PRODUCTION: MS SQL Server Configuration
echo # ===========================================
echo spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=apartment_sales_db;encrypt=true;trustServerCertificate=true
echo spring.datasource.driver-class-name=com.microsoft.sqlserver.jdbc.SQLServerDriver
echo spring.datasource.username=apartment_app_user
echo spring.datasource.password=YourSecurePassword123!
echo.
echo # ===========================================
echo # DEVELOPMENT: H2 In-Memory Configuration
echo # ===========================================
echo # Current configuration for development/testing
echo # spring.datasource.url=jdbc:h2:mem:apartment_sales_db
echo # spring.datasource.driver-class-name=org.h2.Driver
echo # spring.datasource.username=sa
echo # spring.datasource.password=password
echo.
echo # H2 Console (for development)
echo # spring.h2.console.enabled=true
echo # spring.h2.console.path=/h2-console
echo.
echo # JPA Configuration
echo # ===========================================
echo # For H2 (Development)
echo # spring.jpa.hibernate.ddl-auto=create-drop
echo # spring.jpa.show-sql=true
echo # spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect
echo # spring.jpa.properties.hibernate.format_sql=true
echo.
echo # ===========================================
echo # For MS SQL Server (Production) - Active
echo # ===========================================
echo spring.jpa.hibernate.ddl-auto=update
echo spring.jpa.show-sql=false
echo spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.SQLServerDialect
echo spring.jpa.properties.hibernate.format_sql=true
echo spring.jpa.properties.hibernate.hbm2ddl.auto=update
echo.
echo # Thymeleaf Configuration
echo spring.thymeleaf.cache=false
echo spring.thymeleaf.prefix=classpath:/templates/
echo spring.thymeleaf.suffix=.html
echo.
echo # Server Configuration
echo server.port=8083
echo.
echo # Logging Configuration
echo logging.level.com.example.apartmentsalesmanagementsystem=DEBUG
echo logging.level.org.springframework.security=DEBUG
echo.
echo # File Upload Configuration
echo spring.servlet.multipart.max-file-size=10MB
echo spring.servlet.multipart.max-request-size=10MB
) > "src\main\resources\application.properties"

echo Configuration switched to MS SQL Server!
echo.
echo IMPORTANT: Update the following in application.properties:
echo 1. Change 'localhost' to your SQL Server hostname/IP
echo 2. Update 'apartment_app_user' to your database username
echo 3. Update 'YourSecurePassword123!' to your database password
echo.
echo Run 'switch-to-h2.bat' to switch back to H2 for development.
echo.
pause

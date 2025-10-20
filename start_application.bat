@echo off
echo Starting Skyline Apartments System...
echo.

REM Navigate to the project directory
cd /d "%~dp0"

REM Check if Java is installed
java -version >nul 2>&1
if errorlevel 1 (
    echo Error: Java is not installed or not in PATH
    echo Please install Java 17 or later
    pause
    exit /b 1
)

REM Check if Maven wrapper exists
if not exist "mvnw.cmd" (
    echo Error: Maven wrapper not found
    echo Please ensure you're in the correct project directory
    pause
    exit /b 1
)

echo Starting the application on port 8083...
echo.
echo The application will be available at: http://localhost:8083
echo.
echo Press Ctrl+C to stop the application
echo.

REM Start the Spring Boot application
call mvnw.cmd spring-boot:run

pause

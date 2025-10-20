# Skyline Apartments System Startup Script
Write-Host "Starting Skyline Apartments System..." -ForegroundColor Green
Write-Host ""

# Navigate to the script directory
Set-Location $PSScriptRoot

# Check if Java is installed
try {
    $javaVersion = java -version 2>&1
    Write-Host "Java found: $($javaVersion[0])" -ForegroundColor Green
} catch {
    Write-Host "Error: Java is not installed or not in PATH" -ForegroundColor Red
    Write-Host "Please install Java 17 or later" -ForegroundColor Yellow
    Read-Host "Press Enter to exit"
    exit 1
}

# Check if Maven wrapper exists
if (-not (Test-Path "mvnw.cmd")) {
    Write-Host "Error: Maven wrapper not found" -ForegroundColor Red
    Write-Host "Please ensure you're in the correct project directory" -ForegroundColor Yellow
    Read-Host "Press Enter to exit"
    exit 1
}

Write-Host "Starting the application on port 8083..." -ForegroundColor Cyan
Write-Host ""
Write-Host "The application will be available at: http://localhost:8083" -ForegroundColor Yellow
Write-Host ""
Write-Host "Press Ctrl+C to stop the application" -ForegroundColor Yellow
Write-Host ""

# Start the Spring Boot application
try {
    & .\mvnw.cmd spring-boot:run
} catch {
    Write-Host "Error starting application: $_" -ForegroundColor Red
    Read-Host "Press Enter to exit"
}

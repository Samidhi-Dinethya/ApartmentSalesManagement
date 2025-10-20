# 🚀 Application Startup Guide

## **Quick Start After Device Restart**

### **Option 1: Using Batch File (Recommended)**
1. Double-click `start_application.bat`
2. Wait for the application to start
3. Open your browser and go to: http://localhost:8083

### **Option 2: Using PowerShell**
1. Right-click `start_application.ps1`
2. Select "Run with PowerShell"
3. Wait for the application to start
4. Open your browser and go to: http://localhost:8083

### **Option 3: Manual Start**
1. Open Command Prompt or PowerShell
2. Navigate to your project folder
3. Run: `.\mvnw.cmd spring-boot:run`
4. Wait for the application to start
5. Open your browser and go to: http://localhost:8083

## **Login Credentials**
- **Admin User**: 
  - Username: `admin`
  - Password: `admin123`
- **Client Users**:
  - Username: `john.doe` / Password: `password123`
  - Username: `jane.smith` / Password: `password123`

## **Troubleshooting**

### **If you get "localhost refused to connect":**
1. Make sure the application is running
2. Check if port 8083 is available
3. Try restarting the application

### **If you get "Java not found":**
1. Install Java 17 or later
2. Add Java to your system PATH

### **If you get "Maven wrapper not found":**
1. Make sure you're in the correct project directory
2. Check if `mvnw.cmd` file exists

## **Stopping the Application**
- Press `Ctrl+C` in the terminal/command prompt
- Or close the terminal window

## **Port Information**
- **Application Port**: 8083
- **Database**: SQL Server (Windows Authentication)
- **URL**: http://localhost:8083

# 🏢 Skyline Apartments Management System

<div align="center">

![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.4-brightgreen.svg)
![Java](https://img.shields.io/badge/Java-17-orange.svg)
![MySQL](https://img.shields.io/badge/MySQL-8.0-blue.svg)
![License](https://img.shields.io/badge/License-MIT-yellow.svg)

*A comprehensive Spring Boot web application for managing apartment sales with separate admin and client dashboards, user management, and apartment listings.*

[🚀 Quick Start](#installation--setup) • [📖 Documentation](#key-features-explained) • [🐛 Issues](https://github.com/yourusername/ApartmentSalesManagementSystem/issues) • [💡 Features](#features)

</div>

---

## 📋 Table of Contents

- [Features](#-features)
- [Technology Stack](#-technology-stack)
- [Prerequisites](#-prerequisites)
- [Installation & Setup](#-installation--setup)
- [Project Structure](#-project-structure)
- [Key Features Explained](#-key-features-explained)
- [API Endpoints](#-api-endpoints)
- [Database Schema](#-database-schema)
- [Screenshots](#-screenshots)
- [Customization](#-customization)
- [Troubleshooting](#-troubleshooting)
- [Contributing](#-contributing)
- [License](#-license)

## ✨ Features

### 🏠 Core Features
- **👥 User Management**: Complete CRUD operations for users with role-based access
- **🏢 Apartment Management**: Full apartment listing and management system
- **🔐 Role-Based Access**: Separate dashboards for Admin and Client users
- **🎨 Modern UI**: Beautiful, responsive interface built with Bootstrap 5
- **💾 Database Integration**: MySQL database with JPA/Hibernate
- **🔒 Security**: Spring Security with BCrypt password encoding
- **📱 Responsive Design**: Mobile-friendly interface
- **📊 Analytics**: System statistics and reporting

### 👥 User Roles

#### 🔧 Admin Dashboard
- 📊 View system statistics and analytics
- 👥 Manage all users (activate/deactivate, edit, delete)
- 🏢 Manage all apartments across the system
- 📈 Generate reports and view system activity
- ⚙️ Full administrative control
- 🔍 Advanced search and filtering

#### 👤 Client Dashboard
- 🏠 Personal dashboard with user-specific information
- 📝 Manage own apartment listings
- 🔍 Browse available apartments
- 👤 Edit personal profile
- ➕ Add new apartment listings
- 💬 Contact management

### 🎨 UI Features
- 🎨 Modern, responsive design
- 🌈 Beautiful gradient backgrounds
- ✨ Interactive cards and hover effects
- 🎯 Font Awesome icons
- 🧩 Bootstrap 5 components
- 📱 Mobile-friendly interface
- 🌙 Dark/Light theme support
- 🎭 Smooth animations and transitions

## 🛠️ Technology Stack

| Category | Technology | Version |
|----------|------------|---------|
| **🚀 Backend** | Spring Boot | 3.5.4 |
| **🗄️ Database** | MySQL | 8.0+ |
| **🔄 ORM** | Spring Data JPA with Hibernate | Latest |
| **🔒 Security** | Spring Security with BCrypt | Latest |
| **🎨 Frontend** | Thymeleaf templates with Bootstrap 5 | 5.3+ |
| **🎯 Icons** | Font Awesome | 6.0+ |
| **🔨 Build Tool** | Maven | 3.6+ |
| **☕ Runtime** | Java | 17+ |

## 📋 Prerequisites

Before running this application, make sure you have:

- ☕ **Java 17** or higher
- 🗄️ **MySQL 8.0** or higher  
- 🔨 **Maven 3.6** or higher
- 💻 **IDE** (IntelliJ IDEA, Eclipse, or VS Code)
- 🌐 **Web Browser** (Chrome, Firefox, Safari, or Edge)

## 🚀 Installation & Setup

### 1. 🗄️ Database Setup

1. **Install MySQL** if you haven't already
2. **Create a new database** (optional - the application will create it automatically)
3. **Update database credentials** in `src/main/resources/application.properties`:

```properties
# Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/apartment_sales_db?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC
spring.datasource.username=your_username
spring.datasource.password=your_password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect
```

### 2. 📥 Clone and Build

```bash
# Clone the repository
git clone https://github.com/yourusername/ApartmentSalesManagementSystem.git
cd ApartmentSalesManagementSystem

# Build the project
mvn clean install

# Verify build success
mvn verify
```

### 3. ▶️ Run the Application

```bash
# Option 1: Run with Maven (Recommended for development)
mvn spring-boot:run

# Option 2: Run the JAR file (For production)
java -jar target/ApartmentSalesManagementSystem-0.0.1-SNAPSHOT.war

# Option 3: Run with specific profile
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

🌐 **Application URL**: `http://localhost:8080`

### 🎯 Initial Data

The application automatically creates sample data on first run:

#### 🔧 Admin User
| Field | Value |
|-------|-------|
| **Username** | `admin` |
| **Password** | `admin123` |
| **Role** | ADMIN |
| **Access** | Full system control |

#### 👤 Client Users
| Username | Password | Role | Description |
|----------|----------|------|-------------|
| `john.doe` | `password123` | CLIENT | Sample client user |
| `jane.smith` | `password123` | CLIENT | Sample client user |

> ⚠️ **Security Note**: Change default passwords in production environment!



## 📁 Project Structure

```
ApartmentSalesManagementSystem/
├── 📁 src/
│   ├── 📁 main/
│   │   ├── 📁 java/
│   │   │   └── 📁 com/example/apartmentsalesmanagementsystem/
│   │   │       ├── 📁 config/                    # 🔧 Configuration classes
│   │   │       │   ├── SecurityConfig.java       # 🔒 Security configuration
│   │   │       │   └── DataInitializer.java      # 🎯 Sample data initialization
│   │   │       ├── 📁 controller/                # 🎮 REST controllers
│   │   │       │   ├── MainController.java       # 🏠 Main application controller
│   │   │       │   ├── UserController.java       # 👥 User management
│   │   │       │   ├── AdminController.java      # 🔧 Admin operations
│   │   │       │   ├── ClientController.java     # 👤 Client operations
│   │   │       │   └── ApartmentController.java  # 🏢 Apartment management
│   │   │       ├── 📁 entity/                    # 📊 Data models
│   │   │       │   ├── User.java                 # 👤 User entity
│   │   │       │   ├── UserRole.java             # 🔐 User roles enum
│   │   │       │   ├── Apartment.java            # 🏢 Apartment entity
│   │   │       │   └── ApartmentStatus.java      # 📋 Apartment status enum
│   │   │       ├── 📁 repository/                # 💾 Data access layer
│   │   │       │   ├── UserRepository.java       # 👥 User data operations
│   │   │       │   └── ApartmentRepository.java  # 🏢 Apartment data operations
│   │   │       └── 📁 service/                   # 🔄 Business logic
│   │   │           ├── UserService.java          # 👥 User business logic
│   │   │           └── ApartmentService.java     # 🏢 Apartment business logic
│   │   └── 📁 resources/
│   │       ├── application.properties            # ⚙️ Application configuration
│   │       └── 📁 templates/                     # 🎨 Thymeleaf templates
│   │           ├── home.html                     # 🏠 Home page
│   │           ├── login.html                    # 🔐 Login page
│   │           ├── register.html                 # 📝 Registration page
│   │           ├── 📁 admin/                     # 🔧 Admin templates
│   │           │   └── dashboard.html            # 📊 Admin dashboard
│   │           └── 📁 client/                    # 👤 Client templates
│   │               └── dashboard.html            # 🏠 Client dashboard
│   └── 📁 test/                                  # 🧪 Test files
├── 📄 pom.xml                                    # 🔨 Maven configuration
├── 📄 README.md                                  # 📖 Project documentation
└── 📄 .gitignore                                 # 🚫 Git ignore rules
```

## 📖 Key Features Explained

### 👥 User Management CRUD
- **➕ Create**: User registration with comprehensive validation
- **👁️ Read**: View user profiles and comprehensive user lists
- **✏️ Update**: Edit user information with real-time validation
- **🗑️ Delete**: Remove users (admin only with confirmation)

### 🔧 Admin Dashboard
- 📊 System statistics overview with real-time data
- 👥 User management interface with advanced filtering
- 🏢 Apartment management with bulk operations
- 📈 Activity monitoring and audit trails
- ⚡ Quick action buttons for common tasks
- 🔍 Advanced search and reporting capabilities

### 👤 Client Dashboard
- 🏠 Personal apartment listings management
- 🔍 Browse available apartments with filters
- 👤 Profile management with photo upload
- ⚡ Quick access to common actions
- 📱 Mobile-optimized interface

### 🔒 Security Features
- 🔐 Role-based access control (RBAC)
- 🔑 Password encryption with BCrypt
- ✅ Form validation and sanitization
- 🕐 Session management with timeout
- 🛡️ CSRF protection
- 🔒 SQL injection prevention

## 📸 Screenshots

### 🏠 Home Page
![Home Page](https://via.placeholder.com/800x400/4CAF50/FFFFFF?text=Home+Page)

### 🔐 Login Page
![Login Page](https://via.placeholder.com/800x400/2196F3/FFFFFF?text=Login+Page)

### 🔧 Admin Dashboard
![Admin Dashboard](https://via.placeholder.com/800x400/FF9800/FFFFFF?text=Admin+Dashboard)

### 👤 Client Dashboard
![Client Dashboard](https://via.placeholder.com/800x400/9C27B0/FFFFFF?text=Client+Dashboard)

### 🏢 Apartment Management
![Apartment Management](https://via.placeholder.com/800x400/607D8B/FFFFFF?text=Apartment+Management)

## 🔗 API Endpoints

### 🌐 Public Endpoints
| Method | Endpoint | Description | Access |
|--------|----------|-------------|---------|
| `GET` | `/` | 🏠 Home page | Public |
| `GET` | `/login` | 🔐 Login page | Public |
| `POST` | `/login` | 🔐 Process login | Public |
| `GET` | `/register` | 📝 Registration page | Public |
| `POST` | `/register` | 📝 Process registration | Public |
| `GET` | `/apartments` | 🏢 Browse apartments | Public |

### 🔧 Admin Endpoints
| Method | Endpoint | Description | Access |
|--------|----------|-------------|---------|
| `GET` | `/admin/dashboard` | 📊 Admin dashboard | Admin |
| `GET` | `/admin/users` | 👥 Manage users | Admin |
| `POST` | `/admin/users` | ➕ Create user | Admin |
| `PUT` | `/admin/users/{id}` | ✏️ Update user | Admin |
| `DELETE` | `/admin/users/{id}` | 🗑️ Delete user | Admin |
| `GET` | `/admin/apartments` | 🏢 Manage apartments | Admin |
| `GET` | `/admin/reports` | 📈 View reports | Admin |

### 👤 Client Endpoints
| Method | Endpoint | Description | Access |
|--------|----------|-------------|---------|
| `GET` | `/client/dashboard` | 🏠 Client dashboard | Client |
| `GET` | `/client/apartments` | 🔍 Browse apartments | Client |
| `GET` | `/client/my-apartments` | 🏠 My apartment listings | Client |
| `POST` | `/client/apartments` | ➕ Add apartment | Client |
| `PUT` | `/client/apartments/{id}` | ✏️ Edit apartment | Client |
| `DELETE` | `/client/apartments/{id}` | 🗑️ Delete apartment | Client |
| `GET` | `/client/profile` | 👤 Profile management | Client |
| `PUT` | `/client/profile` | ✏️ Update profile | Client |

## 🗄️ Database Schema

### 👥 Users Table
| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| `id` | BIGINT | PRIMARY KEY, AUTO_INCREMENT | Unique user identifier |
| `username` | VARCHAR(50) | UNIQUE, NOT NULL | User login name |
| `email` | VARCHAR(100) | UNIQUE, NOT NULL | User email address |
| `password` | VARCHAR(255) | NOT NULL | Encrypted password |
| `first_name` | VARCHAR(50) | NOT NULL | User's first name |
| `last_name` | VARCHAR(50) | NOT NULL | User's last name |
| `phone_number` | VARCHAR(20) | NULL | Contact phone number |
| `role` | ENUM | NOT NULL | User role (ADMIN/CLIENT) |
| `is_active` | BOOLEAN | DEFAULT TRUE | Account status |
| `created_at` | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | Creation timestamp |
| `updated_at` | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP ON UPDATE | Last update timestamp |

### 🏢 Apartments Table
| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| `id` | BIGINT | PRIMARY KEY, AUTO_INCREMENT | Unique apartment identifier |
| `title` | VARCHAR(200) | NOT NULL | Apartment title |
| `description` | TEXT | NULL | Detailed description |
| `address` | VARCHAR(255) | NOT NULL | Street address |
| `city` | VARCHAR(100) | NOT NULL | City name |
| `state` | VARCHAR(50) | NOT NULL | State/Province |
| `zip_code` | VARCHAR(20) | NOT NULL | Postal/ZIP code |
| `price` | DECIMAL(12,2) | NOT NULL | Apartment price |
| `bedrooms` | INT | NOT NULL | Number of bedrooms |
| `bathrooms` | DECIMAL(3,1) | NOT NULL | Number of bathrooms |
| `square_feet` | INT | NULL | Apartment size |
| `status` | ENUM | DEFAULT 'AVAILABLE' | Status (AVAILABLE/UNDER_CONTRACT/SOLD/RENTED) |
| `image_url` | VARCHAR(500) | NULL | Apartment image URL |
| `owner_id` | BIGINT | FOREIGN KEY | Reference to Users table |
| `created_at` | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | Creation timestamp |
| `updated_at` | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP ON UPDATE | Last update timestamp |

## 🎨 Customization

### ➕ Adding New Features
1. **📊 Create entity classes** in the `entity` package
2. **💾 Add repository interfaces** in the `repository` package
3. **🔄 Implement service logic** in the `service` package
4. **🎮 Create controllers** in the `controller` package
5. **🎨 Add Thymeleaf templates** in the `templates` directory
6. **🧪 Write unit tests** in the `test` package
7. **📝 Update documentation** and README

### 🎨 Styling Guidelines
- **🎯 Main styles** are in the HTML files using `<style>` tags
- **🧩 Bootstrap 5 classes** are used for layout and components
- **🎨 Custom CSS variables** can be added for consistent theming
- **📱 Responsive design** principles should be followed
- **♿ Accessibility** standards should be maintained
- **🎭 Animations** should be smooth and purposeful

### 🔧 Configuration Options
- **⚙️ Application properties** can be customized in `application.properties`
- **🔒 Security settings** can be modified in `SecurityConfig.java`
- **🗄️ Database settings** can be adjusted for different environments
- **📊 Logging levels** can be configured for debugging

## 🔧 Troubleshooting

### 🚨 Common Issues

#### 1. 🗄️ Database Connection Error
**Symptoms**: Application fails to start with database connection errors
**Solutions**:
- ✅ Verify MySQL is running: `sudo systemctl status mysql`
- ✅ Check database credentials in `application.properties`
- ✅ Ensure database exists or auto-creation is enabled
- ✅ Verify MySQL port (default: 3306) is accessible
- ✅ Check firewall settings

#### 2. 🔌 Port Already in Use
**Symptoms**: `Port 8080 was already in use` error
**Solutions**:
- ✅ Change port in `application.properties`: `server.port=8081`
- ✅ Kill process using port: `netstat -tulpn | grep :8080`
- ✅ Use different port: `mvn spring-boot:run -Dspring-boot.run.arguments="--server.port=8081"`

#### 3. 🔨 Build Errors
**Symptoms**: Maven build fails with compilation errors
**Solutions**:
- ✅ Ensure Java 17+ is installed: `java -version`
- ✅ Clean and rebuild: `mvn clean install`
- ✅ Update Maven dependencies: `mvn dependency:resolve`
- ✅ Check IDE Java version settings

#### 4. 🔐 Authentication Issues
**Symptoms**: Login fails or access denied errors
**Solutions**:
- ✅ Verify user credentials in database
- ✅ Check password encryption settings
- ✅ Ensure user account is active
- ✅ Clear browser cache and cookies

### 📊 Logs and Debugging
- **🖥️ Application logs** are available in the console
- **🗄️ Database queries** are logged when `spring.jpa.show-sql=true`
- **🔍 Debug mode** can be enabled with `logging.level.com.example=DEBUG`
- **📈 Performance monitoring** available with Actuator endpoints

### 🆘 Getting Help
- 📖 Check the [Spring Boot documentation](https://spring.io/projects/spring-boot)
- 🐛 Report issues on [GitHub Issues](https://github.com/yourusername/ApartmentSalesManagementSystem/issues)
- 💬 Join our community discussions

## 🤝 Contributing

We welcome contributions to improve the Apartment Sales Management System! Here's how you can help:

### 🚀 Getting Started
1. **🍴 Fork the repository** on GitHub
2. **🌿 Create a feature branch**: `git checkout -b feature/amazing-feature`
3. **💻 Make your changes** following our coding standards
4. **🧪 Add tests** for new functionality
5. **📝 Update documentation** if needed
6. **✅ Run tests**: `mvn test`
7. **📤 Submit a pull request**

### 📋 Contribution Guidelines
- **🎯 Follow Java coding conventions**
- **📝 Write clear commit messages**
- **🧪 Ensure all tests pass**
- **📖 Update documentation for new features**
- **🔍 Test your changes thoroughly**

### 🐛 Reporting Issues
- **🔍 Search existing issues** before creating new ones
- **📝 Provide detailed information** about the problem
- **🖥️ Include system information** (OS, Java version, etc.)
- **📊 Add logs and error messages** if applicable

## 📄 License

This project is licensed under the **MIT License** - see the [LICENSE](LICENSE) file for details.

```
MIT License

Copyright (c) 2024 Apartment Sales Management System

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```

## 🆘 Support

### 📞 Getting Help
- **🐛 Bug Reports**: [GitHub Issues](https://github.com/yourusername/ApartmentSalesManagementSystem/issues)
- **💡 Feature Requests**: [GitHub Discussions](https://github.com/yourusername/ApartmentSalesManagementSystem/discussions)
- **📧 Email**: support@apartmentsales.com
- **💬 Discord**: [Join our community](https://discord.gg/apartmentsales)

### 📚 Resources
- **📖 Documentation**: [Wiki](https://github.com/yourusername/ApartmentSalesManagementSystem/wiki)
- **🎥 Video Tutorials**: [YouTube Channel](https://youtube.com/apartmentsales)
- **📝 Blog**: [Development Blog](https://blog.apartmentsales.com)

---

<div align="center">

### 🌟 Star this repository if you found it helpful!

**Made with ❤️ by the Apartment Sales Management Team**

[⬆️ Back to Top](#-skyline-apartments-management-system)

</div>

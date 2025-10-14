# NovaBook Library Management System

**Author**: Luis Alfredo  
**Clan**: Cienaga  
**Program**: Advanced Java + Spring Boot Route  
**Institution**: CodeUp  
**Project Type**: Library Management System with JavaFX GUI  
**Date**: October 2024

---

## 📋 Project Overview

NovaBook is a comprehensive library management system developed in Java 17 with JavaFX for the graphical user interface and MySQL for data persistence. The system implements role-based access control (RBAC) following the Model-View-Controller (MVC) architectural pattern with Data Access Object (DAO) design pattern.

### 🎯 System Features

- **Role-Based Access Control**: Administrator and Librarian roles with different permission levels
- **Book Management**: Complete CRUD operations for library inventory
- **Partner Management**: Library member registration and administration
- **Loan Management**: Book lending and return processing with overdue tracking
- **User Management**: System user administration (Administrator only)
- **Data Import/Export**: CSV functionality for data migration and reporting

---

## 👥 User Roles & Permissions

### 👑 Administrator Role
**Full System Access**
- ✅ Book Management (Create, Read, Update, Delete)
- ✅ Partner Management (Complete member administration)
- ✅ Loan Management (Lending and returns processing)
- ✅ User Management (System user administration)
- ✅ Import/Export Operations (Data management)

### 📚 Librarian Role
**Limited System Access**
- ✅ Book Management (Complete book operations)
- ✅ Partner Management (Member administration)
- ✅ Loan Management (Lending and returns)
- ✅ Import/Export Operations (Data operations)
- ❌ User Management (Access Denied - Administrator only)

---

## 🏗️ System Architecture

### Design Patterns Implemented

#### Model-View-Controller (MVC)
- **Model Layer**: Entity classes and business logic
- **View Layer**: JavaFX user interface components
- **Controller Layer**: Application logic and navigation control

#### Data Access Object (DAO)
- Centralized database operations
- Clean separation of concerns
- Connection pooling and management

#### Singleton Pattern
- Database connection management
- Single point of access for database operations

### 📊 System Diagrams

#### Use Case Diagram
![Use Case Diagram](docs/diagrams/use-case-diagram.png)

**Shows:**
- System actors (Administrator, Librarian)
- Core use cases organized by functionality
- Role-based access restrictions
- Access control mechanisms

#### Class Diagram
![Class Diagram](docs/diagrams/class-diagram.png)

**Shows:**
- Complete MVC architecture
- Entity relationships and associations
- DAO pattern implementation
- Controller and View organization
- Database connection management

---

## 🔧 Technology Stack

| Component | Technology | Version |
|-----------|------------|---------|
| **Language** | Java | 11+ |
| **GUI Framework** | JavaFX | 17+ |
| **Database** | MySQL | 8.0+ |
| **Build Tool** | Maven | 3.6+ |
| **IDE Support** | NetBeans | 15+ |
| **Testing** | JUnit 5 | 5.10.0 |
| **Code Coverage** | JaCoCo | 0.8.11 |
| **Logging** | java.util.logging | Built-in |
| **Configuration** | Properties files | Built-in |
| **Design Patterns** | MVC, DAO, Singleton | - |

---

## 📁 Project Structure

```
NovaBook/
├── src/
│   ├── main/
│   │   ├── java/com/codeup/novabook/
│   │   │   ├── App/
│   │   │   │   └── NovaBook.java                    # Application entry point
│   │   │   ├── Controllers/
│   │   │   │   ├── LoginController.java             # Authentication logic
│   │   │   │   ├── MainController.java              # Main navigation
│   │   │   │   ├── ReportsController.java           # Import/Export operations
│   │   │   │   └── BookController.java              # Book operations
│   │   │   ├── Models/
│   │   │   │   ├── Entity/                          # Domain entities
│   │   │   │   │   ├── Book.java                    # Book entity
│   │   │   │   │   ├── Partner.java                 # Library member entity
│   │   │   │   │   ├── Users.java                   # System user entity
│   │   │   │   │   ├── Loan.java                    # Loan transaction entity
│   │   │   │   │   └── Rol.java                     # User role entity
│   │   │   │   ├── DAO/                             # Data access objects
│   │   │   │   │   ├── BookDAO.java                 # Book data operations
│   │   │   │   │   ├── PartnerDAO.java              # Partner data operations
│   │   │   │   │   ├── UserDAO.java                 # User data operations
│   │   │   │   │   ├── LoanDAO.java                 # Loan data operations
│   │   │   │   │   └── RolDAO.java                  # Role data operations
│   │   │   │   └── Connection/
│   │   │   │       └── ConnectionDB.java            # Database connection management
│   │   │   ├── Utils/                               # Utility classes
│   │   │   │   ├── ConfigManager.java               # Configuration management
│   │   │   │   └── AppLogger.java                   # Application logging
│   │   │   ├── Views/                               # JavaFX user interface
│   │   │   │   ├── LoginView.java                   # Authentication interface
│   │   │   │   ├── MainView.java                    # Main menu interface
│   │   │   │   ├── BooksView.java                   # Book management interface
│   │   │   │   ├── PartnersView.java                # Partner management interface
│   │   │   │   ├── LoansView.java                   # Loan management interface
│   │   │   │   ├── UsersView.java                   # User management interface
│   │   │   │   ├── ReportsView.java                 # Import/Export interface
│   │   │   │   ├── AdminDashboardView.java          # Administrator dashboard
│   │   │   │   └── LibrarianDashboardView.java      # Librarian dashboard
│   │   │   └── Reports/
│   │   │       ├── CSVReportGenerator.java          # Data export functionality
│   │   │       └── CSVBookImporter.java             # Data import functionality
│   │   └── resources/
│   │       └── config.properties                    # Application configuration
│   └── test/java/com/codeup/novabook/
│       └── Models/Entity/
│           ├── BookTest.java                        # Book entity tests
│           └── LoanTest.java                        # Loan entity tests
├── database/                                # Database scripts
│   ├── schema.sql                          # Database schema with triggers
│   └── sample_data.sql                     # Test data for development
├── docs/                                   # Documentation
│   └── diagrams/                           # UML diagrams
│       ├── use-case-diagram.png            # Use case diagram
│       ├── use-case-diagram.puml           # PlantUML source
│       ├── class-diagram.png               # Class diagram
│       └── class-diagram.puml              # PlantUML source
├── logs/                                   # Application logs
│   └── app.log                             # Main application log file
├── exports/                                # CSV export files
├── jacoco-commands.sh                      # JaCoCo utility script
├── pom.xml                                 # Maven configuration
├── README.md                               # Project documentation
├── REQUIREMENTS_ANALYSIS.md                # Requirements compliance analysis
├── TESTING_DOCUMENTATION.md               # JUnit 5 testing documentation
└── JACOCO_COVERAGE_REPORT.md               # Code coverage analysis
```

---

## 🚀 Installation & Setup

### Prerequisites

```bash
- Java Development Kit (JDK) 17 or higher
- MySQL Server 8.0 or higher
- Maven 3.6 or higher
- JavaFX Runtime (included in OpenJDK 17+)
```

### Database Configuration

1. **Create MySQL Database:**
   ```sql
   CREATE DATABASE Biblioteca CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   ```

2. **Configure Database Connection:**
   - Edit `src/main/resources/config.properties`
   - Update connection parameters:
     ```properties
     database.url=jdbc:mysql://localhost:3306/Biblioteca
     database.username=root
     database.password=your_password
     database.driver=com.mysql.cj.jdbc.Driver
     ```

3. **Run Database Scripts:**
   ```bash
   # Create database schema with triggers and procedures
   mysql -u root -p < database/schema.sql
   
   # Load sample data for testing (optional)
   mysql -u root -p < database/sample_data.sql
   ```

### Build & Execution

1. **Clone/Download Project:**
   ```bash
   cd /path/to/your/workspace
   # Project should be in: NovaBook/
   ```

2. **Compile Project:**
   ```bash
   cd NovaBook
   mvn clean compile
   ```

3. **Run Tests:**
   ```bash
   # Run unit tests with coverage
   mvn clean test
   
   # Generate coverage report
   ./jacoco-commands.sh test
   
   # View coverage summary
   ./jacoco-commands.sh summary
   ```

4. **Run Application:**
   ```bash
   mvn javafx:run
   ```

### Demo User Accounts

| Role | Username | Password | Description |
|------|----------|----------|-------------|
| **Administrator** | `admin` | `admin` | Full system access |
| **Librarian** | `librarian` | `librarian` | Limited access (no user management) |

---

## 💻 System Features

### Authentication System
- Secure login with role validation
- Session management with current user tracking
- Role-based interface adaptation
- Access control enforcement

### Book Management
- Add new books with complete bibliographic information
- Edit existing book details
- Delete books from inventory
- Search books by title, author, or ISBN
- Availability status tracking
- Publication date management

### Partner Management
- Register new library members
- Edit member information
- Manage member status (active/inactive)
- Contact information management
- Registration date tracking

### Loan Management
- Create new book loans
- Process book returns
- Track loan dates and due dates
- Overdue loan identification
- Loan history management
- Search loan records

### Data Import/Export
- Export books to CSV format
- Export partners to CSV format
- Export loans to CSV format
- Import books from CSV files
- Download CSV templates
- Data validation on import

---

## 🔒 Security Features

### Role-Based Access Control (RBAC)
- Granular permission system
- Interface adaptation based on user role
- Access denial mechanisms for restricted functions
- Session-based authentication

### Data Validation
- Input validation on all forms
- SQL injection prevention through prepared statements
- Data integrity enforcement
- Error handling and user feedback

---

## 📈 Future Enhancements

### Phase 2 - Spring Boot Migration
- [ ] Web-based interface using Spring Boot
- [ ] REST API implementation
- [ ] JWT authentication
- [ ] Database migration to JPA/Hibernate

### Phase 3 - Advanced Features
- [ ] Advanced reporting dashboard
- [ ] Email notifications for overdue books
- [ ] Barcode scanning integration
- [ ] Multi-library support
- [ ] Mobile application support

---

## 🛠️ Development Information

### Maven Dependencies
```xml
<!-- MySQL Connector -->
<dependency>
    <groupId>com.mysql</groupId>
    <artifactId>mysql-connector-j</artifactId>
    <version>8.0.33</version>
</dependency>

<!-- Apache Commons DBCP -->
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-dbcp2</artifactId>
    <version>2.12.0</version>
</dependency>

<!-- NetBeans AbsoluteLayout -->
<dependency>
    <groupId>org.netbeans.external</groupId>
    <artifactId>AbsoluteLayout</artifactId>
    <version>RELEASE170</version>
</dependency>
```

### Compilation Configuration
```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-compiler-plugin</artifactId>
    <version>3.11.0</version>
    <configuration>
        <source>17</source>
        <target>17</target>
        <release>17</release>
    </configuration>
</plugin>
```

---

## 📞 Contact Information

**Developer**: Luis Alfredo  
**Clan**: Cienaga  
**Program**: Advanced Java + Spring Boot Route  
**Institution**: CodeUp  

**Project Repository**: Local Development Environment  
**Development Environment**: Ubuntu Linux with NetBeans IDE  

---

## 📄 License

This project is developed as part of the CodeUp Advanced Java + Spring Boot learning route.  
Educational purposes only.

---

## 🙏 Acknowledgments

- **CodeUp Institution** for providing the learning framework
- **Clan Cienaga** for collaborative support
- **Java Community** for extensive documentation and resources
- **JavaFX Community** for GUI framework support

---

**Generated Documentation**: October 2024  
**PlantUML Diagrams**: Professional UML documentation  
**Total Classes**: 27 Java classes implementing complete MVC architecture
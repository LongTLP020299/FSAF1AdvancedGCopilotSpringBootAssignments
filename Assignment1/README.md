# E-commerce Review System

A Spring Boot application for learning advanced GitHub Copilot features and implementing a product review and rating system.

## Project Overview

This project is designed as a lab exercise for **Advanced GitHub Copilot for Java & Spring Boot Developers**. It demonstrates:

- Planning and implementing complex features using GitHub Copilot
- Test-Driven Development (TDD) approach
- Advanced SQL queries and database optimization
- Spring Boot best practices and security

## Features

### Current Features
- ✅ User authentication and registration
- ✅ Product management with categories
- ✅ Order processing system
- ✅ Basic security configuration
- ✅ H2 database setup for development

### To Be Implemented (Lab Tasks)
- 🔄 Product Review and Rating System
- 🔄 Automatic Average Rating Calculation
- 🔄 Dashboard API with Statistics
- 🔄 Advanced Query Optimization

## Technology Stack

- **Framework**: Spring Boot 3.2.0
- **Java Version**: 17
- **Database**: MySQL (production), H2 for testing
- **Security**: Spring Security (simplified for development)
- **Testing**: JUnit 5, Mockito, TestContainers
- **Build Tool**: Maven
- **Mapping**: MapStruct

## Project Structure

```
src/
├── main/
│   ├── java/com/fsoft/ecommerce/
│   │   ├── config/          # Security and other configurations
│   │   ├── controller/      # REST controllers
│   │   ├── dto/            # Data Transfer Objects
│   │   ├── entity/         # JPA entities
│   │   ├── exception/      # Custom exceptions
│   │   ├── repository/     # JPA repositories
│   │   └── service/        # Business logic services
│   └── resources/
│       └── application.properties
└── test/
    └── java/               # Unit and integration tests
```

## Quick Start

### Prerequisites
- Java 17 or higher
- Maven 3.6+
- MySQL 8.0+ server running on localhost:3306
- IDE with GitHub Copilot extension (VS Code recommended)

### Running the Application

1. **Start MySQL Server**:
   ```bash
   # Make sure MySQL is running on localhost:3306
   # The application will auto-create database 'democpl' if not exists
   ```

2. **Clone and navigate to project**:
   ```bash
   cd d:\workspace_cpl\Lab1
   ```

3. **Install dependencies**:
   ```bash
   mvn clean install
   ```

4. **Run the application**:
   ```bash
   mvn spring-boot:run
   ```
   
   **Note**: 
   - Database `democpl` will be created automatically if it doesn't exist
   - All tables will be created/updated automatically based on JPA entities
   - Sample data will be loaded automatically from `data.sql`

5. **Access the application**:
   - Application: http://localhost:8080
   - MySQL Database: `democpl` on localhost:3306

### Testing

Run all tests:
```bash
mvn test
```

Run specific test class:
```bash
mvn test -Dtest=ReviewServiceImplTest
```

## Lab Tasks

This project is designed for a 120-minute lab session with 5 main tasks:

### Task 1: Planning with @workspace
Use GitHub Copilot Chat to plan the review system implementation.

### Task 2: TDD Implementation
Implement the review system using Test-Driven Development.

### Task 3: Logic Integration
Add automatic average rating calculation.

### Task 4: Advanced Querying
Create dashboard API with complex SQL queries.

### Task 5: Final Review
Use Copilot for code review and optimization.

## API Endpoints

### Current Endpoints
- `POST /api/products/{productId}/reviews?userId={userId}` - Add product review
- `GET /api/products/{productId}/reviews` - Get product reviews
- `GET /api/dashboard/stats` - Get dashboard statistics

### Security
- Basic Spring Security configuration
- Simplified for development and lab purposes
- All API endpoints are open for testing

## Database Schema

### Main Entities
- **User**: User accounts with roles
- **Product**: Products with categories and ratings
- **Order**: Customer orders with status tracking
- **OrderItem**: Individual items in orders
- **Review**: Product reviews with ratings (to be enhanced)

### Key Relationships
- User → Orders (One-to-Many)
- Order → OrderItems (One-to-Many)
- Product → OrderItems (One-to-Many)
- User → Reviews (One-to-Many)
- Product → Reviews (One-to-Many)

## Development Guidelines

### Using GitHub Copilot Effectively

1. **Use @workspace for context**: 
   ```
   @workspace I need to add a product review feature...
   ```

2. **Provide detailed prompts**:
   ```
   // Implement the logic for adding a review.
   // 1. First, verify user has purchased the product
   // 2. Check if review already exists
   // 3. Save the review and update average rating
   ```

3. **Use descriptive comments**:
   ```java
   // Calculate average rating for product and update in database
   ```

## Database Configuration

### Auto-Update Schema
The application is configured with **Hibernate auto-update** feature:

```yml
spring:
  jpa:
    hibernate:
      ddl-auto: update  # Automatically updates tables when entities change
  sql:
    init:
      mode: always      # Always run data.sql for sample data
```

**Benefits:**
- ✅ **No manual database setup required**
- ✅ **Automatic table creation** when you first run the app
- ✅ **Schema updates** when you modify entities
- ✅ **Sample data loading** from `data.sql`
- ✅ **Database auto-creation** with `createDatabaseIfNotExist=true`

**What happens when you run the app:**
1. MySQL database `democpl` is created if it doesn't exist
2. All tables are created automatically from JPA entities
3. If you modify entities (add/remove fields), tables are updated
4. Sample data is inserted from `data.sql`

### Manual Database Setup (Optional)
If you prefer manual setup, you can use:
```bash
mysql -u root -p < database-setup.sql
```

### Testing Strategy
- Write tests before implementation (TDD)
- Mock external dependencies
- Test edge cases and error conditions
- Validate business rules

## Contributing

This is a lab project. Follow the lab guide for implementing features step by step.

## License

This project is for educational purposes only.

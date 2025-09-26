# Inventory Management System API

A Spring Boot REST API for managing product inventory in a warehouse environment.

## Overview

This project implements a comprehensive inventory management system with full CRUD operations, stock management, and low stock monitoring capabilities. Built with Spring Boot 3.5.6 and following enterprise-level best practices.

## Features

### Core Features
- **Product Management**: Full CRUD operations for products
- **Inventory Control**: Stock increase/decrease with validation
- **Low Stock Monitoring**: Track products below threshold
- **Data Validation**: Comprehensive input validation
- **Error Handling**: Proper HTTP status codes and error messages

### Product Properties
- `id`: Unique identifier (auto-generated)
- `name`: Product name (required)
- `description`: Product description (optional)
- `stockQuantity`: Current stock level (required, ≥ 0)
- `lowStockThreshold`: Minimum stock level (default: 10)

## API Endpoints

### Product Management

#### Create Product
```http
POST /api/products
Content-Type: application/json

{
  "name": "Laptop",
  "description": "Gaming laptop",
  "stockQuantity": 50,
  "lowStockThreshold": 5
}
```

#### Get All Products
```http
GET /api/products
```

#### Get Product by ID
```http
GET /api/products/{id}
```

#### Update Product
```http
PUT /api/products/{id}
Content-Type: application/json

{
  "name": "Updated Laptop",
  "description": "High-end gaming laptop",
  "stockQuantity": 45,
  "lowStockThreshold": 8
}
```

#### Delete Product
```http
DELETE /api/products/{id}
```

### Stock Management

#### Increase Stock
```http
POST /api/products/{id}/stock/increase
Content-Type: application/json

{
  "quantity": 20
}
```

#### Decrease Stock
```http
POST /api/products/{id}/stock/decrease
Content-Type: application/json

{
  "quantity": 15
}
```

### Monitoring

#### Get Low Stock Products
```http
GET /api/products/low-stock
```

## Business Rules

1. **Stock Validation**: Stock quantity cannot go below zero
2. **Insufficient Stock**: Returns 400 Bad Request when trying to decrease stock beyond available amount
3. **Low Stock Detection**: Products with stock ≤ threshold are flagged as low stock
4. **Input Validation**: All required fields must be provided and valid

## Error Responses

### 400 Bad Request - Validation Error
```json
{
  "status": 400,
  "error": "Validation Failed",
  "validationErrors": {
    "name": "Product name is required",
    "stockQuantity": "Stock quantity cannot be negative"
  },
  "timestamp": "2024-01-15T10:30:00"
}
```

### 400 Bad Request - Insufficient Stock
```json
{
  "status": 400,
  "error": "Insufficient Stock",
  "message": "Insufficient stock. Available: 10, Requested: 15",
  "timestamp": "2024-01-15T10:30:00"
}
```

### 404 Not Found
```json
{
  "status": 404,
  "error": "Product Not Found",
  "message": "Product not found with ID: 999",
  "timestamp": "2024-01-15T10:30:00"
}
```

## Running the Application

### Prerequisites
- Java 17 or higher
- Maven 3.6+

### Development
```bash
# Run the application
./mvnw spring-boot:run

# Run tests
./mvnw test

# Build JAR
./mvnw clean package
```

### Production
```bash
# Run the JAR file
java -jar target/ase-challenge-0.0.1-SNAPSHOT.jar
```

The application will start on `http://localhost:8080`

### Database Access
- H2 Console: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:inventory`
- Username: `sa`
- Password: (empty)

## Testing

The project includes comprehensive tests:

- **Unit Tests**: Service and controller layer testing
- **Integration Tests**: End-to-end API testing
- **Edge Cases**: Boundary condition testing

### Test Coverage
- Stock operations with various scenarios
- Validation error handling
- Business logic edge cases
- Complete product lifecycle

### Running Tests
```bash
# Run all tests
./mvnw test

# Run specific test class
./mvnw test -Dtest=ProductServiceTest

# Run integration tests
./mvnw test -Dtest=ProductIntegrationTest
```

## Sample Data

The application includes sample data for demonstration:
- 6 pre-loaded products with varying stock levels
- Mix of normal and low-stock items for testing

## Architecture

### Technology Stack
- **Spring Boot 3.5.6**: Main framework
- **Spring Data JPA**: Data persistence
- **H2 Database**: In-memory database
- **Lombok**: Boilerplate code reduction
- **Bean Validation**: Input validation
- **JUnit 5**: Testing framework

### Project Structure
```
src/
├── main/java/com/verto/ase_challenge/
│   ├── controller/     # REST controllers
│   ├── service/        # Business logic
│   ├── repository/     # Data access
│   ├── entity/         # JPA entities
│   ├── dto/           # Data transfer objects
│   └── exception/     # Exception handling
└── test/java/com/verto/ase_challenge/
    ├── service/       # Service tests
    ├── controller/    # Controller tests
    └── integration/   # Integration tests
```

## Quick Start

### Option 1: Using Maven (Recommended)
```bash
# Build and run
./mvnw spring-boot:run

# Or build JAR and run
./mvnw clean package -DskipTests
java -jar target/ase-challenge-0.0.1-SNAPSHOT.jar
```

### Option 2: Using Docker
```bash
# Build and run with Docker Compose
docker-compose up --build
```

### Option 3: Using deployment scripts
```bash
# Linux/Mac
./deploy.sh

# Windows
deploy.bat
```

## Testing the API

Use the provided `api-test.http` file with your HTTP client (VS Code REST Client, Postman, etc.) or use curl:

```bash
# Create a product
curl -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -d '{"name":"Laptop","description":"Gaming laptop","stockQuantity":50,"lowStockThreshold":5}'

# Get all products
curl http://localhost:8080/api/products

# Increase stock
curl -X POST http://localhost:8080/api/products/1/stock/increase \
  -H "Content-Type: application/json" \
  -d '{"quantity":20}'
```

## Deployment Ready

The application is production-ready with:
- ✅ Robust business logic for inventory management
- ✅ Comprehensive error handling with proper HTTP status codes
- ✅ Input validation and sanitization
- ✅ Transaction management for data consistency
- ✅ Extensive test coverage (unit + integration tests)
- ✅ Docker containerization support
- ✅ Health checks and monitoring
- ✅ Detailed API documentation
- ✅ Sample data and test scripts
- ✅ Cross-platform deployment scripts
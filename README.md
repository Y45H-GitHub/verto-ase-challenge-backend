# Inventory Management System - Multi-Language Implementation

A comprehensive inventory management system implemented in both **Java (Spring Boot)** and **TypeScript (Express.js)** with identical functionality and APIs.
YouTube : https://youtu.be/a9goL0oHtyc

## 🚀 Overview

<img width="1481" height="719" alt="image" src="https://github.com/user-attachments/assets/a656a9bd-fe75-402f-9445-04b6ab567c39" />

This repository demonstrates a complete inventory management system built with enterprise-level best practices in two different technology stacks:

- **Java Backend**: Spring Boot 3.5.6 with H2 Database
- **TypeScript Backend**: Express.js with SQLite Database

Both implementations provide identical REST APIs for managing product inventory in a warehouse environment.

## ✨ Features

### Core Functionality
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

## 📋 API Endpoints

Both backends expose identical REST APIs:

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

#### Health Check
```http
GET /health
```

## 🏗️ Architecture

### Java Backend (`backend-java/ase-challenge/`)

**Technology Stack:**
- Spring Boot 3.5.6
- Spring Data JPA
- H2 Database (in-memory)
- Lombok
- Bean Validation
- JUnit 5 + Mockito

**Project Structure:**
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

### TypeScript Backend (`backend-ts/`)

**Technology Stack:**
- Node.js + Express.js
- TypeScript
- SQLite Database
- Jest (Testing)
- ESLint (Linting)

**Project Structure:**
```
src/
├── controllers/        # Request handlers
├── services/          # Business logic
├── database/          # Database layer
├── types/             # TypeScript interfaces
├── routes/            # Route definitions
├── __tests__/         # Test files
├── app.ts             # Express app setup
└── index.ts           # Application entry point
```

## 🧪 Testing

### Java Backend
- **27 Tests Passing** ✅
- Unit tests for services and controllers
- Integration tests for complete workflows
- Comprehensive edge case coverage

```bash
cd backend-java/ase-challenge
./mvnw test
```

### TypeScript Backend
- **32 Tests Passing** ✅
- Unit tests for business logic
- Integration tests for API endpoints
- Complete test coverage matching Java functionality

```bash
cd backend-ts
npm test
```

## 🚀 Running the Applications

### Java Backend

#### Prerequisites
- Java 17 or higher
- Maven 3.6+

#### Development
```bash
cd backend-java/ase-challenge
./mvnw spring-boot:run
```

#### Production
```bash
cd backend-java/ase-challenge
./mvnw clean package
java -jar target/ase-challenge-0.0.1-SNAPSHOT.jar
```

**Access:** `http://localhost:8080`

### TypeScript Backend

#### Prerequisites
- Node.js 18+
- npm or yarn

#### Development
```bash
cd backend-ts
npm install
npm run dev
```

#### Production
```bash
cd backend-ts
npm install
npm run build
npm start
```

**Access:** `http://localhost:8080`

## 🐳 Docker Support

Both backends include Docker support:

### Java Backend
```bash
cd backend-java/ase-challenge
docker-compose up --build
```

### TypeScript Backend
```bash
cd backend-ts
docker-compose up --build
```

## 📊 Business Rules

1. **Stock Validation**: Stock quantity cannot go below zero
2. **Insufficient Stock**: Returns 400 Bad Request when trying to decrease stock beyond available amount
3. **Low Stock Detection**: Products with stock ≤ threshold are flagged as low stock
4. **Input Validation**: All required fields must be provided and valid

## 🔧 Error Responses

### 400 Bad Request - Validation Error
```json
{
  "status": 400,
  "error": "Validation Failed",
  "message": "Invalid input data",
  "validationErrors": {
    "name": "Product name is required",
    "stockQuantity": "Stock quantity cannot be negative"
  },
  "timestamp": "2024-01-15T10:30:00.000Z"
}
```

### 400 Bad Request - Insufficient Stock
```json
{
  "status": 400,
  "error": "Insufficient Stock",
  "message": "Insufficient stock. Available: 10, Requested: 15",
  "timestamp": "2024-01-15T10:30:00.000Z"
}
```

### 404 Not Found
```json
{
  "status": 404,
  "error": "Product Not Found",
  "message": "Product not found with ID: 999",
  "timestamp": "2024-01-15T10:30:00.000Z"
}
```

## 📝 Sample Data

Both applications include sample data for demonstration:
- 6 pre-loaded products with varying stock levels
- Mix of normal and low-stock items for testing

## 🧪 Testing the APIs

### Using curl

```bash
# Health check
curl http://localhost:8080/health

# Get all products
curl http://localhost:8080/api/products

# Create a product
curl -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -d '{"name":"Laptop","description":"Gaming laptop","stockQuantity":50,"lowStockThreshold":5}'

# Increase stock
curl -X POST http://localhost:8080/api/products/1/stock/increase \
  -H "Content-Type: application/json" \
  -d '{"quantity":20}'

# Get low stock products
curl http://localhost:8080/api/products/low-stock
```

### Database Access

#### Java Backend (H2 Console)
- URL: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:inventory`
- Username: `sa`
- Password: (empty)

#### TypeScript Backend (SQLite)
- Database file: In-memory SQLite
- Access via API endpoints only

## 🔒 Security Features

### Java Backend
- Spring Security configuration
- Input validation with Bean Validation
- SQL injection prevention with JPA
- CORS configuration

### TypeScript Backend
- Helmet.js security middleware
- CORS middleware
- Input validation and sanitization
- SQL injection prevention with parameterized queries

## 📈 Performance & Scalability

Both implementations are designed for:
- **High Performance**: Optimized database queries
- **Scalability**: Stateless design for horizontal scaling
- **Reliability**: Comprehensive error handling and validation
- **Maintainability**: Clean architecture and extensive testing



Both will start on `http://localhost:8080` with identical APIs! 🚀

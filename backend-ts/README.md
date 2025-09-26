# Inventory Management System API (TypeScript)

A TypeScript/Node.js REST API for managing product inventory in a warehouse environment.

## Overview

This project implements a comprehensive inventory management system with full CRUD operations, stock management, and low stock monitoring capabilities. Built with Express.js, TypeScript, and SQLite following enterprise-level best practices.

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

#### Health Check
```http
GET /health
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

## Running the Application

### Prerequisites
- Node.js 18+ 
- npm or yarn

### Installation
```bash
# Install dependencies
npm install

# Build the project
npm run build
```

### Development
```bash
# Run in development mode with hot reload
npm run dev

# Run tests
npm test

# Run tests in watch mode
npm run test:watch

# Lint code
npm run lint

# Fix linting issues
npm run lint:fix
```

### Production
```bash
# Build and start
npm run build
npm start
```

The application will start on `http://localhost:8080`

## Testing

The project includes comprehensive tests:

- **Unit Tests**: Service layer testing
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
npm test

# Run tests in watch mode
npm run test:watch

# Run with coverage
npm test -- --coverage
```

## Sample Data

The application includes sample data for demonstration:
- 6 pre-loaded products with varying stock levels
- Mix of normal and low-stock items for testing

## Architecture

### Technology Stack
- **Node.js**: Runtime environment
- **Express.js**: Web framework
- **TypeScript**: Type-safe JavaScript
- **SQLite**: Lightweight database
- **Jest**: Testing framework
- **ESLint**: Code linting

### Project Structure
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

## Quick Start

### Option 1: Development Mode
```bash
# Install dependencies
npm install

# Start development server
npm run dev
```

### Option 2: Production Build
```bash
# Install dependencies
npm install

# Build and start
npm run build
npm start
```

## Testing the API

Use curl or any HTTP client:

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

# Health check
curl http://localhost:8080/health
```

## Deployment Ready

The application is production-ready with:
- ✅ Robust business logic for inventory management
- ✅ Comprehensive error handling with proper HTTP status codes
- ✅ Input validation and sanitization
- ✅ Transaction management for data consistency
- ✅ Extensive test coverage (unit + integration tests)
- ✅ TypeScript for type safety
- ✅ Security middleware (Helmet, CORS)
- ✅ Logging and monitoring
- ✅ Detailed API documentation
- ✅ Sample data and health checks

## Environment Variables

- `PORT`: Server port (default: 8080)
- `NODE_ENV`: Environment (development/production)

## License

MIT License
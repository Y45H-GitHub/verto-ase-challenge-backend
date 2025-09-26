@echo off
REM Inventory Management System Deployment Script for Windows

echo 🚀 Building Inventory Management System...

REM Clean and build the project
call mvnw.cmd clean package -DskipTests

if %ERRORLEVEL% EQU 0 (
    echo ✅ Build successful!
    echo.
    echo 📦 JAR file created: target\ase-challenge-0.0.1-SNAPSHOT.jar
    echo.
    echo 🔧 To run the application:
    echo    java -jar target\ase-challenge-0.0.1-SNAPSHOT.jar
    echo.
    echo 🌐 Application will be available at:
    echo    API: http://localhost:8080/api/products
    echo    H2 Console: http://localhost:8080/h2-console
    echo.
    echo 🗄️ Database connection details:
    echo    JDBC URL: jdbc:h2:mem:inventory
    echo    Username: sa
    echo    Password: ^(empty^)
    echo.
    echo 📋 API Endpoints:
    echo    GET    /api/products              - Get all products
    echo    POST   /api/products              - Create product
    echo    GET    /api/products/{id}         - Get product by ID
    echo    PUT    /api/products/{id}         - Update product
    echo    DELETE /api/products/{id}         - Delete product
    echo    POST   /api/products/{id}/stock/increase - Increase stock
    echo    POST   /api/products/{id}/stock/decrease - Decrease stock
    echo    GET    /api/products/low-stock    - Get low stock products
) else (
    echo ❌ Build failed!
    exit /b 1
)
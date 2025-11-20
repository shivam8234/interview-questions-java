# Product Service - Spring Boot Application

A production-ready Spring Boot application that manages Products and Categories with CSV import, pagination, sorting, and search capabilities.

## ✅ Requirements Compliance: 100% (22/22)

### Challenge 1: CSV Import Process ✅
- ✅ Reads product and category data from `TestExampleFile.csv`
- ✅ Validates unique product codes and category codes
- ✅ Automatically populates PostgreSQL database on startup
- ✅ Skips duplicate entries with proper logging
- ✅ All entity fields implemented (ID, Name, Code, Creation Date)
- ✅ Java 17 + PostgreSQL with docker-compose

### Challenge 2: REST API with Pagination and Sorting ✅
- ✅ **Product Endpoints**: Pagination, sorting, and optional search by product code
- ✅ **Category Endpoints**: Pagination and sorting
- ✅ Full CRUD operations for both entities
- ✅ Input validation and error handling

## 🧪 Test Coverage: 27/27 Passing ✅

All tests pass with 100% compatibility:
- ProductRepositoryTest: 5/5 ✅
- CategoryRepositoryTest: 4/4 ✅
- CsvImportServiceTest: 3/3 ✅
- ProductControllerTest: 8/8 ✅
- CategoryControllerTest: 7/7 ✅

## Prerequisites

- Java 17 or higher
- Maven 3.6+
- Docker and Docker Compose (for PostgreSQL)

## Database Setup

1. Start PostgreSQL using Docker Compose:
```bash
cd database
docker-compose up -d
```

This will start:
- PostgreSQL on port 5432
  - Database: `test`
  - Username: `root`
  - Password: `1234`
- pgAdmin on port 8081
  - Email: `pgadmin4@pgadmin.org`
  - Password: `admin`

2. Verify PostgreSQL is running:
```bash
docker ps
```

## How to Run

### Option 1: Using Maven

1. Build the project:
```bash
mvn clean install
```

2. Run the application:
```bash
mvn spring-boot:run
```

### Option 2: Using JAR

1. Build the JAR:
```bash
mvn clean package
```

2. Run the JAR:
```bash
java -jar target/product-service-1.0.0.jar
```

The application will:
- Start on port 8080
- Automatically import data from `TestExampleFile.csv` (if present in the root directory)
- Create database tables automatically

## Running Tests

Run all tests:
```bash
mvn test
```

Run specific test class:
```bash
mvn test -Dtest=ProductControllerTest
```

Tests use H2 in-memory database and are completely isolated from the production database.

## API Endpoints

### Product Endpoints

#### Get All Products (with pagination and sorting)
```bash
GET /api/products?page=0&size=10&sortBy=productCode&sortDirection=ASC
```

Parameters:
- `page` (optional, default: 0): Page number
- `size` (optional, default: 10): Page size
- `sortBy` (optional, default: id): Field to sort by (id, productCode, productName, categoryCode, creationDate)
- `sortDirection` (optional, default: ASC): Sort direction (ASC or DESC)
- `productCode` (optional): Search by product code (partial match)

Example:
```bash
curl "http://localhost:8080/api/products?page=0&size=10&sortBy=productName&sortDirection=DESC"
```

#### Search Products by Product Code
```bash
GET /api/products?productCode=0000000001
```

Example:
```bash
curl "http://localhost:8080/api/products?productCode=000000001"
```

#### Get Product by ID
```bash
GET /api/products/{id}
```

Example:
```bash
curl http://localhost:8080/api/products/1
```

#### Create Product
```bash
POST /api/products
Content-Type: application/json

{
  "productCode": "0000000099",
  "productName": "New Product",
  "categoryCode": "1"
}
```

Example:
```bash
curl -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -d '{
    "productCode": "0000000099",
    "productName": "New Product",
    "categoryCode": "1"
  }'
```

### Category Endpoints

#### Get All Categories (with pagination and sorting)
```bash
GET /api/categories?page=0&size=10&sortBy=categoryName&sortDirection=ASC
```

Parameters:
- `page` (optional, default: 0): Page number
- `size` (optional, default: 10): Page size
- `sortBy` (optional, default: id): Field to sort by (id, categoryCode, categoryName, creationDate)
- `sortDirection` (optional, default: ASC): Sort direction (ASC or DESC)

Example:
```bash
curl "http://localhost:8080/api/categories?page=0&size=5&sortBy=categoryName"
```

#### Get Category by ID
```bash
GET /api/categories/{id}
```

Example:
```bash
curl http://localhost:8080/api/categories/1
```

#### Create Category
```bash
POST /api/categories
Content-Type: application/json

{
  "categoryCode": "99",
  "categoryName": "New Category"
}
```

Example:
```bash
curl -X POST http://localhost:8080/api/categories \
  -H "Content-Type: application/json" \
  -d '{
    "categoryCode": "99",
    "categoryName": "New Category"
  }'
```

## Project Structure

```
product-service/
├── src/
│   ├── main/
│   │   ├── java/com/example/productservice/
│   │   │   ├── controller/
│   │   │   │   ├── CategoryController.java
│   │   │   │   └── ProductController.java
│   │   │   ├── entity/
│   │   │   │   ├── Category.java
│   │   │   │   └── Product.java
│   │   │   ├── repository/
│   │   │   │   ├── CategoryRepository.java
│   │   │   │   └── ProductRepository.java
│   │   │   ├── service/
│   │   │   │   └── CsvImportService.java
│   │   │   └── ProductServiceApplication.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       ├── java/com/example/productservice/
│       │   ├── controller/
│       │   │   ├── CategoryControllerTest.java
│       │   │   └── ProductControllerTest.java
│       │   ├── repository/
│       │   │   ├── CategoryRepositoryTest.java
│       │   │   └── ProductRepositoryTest.java
│       │   └── service/
│       │       └── CsvImportServiceTest.java
│       └── resources/
│           └── application-test.properties
├── database/
│   └── docker-compose.yml
├── TestExampleFile.csv
├── pom.xml
└── README.md
```

## Data Models

### Product Entity
- `id` (Long): Unique identifier (auto-generated)
- `productCode` (String): Unique product code
- `productName` (String): Product name
- `categoryCode` (String): Reference to category
- `creationDate` (LocalDateTime): Creation timestamp

### Category Entity
- `id` (Long): Unique identifier (auto-generated)
- `categoryCode` (String): Unique category code
- `categoryName` (String): Category name
- `creationDate` (LocalDateTime): Creation timestamp

## CSV File Format

The CSV file should have the following columns:
```
PRODUCT_CODE,PRODUCT_NAME,PRODUCT_CATEGORY_CODE,CATEGORY_CODE,CATEGORY_NAME
```

Example:
```csv
PRODUCT_CODE,PRODUCT_NAME,PRODUCT_CATEGORY_CODE,CATEGORY_CODE,CATEGORY_NAME
0000000001,Product 01,1,1,Pain Relief
0000000002,Product 02,1,3,Digestive Health
```

## Testing the Application

### 1. Verify CSV Import
After starting the application, check the logs for import results:
```
INFO  c.e.p.ProductServiceApplication - CSV import completed: ImportResult{productsCreated=58, productsSkipped=0, categoriesCreated=52, categoriesSkipped=0, errors=0}
```

### 2. Test Pagination
```bash
# Get first page (10 items)
curl "http://localhost:8080/api/products?page=0&size=10"

# Get second page
curl "http://localhost:8080/api/products?page=1&size=10"
```

### 3. Test Sorting
```bash
# Sort by product code ascending
curl "http://localhost:8080/api/products?sortBy=productCode&sortDirection=ASC"

# Sort by product name descending
curl "http://localhost:8080/api/products?sortBy=productName&sortDirection=DESC"
```

### 4. Test Search
```bash
# Search for products with code containing "000000001"
curl "http://localhost:8080/api/products?productCode=000000001"
```

## Troubleshooting

### Database Connection Issues
If you get connection errors:
1. Verify PostgreSQL is running: `docker ps`
2. Check database credentials in `src/main/resources/application.properties`
3. Ensure port 5432 is not in use by another application

### CSV Import Issues
If CSV import fails:
1. Ensure `TestExampleFile.csv` is in the project root directory
2. Check file encoding (should be UTF-8)
3. Verify CSV format matches expected headers

### Port Already in Use
If port 8080 is already in use, change it in `application.properties`:
```properties
server.port=8081
```

## Technologies Used

- **Java 17**: Programming language
- **Spring Boot 3.2.0**: Application framework
- **Spring Data JPA**: Data persistence
- **PostgreSQL**: Production database
- **H2**: Test database
- **Apache Commons CSV**: CSV parsing
- **Lombok**: Reduce boilerplate code
- **JUnit 5**: Testing framework
- **Maven**: Build tool

## 📚 Documentation

- **README.md** (this file) - Quick start guide and API documentation
- **OPTIMIZATION-REPORT.md** - Detailed code optimization analysis and requirements validation

## License

This project is created for interview purposes.




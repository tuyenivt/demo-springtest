# Product Service

This project only focus on demo spring testing.
A Spring Boot 4 microservice demonstrating RESTful CRUD operations for product management using Spring Data JDBC.

## Tech Stack

- **Java 25**
- **Spring Boot 4.1**
- **Spring Data JDBC** (not JPA/Hibernate)
- **H2 Database** (in-memory for testing)
- **Lombok** for boilerplate reduction
- **JUnit 5 + DBUnit** for testing

## Project Structure

```
src/main/java/com/coloza/demo/springtest/
├── ProductServiceApplication.java    # Entry point
├── model/
│   └── Product.java                  # Domain entity
├── repository/
│   ├── ProductRepository.java        # Repository interface
│   └── ProductRepositoryImpl.java    # JDBC implementation
├── service/
│   ├── ProductService.java           # Service interface
│   └── ProductServiceImpl.java       # Business logic
└── web/
    └── ProductController.java        # REST endpoints
```

## API Endpoints

| Method | Endpoint        | Description        | Response                    |
|--------|-----------------|--------------------|-----------------------------|
| GET    | `/product/{id}` | Get product by ID  | 200 OK / 404 Not Found      |
| GET    | `/products`     | List all products  | 200 OK                      |
| POST   | `/product`      | Create new product | 201 Created                 |
| PUT    | `/product/{id}` | Update product     | 200 OK / 409 Conflict / 404 |
| DELETE | `/product/{id}` | Delete product     | 200 OK / 404 / 500          |

## Product Model

```json
{
  "id": 1,
  "name": "Product Name",
  "quantity": 100,
  "version": 1
}
```

## Optimistic Locking

The service implements optimistic concurrency control using ETags:

- **GET/POST/PUT** responses include `ETag` header with version number
- **PUT** requests require `If-Match` header matching current version
- Version mismatch returns **409 Conflict**

### Example Update Flow

```bash
# 1. Get product (note the ETag)
curl -i http://localhost:8080/product/1
# ETag: "1"

# 2. Update with If-Match header
curl -X PUT http://localhost:8080/product/1 \
  -H "Content-Type: application/json" \
  -H "If-Match: 1" \
  -d '{"id":1,"name":"Updated Name","quantity":50,"version":1}'
```

## Running the Service

```bash
# From project root
./gradlew :product-service:bootRun

# Or build and run JAR
./gradlew :product-service:build
java -jar product-service/build/libs/product-service-0.0.1-SNAPSHOT.jar
```

## Running Tests

```bash
# All tests
./gradlew :product-service:test

# With test report
./gradlew :product-service:test --info
```

## Test Coverage

- **Unit Tests**: Service layer with mocked repository
- **Controller Tests**: MockMvc with mocked service
- **Repository Tests**: H2 in-memory database with DBUnit
- **Integration Tests**: Full application context with real database

## Architecture

```
┌─────────────────┐
│  Controller     │  ← HTTP requests/responses, ETag handling
├─────────────────┤
│  Service        │  ← Business logic, version initialization
├─────────────────┤
│  Repository     │  ← Data access via JdbcTemplate
├─────────────────┤
│  Database       │  ← H2 (test) / configurable (prod)
└─────────────────┘
```

## Configuration

Default Spring Boot configuration is used. Override in `application.properties`:

```properties
# Server port (default: 8080)
server.port=8080

# Database (default: H2 in-memory)
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.username=sa
spring.datasource.password=
```

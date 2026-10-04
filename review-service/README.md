# Review Service

This project only focus on demo spring testing.
A Spring Boot 4 microservice for managing product reviews with MongoDB persistence.

## Overview

This service provides REST APIs for creating, reading, updating, and deleting product reviews. Each review is associated with a product and can contain multiple review entries from different users.

## Tech Stack

- **Java 25+** with Spring Boot 4.1
- **Spring Data MongoDB** for data access
- **MongoDB 8.x** as the database
- **Lombok** for boilerplate reduction
- **JUnit 5** + **Testcontainers** for testing

## Project Structure

```
review-service/
├── src/main/java/com/coloza/demo/springtest/
│   ├── ReviewServiceApplication.java    # Application entry point
│   ├── model/
│   │   ├── Review.java                  # MongoDB document
│   │   └── ReviewEntry.java             # Embedded review entry
│   ├── repository/
│   │   └── ReviewRepository.java        # Data access layer
│   ├── service/
│   │   ├── ReviewService.java           # Service interface
│   │   └── ReviewServiceImpl.java       # Service implementation
│   └── web/
│       └── ReviewController.java        # REST controller
└── src/test/
    ├── java/                            # Unit and integration tests
    └── resources/data/review/           # Test fixtures (JSON)
```

## API Endpoints

| Method | Endpoint                    | Description                  |
|--------|-----------------------------|------------------------------|
| GET    | `/review/{id}`              | Get review by ID             |
| GET    | `/reviews`                  | Get all reviews              |
| GET    | `/reviews?productId={id}`   | Get review by product ID     |
| POST   | `/review`                   | Create new review            |
| POST   | `/review/{productId}/entry` | Add entry to existing review |
| DELETE | `/review/{id}`              | Delete review                |

## Data Model

### Review
```json
{
  "id": "string",
  "productId": 1,
  "version": 1,
  "entries": [ReviewEntry]
}
```

### ReviewEntry
```json
{
  "username": "string",
  "date": "2024-01-01T00:00:00",
  "review": "string"
}
```

## Running the Service

### Prerequisites
- Java 25+
- MongoDB 8.x running locally or via Docker

### Build
```bash
./gradlew :review-service:build
```

### Run
```bash
./gradlew :review-service:bootRun
```

### Test
```bash
./gradlew :review-service:test
```

## Configuration

Configure MongoDB connection in `application.properties`:

```properties
spring.data.mongodb.uri=mongodb://localhost:27017/reviews
```

## HTTP Response Headers

- **ETag**: Contains the review version for cache validation
- **Location**: URI of the created/updated resource

# Inventory Service

This project only focus on demo spring testing.
A Spring Boot 4 microservice that acts as a gateway/client to an external Inventory Management system.

## Overview

This service provides REST endpoints for inventory management operations by proxying requests to a backend Inventory Manager Service. It does not persist data directly but serves as an adapter/facade layer.

## Tech Stack

- **Java 25+**
- **Spring Boot 4.1**
- **Spring Web MVC**
- **Lombok** - Boilerplate reduction
- **WireMock 4.4** - HTTP mocking for tests

## Project Structure

```
inventory-service/
├── src/main/java/com/coloza/demo/springtest/
│   ├── InventoryServiceApplication.java    # Application entry point
│   ├── model/
│   │   ├── InventoryRecord.java            # Inventory data model
│   │   └── PurchaseRecord.java             # Purchase request DTO
│   ├── service/
│   │   ├── InventoryService.java           # Service interface
│   │   └── InventoryServiceImpl.java       # Implementation (RestTemplate)
│   └── web/
│       └── InventoryController.java        # REST controller
├── src/main/resources/
│   └── application.properties              # Configuration
└── src/test/
    ├── java/.../
    │   ├── integration/                    # Integration tests
    │   ├── service/                        # Service layer tests
    │   └── web/                            # Controller tests
    └── resources/
        ├── __files/json/                   # Mock response data
        └── mappings/                       # WireMock mappings
```

## API Endpoints

### Get Inventory by Product ID

```http
GET /inventory/{id}
```

**Response:**
- `200 OK` - Returns `InventoryRecord` with `Location` header
- `404 Not Found` - Product not found

**Example Response:**
```json
{
  "productId": 1,
  "quantity": 500,
  "productName": "Super Great Product",
  "productCategory": "Great Products"
}
```

### Process Purchase

```http
POST /inventory/purchase-record
Content-Type: application/json

{
  "productId": 1,
  "quantityPurchased": 5
}
```

**Response:**
- `200 OK` - Returns updated `InventoryRecord` with `Location` header
- `404 Not Found` - Product not found

## Configuration

| Property                   | Description                           | Default                     |
|----------------------------|---------------------------------------|-----------------------------|
| `inventoryManager.baseUrl` | Base URL of Inventory Manager Service | `http://somehost/inventory` |

## Running the Service

```bash
# From project root
./gradlew :inventory-service:bootRun

# Or build and run JAR
./gradlew :inventory-service:build
java -jar inventory-service/build/libs/inventory-service-0.0.1-SNAPSHOT.jar
```

## Running Tests

```bash
# Run all tests
./gradlew :inventory-service:test

# Run specific test class
./gradlew :inventory-service:test --tests "InventoryControllerTest"
```

## Test Coverage

| Test Class                        | Type        | Description                        |
|-----------------------------------|-------------|------------------------------------|
| `InventoryControllerTest`         | Unit        | Controller layer with MockMvc      |
| `InventoryServiceTest`            | Unit        | Service with programmatic WireMock |
| `InventoryServiceMappingTest`     | Unit        | Service with file-based WireMock   |
| `InventoryServiceIntegrationTest` | Integration | Full request flow                  |

## Architecture Notes

- **No Database**: This service does not have a persistence layer
- **REST Client Pattern**: Uses `RestTemplate` to call external Inventory Manager
- **Fault Tolerance**: HTTP errors are converted to `Optional.empty()` responses
- **Stateless**: No session or state management

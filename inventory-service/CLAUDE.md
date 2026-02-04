# CLAUDE.md

## Quick Reference

**Module:** `inventory-service`
**Package:** `com.coloza.demo.springtest`
**Port:** Not explicitly configured (default 8080)
**External Dependency:** Inventory Manager Service at `inventoryManager.baseUrl`

## Key Files

| File                        | Purpose                                                                  |
|-----------------------------|--------------------------------------------------------------------------|
| `InventoryController.java`  | REST endpoints: GET `/inventory/{id}`, POST `/inventory/purchase-record` |
| `InventoryServiceImpl.java` | RestTemplate calls to external Inventory Manager                         |
| `InventoryRecord.java`      | Model: productId, quantity, productName, productCategory                 |
| `PurchaseRecord.java`       | DTO: productId, quantityPurchased                                        |

## Architecture Pattern

```
Client → InventoryController → InventoryService → RestTemplate → External Inventory Manager
```

This is a **gateway/facade service** - no database, just proxies to external service.

## Testing Strategy

- **WireMock on port 10000** for mocking external HTTP calls
- Test `application.properties` overrides `inventoryManager.baseUrl=http://localhost:10000/inventory`
- Mappings in `src/test/resources/mappings/`
- Response files in `src/test/resources/__files/json/`

## Common Commands

```bash
# Build
./gradlew :inventory-service:build

# Run tests
./gradlew :inventory-service:test

# Run application
./gradlew :inventory-service:bootRun
```

## Dependencies

- `spring-boot-starter-webmvc`
- `lombok`
- `wiremock-spring-boot:4.0.9` (test)

## Error Handling

Service returns `Optional.empty()` on:
- `HttpClientErrorException` (4xx errors)
- `HttpServerErrorException` (5xx errors)
- `ResourceAccessException` (timeouts, connection errors)

Controller converts empty Optional to 404 response.

## Notes for Future Work

- Consider adding circuit breaker (Resilience4j) for external calls
- RestTemplate is used; could migrate to WebClient for reactive support
- No validation annotations on DTOs
- No OpenAPI/Swagger documentation

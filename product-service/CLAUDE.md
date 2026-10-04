# CLAUDE.md

## Quick Reference

**Package**: `com.coloza.demo.springtest`
**Java**: 25 | **Spring Boot**: 4.1.1 | **Data Access**: Spring Data JDBC (not JPA)

## Key Files

| Purpose        | File                                    |
|----------------|-----------------------------------------|
| Entry point    | `ProductServiceApplication.java`        |
| Domain model   | `model/Product.java`                    |
| REST API       | `web/ProductController.java`            |
| Business logic | `service/ProductServiceImpl.java`       |
| Data access    | `repository/ProductRepositoryImpl.java` |
| DB schema      | `resources/schema.sql`                  |

## Architecture Decisions

- **No JPA/Hibernate** - Uses raw `JdbcTemplate` for SQL control
- **Optimistic locking** via `version` field + ETag headers
- **Thin service layer** - Delegates to repository, initializes version
- **Constructor injection** everywhere (Lombok `@RequiredArgsConstructor`)

## API Summary

```
GET    /product/{id}  → 200 + ETag | 404
GET    /products      → 200 (list)
POST   /product       → 201 + Location + ETag
PUT    /product/{id}  → 200 | 409 (version mismatch) | 404
DELETE /product/{id}  → 200 | 404 | 500
```

## Build Commands

```bash
./gradlew :product-service:bootRun     # Run
./gradlew :product-service:test        # Test
./gradlew :product-service:build       # Build JAR
```

## Test Structure

- `ProductServiceTest` - Unit test with mocked repo
- `ProductControllerTest` - MockMvc controller test
- `ProductRepositoryTest` - H2 + DBUnit integration
- `ProductServiceIntegrationTest` - Full context E2E

## Common Patterns

**Creating products**: Service sets `version=1` automatically
**Updating products**: Requires `If-Match` header with current version
**Version increment**: Controller increments version on successful update

## Database Schema

```sql
CREATE TABLE products (
    id INTEGER AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(128) NOT NULL,
    quantity INTEGER NOT NULL,
    version INTEGER NOT NULL
);
```

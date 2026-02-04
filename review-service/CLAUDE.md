# CLAUDE.md

## Quick Reference

- **Package**: `com.coloza.demo.springtest`
- **Database**: MongoDB (collection: `Reviews`)
- **Port**: Default Spring Boot port (8080)
- **Build**: Gradle (`./gradlew :review-service:build`)

## Architecture

```
Controller (ReviewController)
    ↓
Service (ReviewService → ReviewServiceImpl)
    ↓
Repository (ReviewRepository extends MongoRepository)
    ↓
MongoDB (Reviews collection)
```

## Key Files

| File                     | Purpose                                           |
|--------------------------|---------------------------------------------------|
| `ReviewController.java`  | REST endpoints at `/review` and `/reviews`        |
| `ReviewServiceImpl.java` | Business logic, version management                |
| `ReviewRepository.java`  | MongoDB CRUD + `findByProductId()`                |
| `Review.java`            | Document with `@Document(collection = "Reviews")` |
| `ReviewEntry.java`       | Embedded entry (username, date, review text)      |

## Patterns Used

- **Layered architecture**: Controller → Service → Repository
- **Repository pattern**: Spring Data MongoDB
- **Embedded documents**: ReviewEntry inside Review
- **Version tracking**: Incremented on update, exposed via ETag header

## Testing

- **Unit tests**: Mocked with `@MockitoBean`
- **Integration tests**: Real MongoDB via Testcontainers (covers all CRUD endpoints)
- **Base class**: `AbstractMongoIT` provides `loadData()` helper
- **Test data**: `src/test/resources/data/review/sample.json`

## Common Commands

```bash
# Run tests
./gradlew :review-service:test

# Run application
./gradlew :review-service:bootRun

# Build only
./gradlew :review-service:build
```

## API Quick Reference

```bash
# Get all reviews
GET /reviews

# Get by product
GET /reviews?productId=1

# Get by ID
GET /review/{id}

# Create review
POST /review
Content-Type: application/json
{"productId": 1, "entries": [{"username": "user1", "review": "Great!"}]}

# Add entry to existing review
POST /review/{productId}/entry
Content-Type: application/json
{"username": "user2", "review": "Nice product"}

# Delete
DELETE /review/{id}
```

## Notes

- Version starts at 1, increments on each update
- ETag header contains version number
- Location header set on POST responses
- Date auto-populated on entry creation

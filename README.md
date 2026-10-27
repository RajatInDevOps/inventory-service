# Inventory Service

A warehouse inventory API, written with Spring Boot.

## Running it

```bash
./mvnw spring-boot:run          # http://localhost:8080
```

## The API

| Method | Path                    | What it does                        |
|--------|-------------------------|-------------------------------------|
| GET    | `/health`               | liveness, plus the item count       |
| GET    | `/items`                | every item; `?below=N` for low stock|
| GET    | `/items/{id}`           | one item                            |
| POST   | `/items/{id}/reserve`   | take stock out (409 when short)     |
| POST   | `/items/{id}/restock`   | put stock in                        |

## Tests

```bash
mvn test
```

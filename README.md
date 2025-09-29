# RAG Chat Storage Microservice

Production-ready microservice to persist chat sessions/messages for a RAG chatbot.

## Features
- Sessions CRUD: create, list (pagination, favorite filter), rename, favorite/unfavorite, soft delete
- Messages: append + list (pagination), optional JSONB context/metadata
- API Key auth (`X-API-Key`), rate-limiting
- Global error handling, CORS, logs ready
- Health checks (Actuator), Swagger/OpenAPI
- Dockerized Postgres + pgAdmin + App
- Flyway migrations

## Quick Start (Docker Compose)

1. Copy env file and adjust values:
   ```bash
   cp .env.example .env
   ```

2. Build the app:
   ```bash
   mvn -q -DskipTests package
   ```

3. Run services:
   ```bash
   docker compose up --build
   ```

- Swagger UI: http://localhost:8080/swagger-ui.html  
- Health: `GET http://localhost:8080/actuator/health`  
- pgAdmin: http://localhost:5050 (use env creds)

> All API calls must include header `X-API-Key: <one of API_KEYS>`

## REST API (v1)

### Create session
`POST /v1/sessions`
```json
{ "userId": "u_123", "title": "Optional" }
```

### List sessions
`GET /v1/sessions?userId=u_123&favorite=true&page=0&size=20&q=hello`

### Rename
`PATCH /v1/sessions/{id}/rename`
```json
{ "title": "New name" }
```

### Favorite toggle
`PATCH /v1/sessions/{id}/favorite`
```json
{ "favorite": true }
```

### Delete (soft)
`DELETE /v1/sessions/{id}`

### Append message
`POST /v1/sessions/{id}/messages`
```json
{
  "sender": "USER",
  "content": "Hi",
  "context": { "docId":"A1", "score":0.9 },
  "metadata": { "model":"gpt-4o-mini", "latencyMs":420 }
}
```

### Get messages (paginated)
`GET /v1/sessions/{id}/messages?page=0&size=50`

## Configuration

See `.env.example` for variables. All are optional with sensible defaults.

- `API_KEY` — `rag-chat-token`)
- `DB_URL`, `DB_USER`, `DB_PASS`
- `CORS_ALLOWED_ORIGINS`
- `SECURITY_ALLOWLIST` (paths to bypass API key, e.g., health + swagger)

## Build Locally (without Docker)
- Start a local Postgres and set `DB_URL`, `DB_USER`, `DB_PASS`
- Run: `mvn spring-boot:run`

## Tests
Minimal service test included:
```bash
mvn test
```

## Notes
- Soft delete marks sessions via `deleted_at`; messages remain but are not listed after delete.
- JSONB fields (`context`, `metadata`) require PostgreSQL.

# MemoApp Backend

A Spring Boot REST API for managing memos/notes, built with Java 21, PostgreSQL database, and containerized with Docker.

## Technology Stack

- **Language**: Java 21
- **Framework**: Spring Boot 3.2.2
- **Database**: PostgreSQL 15
- **Build Tool**: Gradle 8.8
- **Containerization**: Docker & Docker Compose
- **Testing**: JUnit Jupiter, Testcontainers, H2 (in-memory)
- **ORM**: Spring Data JPA / Hibernate

## Project Structure

```
MemoApp/
├── app/                                    # Main application module
│   ├── build.gradle                       # Spring Boot build configuration
│   └── src/
│       ├── main/
│       │   ├── java/memoapp/
│       │   │   ├── App.java               # Spring Boot main class
│       │   │   ├── controller/
│       │   │   │   └── MemoController.java # REST API endpoints
│       │   │   ├── entity/
│       │   │   │   └── Memo.java          # JPA entity
│       │   │   ├── repository/
│       │   │   │   └── MemoRepository.java # Data access layer
│       │   │   ├── service/
│       │   │   │   └── MemoService.java   # Business logic layer
│       │   │   └── config/
│       │   │       └── CorsConfig.java    # CORS configuration
│       │   └── resources/
│       │       └── application.yml        # Spring Boot configuration
│       └── test/                          # Comprehensive test suite
├── Dockerfile                             # Docker container configuration
├── docker-compose.yml                     # Multi-container setup
└── README.md                              # This file
```

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/api/memos` | List all memos |
| `GET` | `/api/memos/{id}` | Get memo by ID |
| `POST` | `/api/memos` | Create new memo |
| `PUT` | `/api/memos/{id}` | Update existing memo |
| `DELETE` | `/api/memos/{id}` | Delete memo |

## Getting Started

### Prerequisites

- Java 21
- Docker and Docker Compose
- Gradle (or use included wrapper)

### Running with Docker (Recommended)

1. Clone the repository
2. Build and start all services:
```bash
docker-compose up --build
```

3. The API will be available at `http://localhost:8081`

### Running Locally

1. Start PostgreSQL database (or use Docker):
```bash
docker run --name postgres-memo \
  -e POSTGRES_DB=memoapp \
  -e POSTGRES_USER=memoapp \
  -e POSTGRES_PASSWORD=password \
  -p 5432:5432 -d postgres:15
```

2. Build and run the application:
```bash
./gradlew bootRun
```

### Running Tests

```bash
# Run all tests
./gradlew test

# Run tests with coverage
./gradlew test jacocoTestReport
```

## API Usage Examples

### Create a memo
```bash
curl -X POST http://localhost:8081/api/memos \
  -H "Content-Type: application/json" \
  -d '{"title":"My First Memo","content":"This is the content of my memo"}'
```

### Get all memos
```bash
curl http://localhost:8081/api/memos
```

### Get a specific memo
```bash
curl http://localhost:8081/api/memos/1
```

### Update a memo
```bash
curl -X PUT http://localhost:8081/api/memos/1 \
  -H "Content-Type: application/json" \
  -d '{"title":"Updated Memo","content":"Updated content"}'
```

### Delete a memo
```bash
curl -X DELETE http://localhost:8081/api/memos/1
```

## Database Schema

The `memos` table contains:
- `id` (BIGINT, Primary Key, Auto-increment)
- `title` (VARCHAR, Not Null)
- `content` (TEXT)
- `created_at` (TIMESTAMP, Not Null)
- `updated_at` (TIMESTAMP)

## Configuration

### Database Configuration
- **Host**: localhost:5432
- **Database**: memoapp
- **Username**: memoapp
- **Password**: password

### CORS Configuration
- Configured for localhost origins
- Supports all standard HTTP methods
- Allows all headers

## Development

### Gradle Commands
```bash
# Build the project
./gradlew build

# Run tests
./gradlew test

# Run the application
./gradlew bootRun

# Clean build artifacts
./gradlew clean
```

### Docker Commands
```bash
# Build and start services
docker-compose up --build

# Start in background
docker-compose up -d

# Stop services
docker-compose down

# View logs
docker-compose logs -f app
```

## License

This project is for educational purposes.
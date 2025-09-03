# MemoApp Project Context

## Project Overview
MemoApp is a Spring Boot REST API for managing memos/notes, built with Java 21, PostgreSQL database, and containerized with Docker.

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
│       │   │   └── service/
│       │   │       └── MemoService.java   # Business logic layer
│       │   └── resources/
│       │       └── application.yml        # Spring Boot configuration
│       └── test/java/memoapp/             # Test source code
├── gradle/                                # Gradle wrapper and version catalog
├── Dockerfile                             # Docker container configuration
├── docker-compose.yml                     # Multi-container setup
├── gradlew                                # Gradle wrapper script (Unix)
├── gradlew.bat                            # Gradle wrapper script (Windows)
└── settings.gradle                        # Multi-project build settings
```

## Technology Stack
- **Language**: Java 21
- **Framework**: Spring Boot 3.2.2
- **Database**: PostgreSQL 15
- **Build Tool**: Gradle 8.8
- **Containerization**: Docker & Docker Compose
- **Testing Framework**: JUnit Jupiter, Testcontainers
- **ORM**: Spring Data JPA / Hibernate

## Key Components

### Spring Boot Application (`App.java`)
- Main Spring Boot application class with `@SpringBootApplication`
- Entry point for the REST API server

### REST API (`MemoController.java`)
- RESTful endpoints for CRUD operations:
  - `GET /api/memos` - List all memos
  - `GET /api/memos/{id}` - Get memo by ID
  - `POST /api/memos` - Create new memo
  - `PUT /api/memos/{id}` - Update existing memo
  - `DELETE /api/memos/{id}` - Delete memo

### Data Model (`Memo.java`)
- JPA entity with fields: id, title, content, createdAt, updatedAt
- Automatic timestamp management with `@PrePersist` and `@PreUpdate`
- Validation with `@NotBlank` for required fields

### Data Access (`MemoRepository.java`)
- Spring Data JPA repository interface
- Extends `JpaRepository` for standard CRUD operations

### Business Logic (`MemoService.java`)
- Service layer handling business logic
- Methods for all CRUD operations

## Configuration

### Database Configuration (`application.yml`)
- PostgreSQL connection settings
- JPA/Hibernate configuration with DDL auto-update
- SQL logging enabled for development

### Docker Configuration
- **Dockerfile**: Multi-stage build for Spring Boot app
- **docker-compose.yml**: 
  - PostgreSQL database service
  - Spring Boot application service
  - Network and volume configuration

## Development Commands

### Local Development
```bash
# Build the project
./gradlew build

# Run tests
./gradlew test

# Run locally (requires PostgreSQL)
./gradlew bootRun
```

### Docker Commands
```bash
# Build and start all services
docker-compose up --build

# Start services in background
docker-compose up -d

# Stop services
docker-compose down

# View logs
docker-compose logs -f app
```

### API Testing
```bash
# Create a memo
curl -X POST http://localhost:8081/api/memos \
  -H "Content-Type: application/json" \
  -d '{"title":"Test Memo","content":"This is a test memo"}'

# Get all memos
curl http://localhost:8081/api/memos

# Get memo by ID
curl http://localhost:8081/api/memos/1
```

## Database Schema
The `memos` table is automatically created with:
- `id` (BIGINT, Primary Key, Auto-increment)
- `title` (VARCHAR, Not Null)
- `content` (TEXT)
- `created_at` (TIMESTAMP, Not Null)
- `updated_at` (TIMESTAMP)

## Development Notes
- Uses Spring Boot 3.x with Java 21 features
- PostgreSQL runs on port 5432, Spring Boot on port 8081
- Database credentials: username=memoapp, password=password, db=memoapp
- Hibernate DDL auto-update creates/updates tables automatically
- Comprehensive logging enabled for SQL queries and web requests
- CORS configuration enabled for Angular frontend (localhost:6565)
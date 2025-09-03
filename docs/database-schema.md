# Database Schema and Configuration

## Overview

The MemoApp uses PostgreSQL as its primary database with Spring Data JPA for object-relational mapping. The database schema is automatically managed by Hibernate with the `ddl-auto: update` configuration.

## Database Configuration

### Connection Details

| Setting | Value | Description |
|---------|-------|-------------|
| **Host** | localhost | Database server host |
| **Port** | 5432 | PostgreSQL default port |
| **Database** | memoapp | Database name |
| **Username** | memoapp | Database user |
| **Password** | password | Database password |
| **Driver** | org.postgresql.Driver | JDBC driver |

### Spring Configuration

Located in `/MemoApp_Backend/app/src/main/resources/application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/memoapp
    username: memoapp
    password: password
    driver-class-name: org.postgresql.Driver
  
  jpa:
    hibernate:
      ddl-auto: update          # Schema management strategy
    show-sql: true              # Log SQL queries
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
        format_sql: true        # Format SQL output
  
  sql:
    init:
      mode: always             # Initialize SQL scripts
```

### Docker Configuration

Database container configuration in `docker-compose.yml`:

```yaml
postgres:
  image: postgres:15-alpine
  container_name: memo-postgres
  environment:
    POSTGRES_DB: memoapp
    POSTGRES_USER: memoapp
    POSTGRES_PASSWORD: password
  ports:
    - "5432:5432"
  volumes:
    - postgres_data:/var/lib/postgresql/data
```

## Schema Design

### Entity Relationship Diagram

```
┌─────────────────────────────────────┐
│               memos                 │
├─────────────────────────────────────┤
│ id (PK)          │ BIGSERIAL        │
│ title            │ VARCHAR(255)     │
│ content          │ TEXT             │
│ created_at       │ TIMESTAMP        │
│ updated_at       │ TIMESTAMP        │
└─────────────────────────────────────┘
```

### Table: memos

The `memos` table stores all memo records in the system.

#### Columns

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| `id` | BIGSERIAL | PRIMARY KEY, NOT NULL, AUTO_INCREMENT | Unique identifier for each memo |
| `title` | VARCHAR(255) | NOT NULL | Title of the memo (max 255 characters) |
| `content` | TEXT | NULL | Content body of the memo (unlimited length) |
| `created_at` | TIMESTAMP | NOT NULL | Timestamp when the memo was created |
| `updated_at` | TIMESTAMP | NULL | Timestamp when the memo was last updated |

#### Indexes

```sql
-- Primary key index (automatically created)
CREATE UNIQUE INDEX pk_memos ON memos (id);

-- Optional indexes for performance (not currently implemented)
-- CREATE INDEX idx_memos_created_at ON memos (created_at);
-- CREATE INDEX idx_memos_title ON memos (title);
```

#### DDL Script

```sql
CREATE SEQUENCE IF NOT EXISTS memos_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE memos (
    id BIGINT NOT NULL DEFAULT nextval('memos_seq'),
    title VARCHAR(255) NOT NULL,
    content TEXT,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    PRIMARY KEY (id)
);

ALTER SEQUENCE memos_seq OWNED BY memos.id;
```

## JPA Entity Mapping

### Memo Entity

Located in `/MemoApp_Backend/app/src/main/java/memoapp/entity/Memo.java`:

```java
@Entity
@Table(name = "memos")
public class Memo {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Title is required")
    @Column(nullable = false)
    private String title;
    
    @Column(columnDefinition = "TEXT")
    private String content;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
```

### Annotations Explained

| Annotation | Purpose |
|------------|---------|
| `@Entity` | Marks the class as a JPA entity |
| `@Table(name = "memos")` | Specifies the database table name |
| `@Id` | Marks the primary key field |
| `@GeneratedValue(strategy = GenerationType.IDENTITY)` | Auto-increment primary key |
| `@NotBlank` | Validation: field cannot be null or empty |
| `@Column(nullable = false)` | Database constraint: column cannot be NULL |
| `@Column(columnDefinition = "TEXT")` | Specifies database column type |
| `@Column(updatable = false)` | Prevents updates to this field |
| `@PrePersist` | Method executed before entity insertion |
| `@PreUpdate` | Method executed before entity update |

## Data Access Layer

### Repository Interface

Located in `/MemoApp_Backend/app/src/main/java/memoapp/repository/MemoRepository.java`:

```java
@Repository
public interface MemoRepository extends JpaRepository<Memo, Long> {
    // Inherits all CRUD operations from JpaRepository
    
    // Custom queries can be added here, for example:
    // List<Memo> findByTitleContaining(String title);
    // List<Memo> findByCreatedAtBefore(LocalDateTime date);
}
```

### Available Methods

The repository provides these methods through `JpaRepository`:

| Method | Description |
|--------|-------------|
| `findAll()` | Retrieve all memos |
| `findById(Long id)` | Find memo by ID |
| `save(Memo memo)` | Create or update memo |
| `deleteById(Long id)` | Delete memo by ID |
| `existsById(Long id)` | Check if memo exists |
| `count()` | Count total memos |

## Database Management Strategies

### Development Environment

```yaml
spring:
  jpa:
    hibernate:
      ddl-auto: update    # Automatically update schema
    show-sql: true        # Show SQL queries in logs
```

**Behavior**: Hibernate automatically creates or updates tables based on entity definitions.

### Testing Environment

```yaml
spring:
  jpa:
    hibernate:
      ddl-auto: create-drop  # Create schema on startup, drop on shutdown
  datasource:
    url: jdbc:h2:mem:testdb  # In-memory H2 database
```

**Behavior**: Fresh database for each test run.

### Production Environment (Recommended)

```yaml
spring:
  jpa:
    hibernate:
      ddl-auto: validate     # Validate schema only, no automatic changes
    show-sql: false         # Disable SQL logging
```

**Behavior**: Hibernate validates that the database schema matches entity definitions but doesn't modify the database.

## Migration Strategy

### Using Flyway (Recommended for Production)

Add Flyway dependency to `build.gradle`:

```gradle
implementation 'org.flywaydb:flyway-core'
```

Create migration files in `src/main/resources/db/migration/`:

```sql
-- V1__Create_memos_table.sql
CREATE SEQUENCE IF NOT EXISTS memos_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE memos (
    id BIGINT NOT NULL DEFAULT nextval('memos_seq'),
    title VARCHAR(255) NOT NULL,
    content TEXT,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    PRIMARY KEY (id)
);

ALTER SEQUENCE memos_seq OWNED BY memos.id;
```

### Manual Migration

For production deployments, manually apply schema changes:

```sql
-- Example: Adding an index
CREATE INDEX idx_memos_created_at ON memos (created_at DESC);

-- Example: Adding a column
ALTER TABLE memos ADD COLUMN category VARCHAR(50);
```

## Performance Considerations

### Indexing Strategy

Current indexes:
- Primary key on `id` (automatic)

Recommended additional indexes for larger datasets:

```sql
-- For date-based queries
CREATE INDEX idx_memos_created_at ON memos (created_at DESC);

-- For text search (requires full-text search extension)
CREATE INDEX idx_memos_title_gin ON memos USING gin(to_tsvector('english', title));
```

### Query Optimization

The current repository uses simple queries. For complex scenarios, consider:

```java
@Query("SELECT m FROM Memo m WHERE m.title LIKE %:keyword% ORDER BY m.createdAt DESC")
List<Memo> findByTitleContainingKeyword(@Param("keyword") String keyword);

@Query(value = "SELECT * FROM memos WHERE created_at >= :date", nativeQuery = true)
List<Memo> findRecentMemos(@Param("date") LocalDateTime date);
```

## Backup and Recovery

### Docker Volume Backup

```bash
# Create backup
docker run --rm -v memo_postgres_data:/data -v $(pwd):/backup alpine tar czf /backup/memo_backup.tar.gz -C /data .

# Restore backup
docker run --rm -v memo_postgres_data:/data -v $(pwd):/backup alpine tar xzf /backup/memo_backup.tar.gz -C /data
```

### PostgreSQL Backup

```bash
# Create backup
pg_dump -h localhost -U memoapp memoapp > memo_backup.sql

# Restore backup
psql -h localhost -U memoapp memoapp < memo_backup.sql
```

## Monitoring and Maintenance

### Health Checks

Spring Boot Actuator provides database health checks:

```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info
  endpoint:
    health:
      show-details: always
```

Access health information at: `http://localhost:1919/actuator/health`

### Database Metrics

Enable JPA metrics in application configuration:

```yaml
spring:
  jpa:
    properties:
      hibernate:
        generate_statistics: true
management:
  metrics:
    enable:
      jvm: true
      jdbc: true
```

### Logging

Configure database-related logging:

```yaml
logging:
  level:
    org.hibernate.SQL: DEBUG              # SQL queries
    org.hibernate.type.descriptor.sql: TRACE  # SQL parameters
    org.springframework.transaction: DEBUG     # Transaction management
```

## Security Considerations

### Database Security

1. **Connection Security**: Use SSL in production
   ```yaml
   spring:
     datasource:
       url: jdbc:postgresql://localhost:5432/memoapp?ssl=true
   ```

2. **User Privileges**: Create application-specific database user with minimal privileges
   ```sql
   CREATE USER memoapp_app WITH PASSWORD 'strong_password';
   GRANT SELECT, INSERT, UPDATE, DELETE ON memos TO memoapp_app;
   ```

3. **Environment Variables**: Store credentials in environment variables
   ```yaml
   spring:
     datasource:
       username: ${DB_USERNAME:memoapp}
       password: ${DB_PASSWORD:password}
   ```

### SQL Injection Prevention

- Use JPA/Hibernate parameterized queries (current implementation is safe)
- Avoid native SQL queries with string concatenation
- Enable SQL query validation in development
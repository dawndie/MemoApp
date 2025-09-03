# System Architecture Overview

## Architecture Pattern

The MemoApp follows a **three-tier layered architecture** with clear separation of concerns:

```
┌─────────────────────────────────────────────────────────────────────┐
│                        Presentation Layer                           │
│  ┌─────────────────────────────────────────────────────────────┐   │
│  │            Angular Frontend (Port 6565)                    │   │
│  │  • Components (MemoList, MemoForm)                         │   │
│  │  • Services (MemoService)                                  │   │
│  │  • Models & Routing                                        │   │
│  └─────────────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────────┘
                                    │ HTTP/REST
                                    ▼
┌─────────────────────────────────────────────────────────────────────┐
│                         Business Layer                              │
│  ┌─────────────────────────────────────────────────────────────┐   │
│  │           Spring Boot Backend (Port 1919)                  │   │
│  │  • REST Controllers (MemoController)                       │   │
│  │  • Business Logic (MemoService)                            │   │
│  │  • Exception Handling (GlobalExceptionHandler)            │   │
│  │  • Configuration (CORS, JPA)                               │   │
│  └─────────────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────────┘
                                    │ JDBC
                                    ▼
┌─────────────────────────────────────────────────────────────────────┐
│                          Data Layer                                 │
│  ┌─────────────────────────────────────────────────────────────┐   │
│  │           PostgreSQL Database (Port 5432)                  │   │
│  │  • Entity Mapping (Memo Entity)                            │   │
│  │  • Data Access (MemoRepository)                            │   │
│  │  • Schema Management (Hibernate DDL)                       │   │
│  └─────────────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────────┘
```

## Component Architecture

### Frontend Components (Angular)

```
                    ┌─────────────────┐
                    │    App Component │
                    │   (Root Shell)   │
                    └─────────┬───────┘
                              │
                    ┌─────────▼───────┐
                    │  RouterOutlet   │
                    └─────────┬───────┘
                              │
              ┌───────────────┼───────────────┐
              ▼               ▼               ▼
    ┌─────────────────┐ ┌─────────────────┐ ┌─────────────────┐
    │   MemoList      │ │   MemoForm      │ │   MemoService   │
    │   Component     │ │   Component     │ │   (Singleton)   │
    │                 │ │                 │ │                 │
    │ • Display List  │ │ • Create/Edit   │ │ • HTTP Calls    │
    │ • Delete Action │ │ • Form Validation│ │ • Error Handle  │
    │ • Navigation    │ │ • Submit/Cancel │ │ • Data Transform│
    └─────────────────┘ └─────────────────┘ └─────────────────┘
              │                 │                       │
              └─────────────────┼───────────────────────┘
                                │
                    ┌───────────▼───────────┐
                    │    Backend API        │
                    │  (HTTP/REST Calls)    │
                    └───────────────────────┘
```

### Backend Components (Spring Boot)

```
┌─────────────────────────────────────────────────────────────────┐
│                        Spring Boot Application                  │
├─────────────────────────────────────────────────────────────────┤
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐ │
│  │  Controller     │  │    Service      │  │   Repository    │ │
│  │     Layer       │  │     Layer       │  │     Layer       │ │
│  │                 │  │                 │  │                 │ │
│  │ MemoController  │─►│  MemoService    │─►│ MemoRepository  │ │
│  │                 │  │                 │  │                 │ │
│  │ • REST Endpoints│  │ • Business Logic│  │ • Data Access   │ │
│  │ • Request/Resp  │  │ • Validation    │  │ • CRUD Ops      │ │
│  │ • HTTP Status   │  │ • Transaction   │  │ • Query Methods │ │
│  └─────────────────┘  └─────────────────┘  └─────────────────┘ │
│           │                      │                      │      │
│           │        ┌─────────────▼───────────┐          │      │
│           │        │   Exception Handling   │          │      │
│           │        │                        │          │      │
│           │        │ GlobalExceptionHandler │          │      │
│           │        │ Custom Exceptions      │          │      │
│           │        └────────────────────────┘          │      │
│           │                                            │      │
│           └────────────────┬───────────────────────────┘      │
│                            │                                  │
│  ┌─────────────────────────▼─────────────────────────────┐    │
│  │                Configuration                          │    │
│  │  • CorsConfig (CORS settings)                        │    │
│  │  • JPA Configuration (Database settings)             │    │
│  │  • Application Properties (application.yml)          │    │
│  └───────────────────────────────────────────────────────┘    │
└─────────────────────────────────────────────────────────────────┘
                                    │
                                    ▼
                    ┌───────────────────────────┐
                    │     PostgreSQL Database   │
                    │                          │
                    │  • memos table           │
                    │  • Indexes               │
                    │  • Constraints           │
                    └───────────────────────────┘
```

## Design Patterns and Principles

### SOLID Principles Implementation

#### Single Responsibility Principle (SRP)
- **MemoController**: Handles only HTTP request/response mapping
- **MemoService**: Contains only business logic and validation
- **MemoRepository**: Manages only data access operations

#### Open/Closed Principle (OCP)
- **Service Layer**: Extensible through interfaces without modification
- **Exception Handling**: New exception types can be added without changing existing handlers

#### Liskov Substitution Principle (LSP)
- **Repository Interface**: Any implementation can replace JpaRepository
- **Service Interface**: Business logic can be swapped with different implementations

#### Interface Segregation Principle (ISP)
- **Focused Interfaces**: Repository provides only necessary CRUD operations
- **Specific Methods**: Each service method has a single, well-defined purpose

#### Dependency Inversion Principle (DIP)
- **Constructor Injection**: High-level modules depend on abstractions
- **Interface Dependencies**: Services depend on repository interfaces, not concrete classes

### Design Patterns Used

#### Repository Pattern
```java
@Repository
public interface MemoRepository extends JpaRepository<Memo, Long> {
    // Abstracts data access logic
}
```

#### Service Layer Pattern
```java
@Service
@Transactional(readOnly = true)
public class MemoService {
    // Encapsulates business logic
}
```

#### Model-View-Controller (MVC)
- **Model**: Memo entity and DTOs
- **View**: Angular components and templates
- **Controller**: REST controllers and Angular component logic

#### Observer Pattern (Frontend)
```typescript
// RxJS Observables for reactive programming
this.memoService.getAllMemos().subscribe({
  next: (memos) => this.memos = memos,
  error: (err) => this.handleError(err)
});
```

## Data Flow Architecture

### Create Memo Flow

```
┌─────────────┐    ┌─────────────┐    ┌─────────────┐    ┌─────────────┐
│   Angular   │    │   Spring    │    │    JPA      │    │ PostgreSQL  │
│   Component │    │   Boot      │    │ Repository  │    │  Database   │
└──────┬──────┘    └──────┬──────┘    └──────┬──────┘    └──────┬──────┘
       │                  │                  │                  │
       │ 1. HTTP POST     │                  │                  │
       ├─────────────────►│                  │                  │
       │                  │ 2. Validate      │                  │
       │                  │    & Process     │                  │
       │                  ├─────────────────►│                  │
       │                  │                  │ 3. Insert SQL    │
       │                  │                  ├─────────────────►│
       │                  │                  │                  │
       │                  │                  │ 4. Generated ID  │
       │                  │                  │◄─────────────────┤
       │                  │ 5. Return Entity │                  │
       │                  │◄─────────────────┤                  │
       │ 6. HTTP Response │                  │                  │
       │◄─────────────────┤                  │                  │
       │                  │                  │                  │
```

### Error Handling Flow

```
┌─────────────┐    ┌─────────────┐    ┌─────────────────┐
│   Angular   │    │   Spring    │    │  Exception      │
│   Component │    │   Boot      │    │  Handler        │
└──────┬──────┘    └──────┬──────┘    └─────────┬───────┘
       │                  │                     │
       │ 1. HTTP Request  │                     │
       ├─────────────────►│                     │
       │                  │ 2. Business Logic   │
       │                  │    Exception        │
       │                  ├────────────────────►│
       │                  │                     │
       │                  │ 3. HTTP Error       │
       │                  │    Response         │
       │                  │◄────────────────────┤
       │ 4. Error Response│                     │
       │◄─────────────────┤                     │
       │                  │                     │
       │ 5. Display Error │                     │
       │    Message       │                     │
       │                  │                     │
```

## Security Architecture

### Current Security Model

```
┌─────────────────────────────────────────────────────────────┐
│                      Security Layers                        │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  ┌─────────────────────────────────────────────────────┐   │
│  │            Input Validation (Level 1)               │   │
│  │  • Frontend: Angular Reactive Forms                 │   │
│  │  • Backend: Bean Validation (@NotBlank, etc.)      │   │
│  └─────────────────────────────────────────────────────┘   │
│                                                             │
│  ┌─────────────────────────────────────────────────────┐   │
│  │              CORS Configuration (Level 2)           │   │
│  │  • Allowed Origins: localhost:6565                  │   │
│  │  • Allowed Methods: GET, POST, PUT, DELETE          │   │
│  │  • Credentials: Not allowed                         │   │
│  └─────────────────────────────────────────────────────┘   │
│                                                             │
│  ┌─────────────────────────────────────────────────────┐   │
│  │           SQL Injection Prevention (Level 3)        │   │
│  │  • JPA/Hibernate Parameterized Queries             │   │
│  │  • No Dynamic SQL Construction                      │   │
│  │  • Repository Pattern Abstraction                   │   │
│  └─────────────────────────────────────────────────────┘   │
│                                                             │
│  ┌─────────────────────────────────────────────────────┐   │
│  │              Error Information (Level 4)            │   │
│  │  • Generic Error Messages for Users                 │   │
│  │  • Detailed Logging for Developers                  │   │
│  │  • No Stack Trace Exposure                          │   │
│  └─────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────┘
```

### Security Considerations for Production

1. **Authentication & Authorization**:
   - Spring Security integration
   - JWT token-based authentication
   - Role-based access control

2. **HTTPS/TLS**:
   - SSL certificate configuration
   - HTTP to HTTPS redirection
   - Secure cookie settings

3. **Database Security**:
   - Connection encryption
   - Database user privileges
   - Environment variable configuration

## Performance Architecture

### Caching Strategy

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│    Browser      │    │   Spring Boot   │    │   Database      │
│                 │    │                 │    │                 │
│ • HTTP Caching  │    │ • Application   │    │ • Query Cache   │
│ • Local Storage │    │   Cache         │    │ • Connection    │
│ • Component     │    │ • Redis (Future)│    │   Pooling       │
│   State         │    │                 │    │                 │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

### Database Optimization

```sql
-- Current Indexes
CREATE UNIQUE INDEX pk_memos ON memos (id);

-- Recommended Future Indexes
CREATE INDEX idx_memos_created_at ON memos (created_at DESC);
CREATE INDEX idx_memos_title_search ON memos USING gin(to_tsvector('english', title));
```

### Connection Pool Configuration

```yaml
spring:
  datasource:
    hikari:
      maximum-pool-size: 20
      connection-timeout: 300000
      idle-timeout: 600000
      max-lifetime: 1800000
```

## Monitoring and Observability

### Application Metrics

```
┌─────────────────────────────────────────────────────────────┐
│                   Observability Stack                       │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  ┌─────────────────────────────────────────────────────┐   │
│  │              Application Metrics                    │   │
│  │  • Spring Boot Actuator Endpoints                  │   │
│  │  • Custom Business Metrics                         │   │
│  │  • JVM and System Metrics                          │   │
│  └─────────────────────────────────────────────────────┘   │
│                                                             │
│  ┌─────────────────────────────────────────────────────┐   │
│  │                    Logging                          │   │
│  │  • Structured JSON Logging                         │   │
│  │  • Request/Response Logging                        │   │
│  │  • Error and Exception Logging                     │   │
│  └─────────────────────────────────────────────────────┘   │
│                                                             │
│  ┌─────────────────────────────────────────────────────┐   │
│  │                Health Checks                        │   │
│  │  • Database Connectivity                           │   │
│  │  • Application Health Status                       │   │
│  │  • Custom Health Indicators                        │   │
│  └─────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────┘
```

### Health Check Endpoints

- `/actuator/health` - Application health status
- `/actuator/info` - Application information
- `/actuator/metrics` - Application metrics
- `/actuator/prometheus` - Prometheus-compatible metrics

## Deployment Architecture

### Development Environment

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Angular Dev   │    │    Spring       │    │   PostgreSQL    │
│   Server        │    │    Boot         │    │   (Docker)      │
│                 │    │                 │    │                 │
│   Port 6565     │    │   Port 1919     │    │   Port 5432     │
│                 │    │                 │    │                 │
│   npm start     │    │ ./gradlew       │    │ docker-compose  │
│                 │    │ bootRun         │    │ up postgres     │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

### Production Environment (Docker)

```
┌─────────────────────────────────────────────────────────────┐
│                    Docker Network                           │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  ┌─────────────────┐    ┌─────────────────┐    ┌──────────┐ │
│  │   Nginx         │    │   Spring Boot   │    │PostgreSQL│ │
│  │   (Reverse      │    │   App           │    │ Database  │ │
│  │   Proxy)        │    │                 │    │           │ │
│  │                 │    │   Port 1919     │    │Port 5432  │ │
│  │   Port 80/443   │    │   (Internal)    │    │(Internal) │ │
│  └─────────────────┘    └─────────────────┘    └──────────┘ │
│           │                       │                    │     │
│           └───────────────────────┼────────────────────┘     │
│                                   │                          │
│  ┌────────────────────────────────▼──────────────────┐       │
│  │                Persistent Volumes                 │       │
│  │  • postgres_data (Database storage)               │       │
│  │  • app_logs (Application logs)                    │       │
│  └───────────────────────────────────────────────────┘       │
└─────────────────────────────────────────────────────────────┘
```

## Scalability Considerations

### Horizontal Scaling

```
                    ┌─────────────────┐
                    │   Load Balancer │
                    │    (Nginx)      │
                    └─────────┬───────┘
                              │
            ┌─────────────────┼─────────────────┐
            │                 │                 │
    ┌───────▼───────┐ ┌───────▼───────┐ ┌───────▼───────┐
    │  App Instance │ │  App Instance │ │  App Instance │
    │      #1       │ │      #2       │ │      #3       │
    └───────────────┘ └───────────────┘ └───────────────┘
            │                 │                 │
            └─────────────────┼─────────────────┘
                              │
                    ┌─────────▼───────┐
                    │   PostgreSQL    │
                    │   Database      │
                    │  (Primary +     │
                    │   Read Replicas)│
                    └─────────────────┘
```

### Vertical Scaling

- **Database**: Increase CPU, RAM, and storage capacity
- **Application**: Increase JVM heap size and container resources
- **Caching**: Add Redis for application-level caching

## Technology Integration Points

### Frontend to Backend Communication

```
Angular Service ──► HTTP Client ──► REST API ──► Spring Controller
      │                                              │
      │              Error Handling                  │
      └──────────────────────────────────────────────┘
                         │
           ┌─────────────▼─────────────┐
           │     Error Response        │
           │  • Status Code            │
           │  • Error Message          │
           │  • Timestamp              │
           └───────────────────────────┘
```

### Backend to Database Communication

```
Spring Service ──► JPA Repository ──► Hibernate ──► JDBC ──► PostgreSQL
      │                                                         │
      │                Transaction Management                   │
      └─────────────────────────────────────────────────────────┘
                              │
                  ┌───────────▼───────────┐
                  │   Connection Pool     │
                  │  • HikariCP          │
                  │  • Connection Reuse  │
                  │  • Pool Management   │
                  └───────────────────────┘
```

This comprehensive system architecture overview provides the technical foundation for understanding how all components of the MemoApp work together to deliver a robust, scalable memo management system.
# Development and Deployment Guide

## Development Environment Setup

### Prerequisites

Ensure you have completed the basic setup from the [Setup Guide](./setup-guide.md) before proceeding with development-specific configurations.

### IDE Configuration

#### IntelliJ IDEA (Backend Development)

1. **Import Project**:
   - Open IntelliJ IDEA
   - File → Open → Select `MemoApp_Backend` directory
   - Choose "Import Gradle Project"

2. **Configure JDK**:
   - File → Project Structure → Project Settings → Project
   - Set Project SDK to Java 21
   - Set Project language level to 21

3. **Enable Annotation Processing**:
   - File → Settings → Build → Compiler → Annotation Processors
   - Check "Enable annotation processing"

4. **Install Plugins**:
   - Spring Boot plugin (usually pre-installed)
   - Lombok plugin (if needed for future development)

5. **Code Style**:
   - File → Settings → Editor → Code Style → Java
   - Import code style or configure according to team standards

#### Visual Studio Code (Frontend Development)

1. **Install Extensions**:
   ```json
   {
     "recommendations": [
       "angular.ng-template",
       "ms-vscode.vscode-typescript-next",
       "esbenp.prettier-vscode",
       "bradlc.vscode-tailwindcss",
       "ms-vscode.vscode-json"
     ]
   }
   ```

2. **Configure Settings** (`.vscode/settings.json`):
   ```json
   {
     "typescript.preferences.importModuleSpecifier": "relative",
     "editor.formatOnSave": true,
     "editor.defaultFormatter": "esbenp.prettier-vscode",
     "angular.experimental-ivy": true
   }
   ```

## Development Workflow

### Backend Development

#### Project Structure

```
MemoApp_Backend/
├── app/
│   ├── src/main/java/memoapp/
│   │   ├── App.java                    # Main application class
│   │   ├── controller/                 # REST controllers
│   │   │   └── MemoController.java
│   │   ├── service/                    # Business logic
│   │   │   └── MemoService.java
│   │   ├── repository/                 # Data access
│   │   │   └── MemoRepository.java
│   │   ├── entity/                     # JPA entities
│   │   │   └── Memo.java
│   │   ├── exception/                  # Custom exceptions
│   │   │   ├── MemoNotFoundException.java
│   │   │   ├── MemoValidationException.java
│   │   │   └── GlobalExceptionHandler.java
│   │   └── config/                     # Configuration
│   │       └── CorsConfig.java
│   └── src/test/java/memoapp/         # Test classes
├── build.gradle                        # Build configuration
└── docker-compose.yml                  # Container orchestration
```

#### Coding Standards

1. **SOLID Principles**:
   - Single Responsibility: Each class has one clear purpose
   - Open/Closed: Components open for extension, closed for modification
   - Liskov Substitution: Proper inheritance and interface implementation
   - Interface Segregation: Focused interfaces for specific operations
   - Dependency Inversion: Depend on abstractions, not concretions

2. **Code Style**:
   ```java
   // Use meaningful variable names
   List<Memo> allMemos = memoService.getAllMemos();
   
   // Proper exception handling
   public Memo getMemoById(Long id) {
       validateMemoId(id);
       return memoRepository.findById(id)
               .orElseThrow(() -> new MemoNotFoundException(id));
   }
   
   // Comprehensive javadoc
   /**
    * Creates a new memo in the system.
    * 
    * @param memo the memo to create
    * @return the created memo with generated ID and timestamps
    * @throws MemoValidationException if the memo is invalid
    */
   ```

3. **Testing Standards**:
   - Unit tests for all service methods
   - Integration tests for controllers
   - Repository tests with Testcontainers
   - Minimum 80% test coverage

#### Development Commands

```bash
# Build the project
./gradlew build

# Run tests
./gradlew test

# Run with continuous build
./gradlew build --continuous

# Run application
./gradlew bootRun

# Generate test coverage report
./gradlew test jacocoTestReport

# Check code quality
./gradlew check

# Clean build artifacts
./gradlew clean
```

#### Adding New Features

1. **Create Entity** (if needed):
   ```java
   @Entity
   @Table(name = "new_entity")
   public class NewEntity {
       // Entity definition
   }
   ```

2. **Create Repository**:
   ```java
   @Repository
   public interface NewEntityRepository extends JpaRepository<NewEntity, Long> {
       // Custom query methods
   }
   ```

3. **Create Service**:
   ```java
   @Service
   @Transactional(readOnly = true)
   public class NewEntityService {
       // Business logic
   }
   ```

4. **Create Controller**:
   ```java
   @RestController
   @RequestMapping("/api/new-entities")
   public class NewEntityController {
       // REST endpoints
   }
   ```

5. **Write Tests**:
   ```java
   @SpringBootTest
   class NewEntityServiceTest {
       // Unit tests
   }
   ```

### Frontend Development

#### Project Structure

```
MemoApp_Frontend/
├── src/
│   ├── app/
│   │   ├── app.ts                      # Main app component
│   │   ├── app.config.ts               # App configuration
│   │   ├── app.routes.ts               # Routing configuration
│   │   ├── components/                 # Angular components
│   │   │   ├── memo-list/
│   │   │   └── memo-form/
│   │   ├── services/                   # HTTP services
│   │   │   └── memo.service.ts
│   │   ├── models/                     # TypeScript interfaces
│   │   │   └── memo.model.ts
│   │   └── environments/               # Environment configs
│   ├── assets/                         # Static assets
│   ├── main.ts                         # Application bootstrap
│   └── styles.css                      # Global styles
├── package.json                        # Dependencies
└── angular.json                        # Angular configuration
```

#### Coding Standards

1. **Angular Style Guide**: Follow official Angular style guide
2. **TypeScript Best Practices**:
   ```typescript
   // Use interfaces for type safety
   interface MemoRequest {
     title: string;
     content: string;
   }
   
   // Use proper error handling
   createMemo(memo: MemoRequest): Observable<Memo> {
     return this.http.post<Memo>(this.apiUrl, memo)
       .pipe(
         catchError(this.handleError)
       );
   }
   ```

3. **Component Architecture**:
   - Smart components for data management
   - Dumb components for presentation
   - Use OnPush change detection where possible

#### Development Commands

```bash
# Install dependencies
npm install

# Start development server
npm start

# Build for production
npm run build

# Run tests
npm test

# Run linting
ng lint

# Generate new component
ng generate component feature-name

# Generate new service
ng generate service service-name
```

## Testing Strategy

### Backend Testing

#### Unit Tests

```java
@ExtendWith(MockitoExtension.class)
class MemoServiceTest {
    
    @Mock
    private MemoRepository memoRepository;
    
    @InjectMocks
    private MemoService memoService;
    
    @Test
    void shouldCreateMemo() {
        // Given
        Memo memo = new Memo("Test", "Content");
        when(memoRepository.save(any(Memo.class))).thenReturn(memo);
        
        // When
        Memo result = memoService.createMemo(memo);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("Test");
    }
}
```

#### Integration Tests

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class MemoControllerIntegrationTest {
    
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");
    
    @Test
    void shouldCreateAndRetrieveMemo() {
        // Integration test implementation
    }
}
```

#### Test Configuration

`src/test/resources/application-test.yml`:
```yaml
spring:
  datasource:
    url: jdbc:h2:mem:testdb
    driver-class-name: org.h2.Driver
    username: sa
    password: password
  jpa:
    hibernate:
      ddl-auto: create-drop
    show-sql: true
```

### Frontend Testing

#### Unit Tests

```typescript
describe('MemoService', () => {
  let service: MemoService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [MemoService]
    });
    service = TestBed.inject(MemoService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  it('should create memo', () => {
    const mockMemo = { title: 'Test', content: 'Content' };
    
    service.createMemo(mockMemo).subscribe(memo => {
      expect(memo.title).toBe('Test');
    });

    const req = httpMock.expectOne('/api/memos');
    expect(req.request.method).toBe('POST');
    req.flush(mockMemo);
  });
});
```

## Build and Deployment

### Development Builds

#### Backend
```bash
# Development build
./gradlew build

# Skip tests for faster build
./gradlew build -x test
```

#### Frontend
```bash
# Development build
ng build

# Watch mode
ng build --watch
```

### Production Builds

#### Backend
```bash
# Production JAR
./gradlew bootJar

# The JAR will be created in build/libs/app-0.0.1-SNAPSHOT.jar
```

#### Frontend
```bash
# Production build
ng build --configuration production

# Build output in dist/ directory
```

### Docker Deployment

#### Backend Docker Image

```dockerfile
FROM openjdk:21-jre-slim

WORKDIR /app

COPY build/libs/app-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 1919

CMD ["java", "-jar", "app.jar"]
```

Build and run:
```bash
# Build image
docker build -t memo-app-backend .

# Run container
docker run -p 1919:1919 memo-app-backend
```

#### Full Stack Deployment

```bash
# Start all services
docker-compose up --build

# Start in production mode
docker-compose -f docker-compose.yml -f docker-compose.prod.yml up --build
```

### Production Deployment

#### Environment Configuration

Create `application-prod.yml`:
```yaml
server:
  port: 8080

spring:
  datasource:
    url: ${DATABASE_URL}
    username: ${DATABASE_USERNAME}
    password: ${DATABASE_PASSWORD}
  
  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: false
  
logging:
  level:
    root: WARN
    memoapp: INFO
```

#### Environment Variables

```bash
export DATABASE_URL=jdbc:postgresql://prod-db:5432/memoapp
export DATABASE_USERNAME=memoapp_user
export DATABASE_PASSWORD=secure_password
export SPRING_PROFILES_ACTIVE=prod
```

#### Production Checklist

- [ ] Use `ddl-auto: validate` in production
- [ ] Configure proper logging levels
- [ ] Set up SSL/TLS for database connections
- [ ] Enable Spring Security if authentication is needed
- [ ] Configure CORS for production domains
- [ ] Set up monitoring and health checks
- [ ] Configure backup strategy
- [ ] Set up CI/CD pipeline

## Monitoring and Maintenance

### Application Monitoring

#### Spring Boot Actuator

Add to `build.gradle`:
```gradle
implementation 'org.springframework.boot:spring-boot-starter-actuator'
```

Configuration:
```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
  endpoint:
    health:
      show-details: when-authorized
```

Available endpoints:
- `/actuator/health` - Application health
- `/actuator/info` - Application info
- `/actuator/metrics` - Application metrics

#### Logging

Configure structured logging:
```yaml
logging:
  level:
    memoapp: INFO
    org.springframework.web: INFO
  pattern:
    file: "%d{ISO8601} [%thread] %-5level %logger{36} - %msg%n"
  file:
    name: logs/memoapp.log
```

### Performance Monitoring

#### Database Performance

```java
@Configuration
public class DatabaseConfig {
    
    @Bean
    public DataSource dataSource() {
        HikariConfig config = new HikariConfig();
        config.setMaximumPoolSize(20);
        config.setConnectionTimeout(300000);
        config.setLeakDetectionThreshold(300000);
        return new HikariDataSource(config);
    }
}
```

#### Application Metrics

```java
@Component
public class MemoMetrics {
    
    private final Counter memoCreatedCounter;
    
    public MemoMetrics(MeterRegistry meterRegistry) {
        this.memoCreatedCounter = Counter.builder("memo.created")
                .description("Number of memos created")
                .register(meterRegistry);
    }
    
    public void incrementMemoCreated() {
        memoCreatedCounter.increment();
    }
}
```

## Troubleshooting

### Common Development Issues

#### Backend Issues

1. **Port Already in Use**:
   ```bash
   lsof -ti:1919 | xargs kill -9
   ```

2. **Database Connection Issues**:
   ```bash
   # Check PostgreSQL status
   pg_isready -h localhost -p 5432
   
   # Restart Docker containers
   docker-compose down && docker-compose up
   ```

3. **Build Failures**:
   ```bash
   # Clean and rebuild
   ./gradlew clean build
   
   # Check for dependency conflicts
   ./gradlew dependencies
   ```

#### Frontend Issues

1. **Node Modules Issues**:
   ```bash
   rm -rf node_modules package-lock.json
   npm install
   ```

2. **Angular CLI Issues**:
   ```bash
   ng update @angular/cli @angular/core
   ```

3. **Build Errors**:
   ```bash
   ng lint --fix
   ng build --verbose
   ```

### Performance Issues

1. **Slow Database Queries**: Enable SQL logging and analyze query performance
2. **Memory Issues**: Configure JVM memory settings
3. **High CPU Usage**: Profile the application and identify bottlenecks

## Code Quality and Best Practices

### Static Code Analysis

#### Backend (SonarQube)

Add to `build.gradle`:
```gradle
plugins {
    id "org.sonarqube" version "4.0.0.2929"
}

sonarqube {
    properties {
        property "sonar.projectName", "MemoApp Backend"
        property "sonar.coverage.jacoco.xmlReportPaths", "build/reports/jacoco/test/jacocoTestReport.xml"
    }
}
```

#### Frontend (ESLint + Prettier)

`.eslintrc.json`:
```json
{
  "extends": [
    "@angular-eslint/recommended",
    "@angular-eslint/template/process-inline-templates"
  ],
  "rules": {
    "@typescript-eslint/no-unused-vars": "error",
    "@angular-eslint/component-selector": "error"
  }
}
```

### Security Best Practices

1. **Input Validation**: Always validate user input
2. **SQL Injection Prevention**: Use parameterized queries
3. **CORS Configuration**: Configure for specific domains in production
4. **Error Handling**: Don't expose sensitive information in error messages
5. **Dependencies**: Regularly update dependencies for security patches

### Documentation Standards

1. **Code Documentation**: Use JavaDoc for Java, TSDoc for TypeScript
2. **API Documentation**: Keep API docs synchronized with code
3. **README Files**: Maintain comprehensive README files
4. **Change Logs**: Document significant changes and migrations

This development guide provides the foundation for maintaining and extending the MemoApp system while following best practices for enterprise-grade applications.
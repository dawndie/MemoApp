# Setup and Installation Guide

## Prerequisites

Before setting up the MemoApp, ensure you have the following installed:

### Required Software

#### For Docker Setup (Recommended)
- **Docker**: Version 20.10 or higher
- **Docker Compose**: Version 2.0 or higher

#### For Manual Setup
- **Java**: Version 21 (OpenJDK or Oracle JDK)
- **Node.js**: Version 18 or higher
- **npm**: Version 9 or higher (comes with Node.js)
- **PostgreSQL**: Version 15 or higher
- **Git**: For cloning the repository

### System Requirements

- **RAM**: Minimum 4GB, Recommended 8GB
- **Storage**: At least 2GB free space
- **OS**: Windows 10/11, macOS 10.15+, or Linux (Ubuntu 20.04+)

## Installation Methods

## Method 1: Docker Setup (Recommended)

This is the easiest way to get the application running with all dependencies.

### Step 1: Clone the Repository

```bash
git clone <repository-url>
cd MemoApp
```

### Step 2: Start with Docker Compose

Navigate to the backend directory and start all services:

```bash
cd MemoApp_Backend
docker-compose up --build
```

This command will:
- Build the Spring Boot application
- Start PostgreSQL database
- Set up networking between containers
- Expose the application on port 1919

### Step 3: Start Frontend (Optional)

The Docker setup only starts the backend. For the frontend:

```bash
cd ../MemoApp_Frontend
npm install
npm start
```

The frontend will be available at: http://localhost:6565

### Verification

- Backend API: http://localhost:1919/api/memos
- Frontend: http://localhost:6565
- Database: localhost:5432 (user: memoapp, password: password, database: memoapp)

---

## Method 2: Manual Setup

For development or when you need more control over the environment.

### Step 1: Clone and Setup Repository

```bash
git clone <repository-url>
cd MemoApp
```

### Step 2: Setup PostgreSQL Database

#### Option A: Local PostgreSQL Installation

1. Install PostgreSQL 15
2. Create database and user:

```sql
CREATE USER memoapp WITH PASSWORD 'password';
CREATE DATABASE memoapp OWNER memoapp;
GRANT ALL PRIVILEGES ON DATABASE memoapp TO memoapp;
```

#### Option B: Docker PostgreSQL Only

```bash
docker run --name postgres-memo \
  -e POSTGRES_DB=memoapp \
  -e POSTGRES_USER=memoapp \
  -e POSTGRES_PASSWORD=password \
  -p 5432:5432 \
  -d postgres:15
```

### Step 3: Setup Backend

```bash
cd MemoApp_Backend

# Build the application
./gradlew build

# Run the application
./gradlew bootRun
```

The backend will start on port 1919.

### Step 4: Setup Frontend

```bash
cd ../MemoApp_Frontend

# Install dependencies
npm install

# Start development server
npm start
```

The frontend will start on port 6565.

### Verification

Visit http://localhost:6565 to access the application.

---

## Method 3: Development Setup

For developers who want to contribute or modify the application.

### Step 1: IDE Setup

#### IntelliJ IDEA (Recommended for Backend)
1. Import the Gradle project
2. Enable annotation processing
3. Set JDK to version 21
4. Install Spring Boot plugin

#### Visual Studio Code (Recommended for Frontend)
1. Install Angular Language Service extension
2. Install TypeScript extension
3. Configure auto-formatting with Prettier

### Step 2: Database Setup for Development

Use H2 in-memory database for testing:

```bash
# Run tests with H2
cd MemoApp_Backend
./gradlew test
```

### Step 3: Environment Configuration

#### Backend Configuration

Create `application-dev.yml` for development:

```yaml
server:
  port: 1919

spring:
  application:
    name: memo-app
  
  datasource:
    url: jdbc:postgresql://localhost:5432/memoapp
    username: memoapp
    password: password
    driver-class-name: org.postgresql.Driver
  
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: false  # Set to false for cleaner logs in dev
  
logging:
  level:
    org.springframework.web: INFO
    org.hibernate.SQL: INFO
```

#### Frontend Configuration

Update `src/environments/environment.ts`:

```typescript
export const environment = {
  production: false,
  apiUrl: 'http://localhost:1919/api'
};
```

### Step 4: Running in Development Mode

#### Backend with Hot Reload
```bash
cd MemoApp_Backend
./gradlew bootRun --continuous
```

#### Frontend with Hot Reload
```bash
cd MemoApp_Frontend
npm start
```

---

## Configuration Details

### Backend Configuration

The backend uses Spring Boot's configuration system. Key files:

- `application.yml`: Main configuration
- `application-test.yml`: Test configuration
- `application-prod.yml`: Production configuration (create if needed)

#### Database Configuration

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/memoapp
    username: memoapp
    password: password
    driver-class-name: org.postgresql.Driver
  
  jpa:
    hibernate:
      ddl-auto: update  # Use 'validate' in production
    show-sql: true      # Set to false in production
```

#### Server Configuration

```yaml
server:
  port: 1919
  servlet:
    context-path: /  # Default context path
```

### Frontend Configuration

#### Environment Files

- `src/environments/environment.ts`: Development
- `src/environments/environment.prod.ts`: Production

#### Angular Configuration

Key settings in `angular.json`:
- Default port: 6565
- Build output directory: `dist/`
- Assets directory: `src/assets/`

### CORS Configuration

The backend is configured to allow requests from:
- `http://localhost:6565` (Angular dev server)
- `http://localhost:3000` (Alternative port)

Located in: `MemoApp_Backend/app/src/main/java/memoapp/config/CorsConfig.java`

---

## Troubleshooting

### Common Issues

#### Port Already in Use

**Error**: Port 1919 or 6565 already in use

**Solution**:
```bash
# Find process using the port
lsof -ti:1919
# Kill the process
kill -9 <PID>

# Or change port in configuration
```

#### Database Connection Issues

**Error**: Cannot connect to PostgreSQL

**Solutions**:
1. Ensure PostgreSQL is running:
   ```bash
   # Check if PostgreSQL is running
   pg_isready -h localhost -p 5432
   ```

2. Verify database credentials in `application.yml`

3. Check if database exists:
   ```sql
   \l  -- List all databases
   ```

#### Java Version Issues

**Error**: Unsupported Java version

**Solution**:
```bash
# Check Java version
java -version
javac -version

# Set JAVA_HOME if needed
export JAVA_HOME=/path/to/java21
```

#### Node.js/npm Issues

**Error**: npm install fails

**Solution**:
```bash
# Clear npm cache
npm cache clean --force

# Delete node_modules and reinstall
rm -rf node_modules package-lock.json
npm install
```

### Docker Issues

#### Docker Build Failures

**Error**: Docker build fails

**Solutions**:
1. Ensure Docker is running
2. Clear Docker cache:
   ```bash
   docker system prune -a
   ```
3. Check Dockerfile syntax

#### Container Connectivity Issues

**Error**: Backend cannot connect to PostgreSQL container

**Solution**:
1. Check container logs:
   ```bash
   docker-compose logs app
   docker-compose logs postgres
   ```

2. Verify network configuration in `docker-compose.yml`

### Performance Issues

#### Slow Application Startup

**Causes and Solutions**:
1. **Database initialization**: Set `ddl-auto: validate` in production
2. **Large dependencies**: Consider using Docker images with dependencies pre-installed
3. **Resource constraints**: Increase Docker memory allocation

#### Frontend Build Issues

**Error**: Angular build fails

**Solution**:
```bash
# Clear Angular cache
rm -rf .angular/cache

# Clear node_modules
rm -rf node_modules package-lock.json
npm install

# Try building again
npm run build
```

---

## Environment Variables

### Backend Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `SPRING_PROFILES_ACTIVE` | Active Spring profile | `default` |
| `SPRING_DATASOURCE_URL` | Database connection URL | `jdbc:postgresql://localhost:5432/memoapp` |
| `SPRING_DATASOURCE_USERNAME` | Database username | `memoapp` |
| `SPRING_DATASOURCE_PASSWORD` | Database password | `password` |
| `SERVER_PORT` | Server port | `1919` |

### Frontend Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `NG_APP_API_URL` | Backend API URL | `http://localhost:1919/api` |

---

## Next Steps

After successful installation:

1. **Explore the API**: Visit http://localhost:1919/api/memos
2. **Use the Frontend**: Visit http://localhost:6565
3. **Read the Development Guide**: [development-guide.md](./development-guide.md)
4. **Check Database Schema**: [database-schema.md](./database-schema.md)

## Support

If you encounter issues not covered in this guide:

1. Check the application logs
2. Review the troubleshooting section
3. Consult the project documentation
4. Check for known issues in the repository
# Build stage
FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /back-end

# Copy gradle wrapper and build files
COPY gradlew .
COPY gradle gradle
COPY app/build.gradle app/
COPY settings.gradle .

# Make gradlew executable
RUN chmod +x ./gradlew

# Download dependencies (cached layer if build.gradle doesn't change)
RUN ./gradlew dependencies --no-daemon || true

# Copy source code
COPY app/src app/src

# Build the application
RUN ./gradlew build -x test --no-daemon

# Runtime stage
FROM eclipse-temurin:21-jre-alpine

WORKDIR /back-end

# Copy only the built jar from builder stage
COPY --from=builder /back-end/app/build/libs/app-0.0.1-SNAPSHOT.jar app.jar

# Expose port
EXPOSE 8081

# Run the application
CMD ["java", "-jar", "app.jar"]
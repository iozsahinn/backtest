# Stage 1: Build
FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /app

# Copy Maven wrapper and pom.xml
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Download dependencies
RUN chmod +x mvnw
RUN ./mvnw dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the application
RUN ./mvnw clean package -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Step 1: Create non-root user and group (as root)
# The user and group MUST exist before you can chown to them.
RUN addgroup -S appuser && adduser -S appuser -G appuser

# Step 2: Create logs directory and set ownership (still as root)
# Now 'appuser' exists, so chown will work.
RUN mkdir -p logs && chown -R appuser:appuser /app

# Step 3: Switch to the non-root user for all subsequent commands and runtime
USER appuser

# Copy the built jar from builder stage
COPY --from=builder /app/target/*.jar app.jar

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=30s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
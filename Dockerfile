# ============================================================
# AutoParts Portal - Dockerfile
# Multi-stage build: Build with Maven, run with JRE
# ============================================================

# Stage 1: Build
FROM maven:3.9.6-eclipse-temurin-17 AS builder

WORKDIR /build

# Copy pom.xml first to leverage Docker layer caching
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build JAR (skip Selenium tests in Docker build)
RUN mvn clean package -DskipTests -B

# Stage 2: Runtime
FROM eclipse-temurin:17-jre-jammy

LABEL maintainer="AutoParts DevOps <devops@autoparts.local>"
LABEL version="1.0.0"
LABEL description="Automobile Spare Parts Portal"

WORKDIR /app

# Create non-root user for security
RUN groupadd --system autoparts && \
    useradd --system --gid autoparts --shell /bin/false autoparts

# Copy JAR from builder stage
COPY --from=builder /build/target/autoparts-portal-1.0.0.jar app.jar

# Set ownership
RUN chown autoparts:autoparts app.jar

USER autoparts

# Expose application port
EXPOSE 8080

# Environment variables with defaults
ENV JAVA_OPTS="-Xms256m -Xmx512m -XX:+UseG1GC"
ENV DB_URL="jdbc:postgresql://postgres:5432/autoparts"
ENV DB_USERNAME="postgres"
ENV DB_PASSWORD="postgres"
ENV DDL_AUTO="update"

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

# Start application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]

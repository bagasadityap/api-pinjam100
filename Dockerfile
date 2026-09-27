# =========================
# Build stage
# =========================
FROM eclipse-temurin:21-jdk AS builder

WORKDIR /app

# Copy Maven wrapper and project configuration
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./

# Make Maven wrapper executable
RUN chmod +x mvnw

# Cache Maven dependencies
RUN ./mvnw dependency:go-offline

# Copy source code
COPY src/ src/

# Build Spring Boot application
RUN ./mvnw clean package -DskipTests


# =========================
# Runtime stage
# =========================
FROM eclipse-temurin:21-jre

WORKDIR /app

# Copy the generated Spring Boot JAR
COPY --from=builder /app/target/*.jar app.jar

# Spring Boot default port
EXPOSE 8080

# Start application
ENTRYPOINT ["java", "-jar", "app.jar"]
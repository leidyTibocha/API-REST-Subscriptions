# Multi-stage Dockerfile for building and running the Spring Boot application

# Build stage
FROM maven:3.10.1-eclipse-temurin-21 AS build
WORKDIR /workspace

# Copy pom and sources and build the jar
COPY pom.xml mvnw mvnw.cmd ./
COPY .mvn .mvn
COPY src ./src

# Use maven to package the application (skip tests to speed up builds; change as needed)
RUN mvn -B -DskipTests package

# Runtime stage
FROM eclipse-temurin:21-jre
WORKDIR /app

# Copy the packaged jar from the build stage (handles whatever the jar filename is)
COPY --from=build /workspace/target/*.jar app.jar

# Expose default Spring Boot port
EXPOSE 8080

# Run the jar
ENTRYPOINT ["java", "-jar", "/app/app.jar"]

# Build stage: use Maven to build the Spring Boot jar
FROM maven:3.9.4-eclipse-temurin-21 AS builder
WORKDIR /workspace

# Copy pom first to leverage Docker cache for deps
COPY pom.xml .
# Use the container's Maven binary instead of the project mvnw wrapper
RUN mvn -B -ntp -DskipTests dependency:go-offline

# Copy sources and build
COPY src ./src
RUN mvn -B -DskipTests package

# Runtime image
FROM eclipse-temurin:21-jre
WORKDIR /app

# Copy jar built in previous stage
COPY --from=builder /workspace/target/*.jar ./app.jar

EXPOSE 8185
ENTRYPOINT ["java", "-jar", "/app/app.jar"]

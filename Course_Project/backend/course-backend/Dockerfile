


# STEP 1: Build the app

FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Copy only necessary files
COPY pom.xml .
COPY src ./src

# Build Spring Boot JAR
RUN mvn clean package -DskipTests

# STEP 2: Run the app

FROM eclipse-temurin:17-jdk
WORKDIR /app

# Copy JAR from build stage
COPY --from=build /app/target/*.jar app.jar

# Expose the backend port
EXPOSE 8080

# Run Spring Boot
ENTRYPOINT ["java", "-jar", "app.jar"]

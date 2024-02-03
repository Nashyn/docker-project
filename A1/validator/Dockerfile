# Build stage
FROM maven:3.8.4-openjdk-17-slim AS build
WORKDIR /app
COPY pom.xml ./
COPY src ./src
RUN mvn package -DskipTests

# Package stage
FROM openjdk:17-slim
WORKDIR /app
COPY --from=build /app/target/validator-1.0-SNAPSHOT.jar /app/validator.jar
EXPOSE 6001
CMD ["java", "-jar", "validator.jar"]

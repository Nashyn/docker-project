# Build stage
FROM maven:3.8.4-openjdk-17-slim AS build
WORKDIR /app
COPY pom.xml ./
COPY src ./src
RUN mvn package -DskipTests

# Package stage
FROM openjdk:17-slim
WORKDIR /app
COPY --from=build /app/target/calculator-1.0-SNAPSHOT.jar /app/calculator.jar
EXPOSE 6000
CMD ["java", "-jar", "calculator.jar"]
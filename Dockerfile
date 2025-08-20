# Use a Maven image for the build stage
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY . .
RUN mvn -q -DskipTests clean package

# Use a lightweight JDK image for the runtime stage
FROM eclipse-temurin:21-jdk
ENV SERVICE_NAME=device-service
WORKDIR /app

# Copy the built JAR to app folder
COPY --from=build /app/target/${SERVICE_NAME}*.jar /app/${SERVICE_NAME}.jar

EXPOSE 8080

ENTRYPOINT exec java -jar /app/${SERVICE_NAME}.jar
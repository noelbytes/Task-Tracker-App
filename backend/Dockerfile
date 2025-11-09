# Backend Dockerfile
FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app

# Copy pom.xml and download dependencies
COPY pom.xml .

# Copy source code and build
COPY src ./src
RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copy jar from build stage
COPY --from=build /app/target/*.jar app.jar

# Runtime JVM tuning for low-memory, low-CPU environment (Render Free tier 512MB/0.1CPU)
# -Xms minimal heap, -Xmx upper bound, Use G1 GC (good default for Java 17),
# ExitOnOutOfMemoryError so container crashes quickly and restarts rather than thrashing.
ENV JAVA_OPTS="-Xms128m -Xmx384m -XX:MaxRAMPercentage=80 -XX:+UseG1GC -XX:+ExitOnOutOfMemoryError -Djava.security.egd=file:/dev/./urandom"

# Expose port
EXPOSE 8080

# Run the application with JAVA_OPTS
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar app.jar"]

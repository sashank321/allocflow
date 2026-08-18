# Build Stage
FROM maven:3.9.6-eclipse-temurin-21-alpine AS build
WORKDIR /app
COPY pom.xml .
COPY dsa-engine/pom.xml dsa-engine/
COPY api/pom.xml api/
COPY dsa-engine/src dsa-engine/src
COPY api/src api/src

RUN mvn clean install -DskipTests

# Runtime Stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
COPY --from=build /app/api/target/api-1.0.0-SNAPSHOT.jar app.jar
USER appuser
EXPOSE 8080
ENV PORT=8080 \
    SPRING_PROFILES_ACTIVE=prod
ENTRYPOINT ["java", "-jar", "app.jar"]

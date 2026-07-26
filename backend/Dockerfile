# Build stage
FROM eclipse-temurin:21-jdk-jammy AS build
WORKDIR /workspace

COPY mvnw pom.xml ./
COPY .mvn ./.mvn
RUN chmod +x mvnw && ./mvnw -B -DskipTests dependency:go-offline

COPY src ./src
RUN ./mvnw -B -DskipTests package && cp target/backend-*.jar app.jar

# Run stage
FROM eclipse-temurin:21-jre-jammy
RUN useradd -r -u 1001 -g root spring
USER spring
WORKDIR /app

COPY --from=build /workspace/app.jar /app/app.jar

EXPOSE 8080

HEALTHCHECK --interval=15s --timeout=3s --retries=5 \
  CMD wget -qO- http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["java", "-jar", "/app/app.jar"]

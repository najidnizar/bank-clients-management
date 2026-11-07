# syntax=docker/dockerfile:1

# ---- Build stage: compile and package the Spring Boot jar ----
# Runs on the builder's native platform ($BUILDPLATFORM): the jar is platform-independent, so a multi-platform
# build (amd64 + arm64) compiles it only once, without slow CPU emulation. Only the runtime stage is per platform.
FROM --platform=$BUILDPLATFORM eclipse-temurin:25-jdk AS build
WORKDIR /workspace

COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN chmod +x mvnw

COPY src/ src/
# The Maven repository is cached between builds to avoid re-downloading dependencies
RUN --mount=type=cache,target=/root/.m2 \
    ./mvnw -B -q package -DskipTests \
    && cp target/*.jar app.jar

# ---- Runtime stage: JRE only, non-root user ----
FROM eclipse-temurin:25-jre
WORKDIR /app

RUN groupadd --system spring && useradd --system --gid spring spring
COPY --from=build /workspace/app.jar app.jar
USER spring

EXPOSE 8080
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75", "-jar", "app.jar"]

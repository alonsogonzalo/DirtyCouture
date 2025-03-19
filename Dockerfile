# Construction
FROM gradle:8.10-jdk23 AS build
WORKDIR /app

# Copy main files for compilation
COPY build.gradle.kts settings.gradle.kts gradle.properties ./
COPY gradle gradle
COPY src src

# Compile project
RUN gradle clean installDist

# Optimized final image
FROM eclipse-temurin:23-jre
WORKDIR /app

# Copy main files from compilation stage
COPY --from=build /app/build/install/DirtyCouture /app/

# Define start command
CMD ["/app/bin/DirtyCouture"]

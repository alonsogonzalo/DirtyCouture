# Etapa de compilación
FROM gradle:8.5-jdk17 AS builder

WORKDIR /app
COPY . .

RUN gradle clean build

# Etapa de ejecución
FROM eclipse-temurin:17-jdk
WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
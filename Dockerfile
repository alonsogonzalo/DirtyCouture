# Etapa de compilación
FROM gradle:8.5-jdk17 AS builder

WORKDIR /app
COPY . .

RUN ./gradlew clean shadowJar

# Etapa de ejecución
FROM eclipse-temurin:17-jdk
WORKDIR /app

COPY --from=builder /app/build/libs/DirtyCouture-all.jar app.jar

ENV PORT=8080
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
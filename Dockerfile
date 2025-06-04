# Build del frontend
FROM node:20-alpine AS frontend-builder
WORKDIR /app

#Copia los package.json del frontend
COPY src/main/kotlin/com/dirtycouture/frontend/package*.json ./

#Instala dependencias npm
RUN npm install

#Copia el resto del frontend
COPY src/main/kotlin/com/dirtycouture/frontend .

#Build del frontend
RUN npm run build

# Build del backend (Ktor)
FROM gradle:8.5-jdk17 AS backend-builder
WORKDIR /app
COPY . .

# Copia el frontend compilado a resources/static del backend
COPY --from=frontend-builder /app/dist src/main/resources/frontend/dist

# Compila backend
RUN ./gradlew clean shadowJar

# Etapa de ejecución
FROM eclipse-temurin:17-jdk
WORKDIR /app

COPY --from=backend-builder /app/build/libs/DirtyCouture-all.jar app.jar

ENV PORT=8080
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
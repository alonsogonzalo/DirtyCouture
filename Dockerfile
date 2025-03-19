# Etapa de construcción
FROM gradle:8.10-jdk23 AS build
WORKDIR /app

# Copiar archivos necesarios para la compilación
COPY build.gradle.kts settings.gradle.kts gradle.properties ./
COPY gradle gradle
COPY src src

# Construir el proyecto
RUN gradle clean installDist

# Etapa final optimizada
FROM eclipse-temurin:23-jre
WORKDIR /app

# Copiar archivos compilados
COPY --from=build /app/build/install/DirtyCouture /app/

# Asegurar permisos de ejecución
RUN chmod +x /app/bin/DirtyCouture

# Comando para iniciar la aplicación
CMD ["/app/bin/DirtyCouture"]

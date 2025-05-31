#Imagen base con JDK 17
FROM eclipse-temurin:17-jdk

#Directorio de trabajo dentro del contenedor
WORKDIR /app

#Copia el .jar generado por GitHub Actions al contenedor
COPY build/libs/*.jar app.jar

#Comando de arranque
ENTRYPOINT ["java", "-jar", "app.jar"]

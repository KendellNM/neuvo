# Dockerfile para Spring Boot con Tesseract OCR
FROM eclipse-temurin:17-jdk-jammy

# Instalar Tesseract OCR con idioma español
RUN apt-get update && apt-get install -y \
    tesseract-ocr \
    tesseract-ocr-spa \
    tesseract-ocr-eng \
    && rm -rf /var/lib/apt/lists/*

# Verificar instalación
RUN tesseract --version && tesseract --list-langs

# Directorio de trabajo
WORKDIR /app

# Copiar archivos del proyecto
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .
COPY src src

# Dar permisos al gradlew
RUN chmod +x ./gradlew

# Compilar el proyecto
RUN ./gradlew bootJar --no-daemon

# Exponer puerto
EXPOSE 8090

# Ejecutar la aplicación
CMD ["java", "-jar", "build/libs/farmacia-0.0.1-SNAPSHOT.jar"]
package com.farm.dolores.farmacia.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@Order(2) // Ejecutar después del DataSeeder
public class InitializationService implements ApplicationRunner {

    @Value("${app.upload.dir:src/main/resources/static/uploads}")
    private String uploadDir;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        createUploadDirectories();
        printStartupInfo();
    }

    private void createUploadDirectories() {
        try {
            String[] directories = {"perfiles", "productos", "recetas"};
            
            for (String dir : directories) {
                Path dirPath = Paths.get(uploadDir, dir);
                if (!Files.exists(dirPath)) {
                    Files.createDirectories(dirPath);
                }
            }
            
            System.out.println("📁 Carpetas de imágenes listas");
            
        } catch (Exception e) {
            System.err.println("❌ Error creando directorios: " + e.getMessage());
        }
    }

    private void printStartupInfo() {
        System.out.println("");
        System.out.println("🚀 Backend iniciado correctamente!");
        System.out.println("📍 Simulador de delivery activo - Enviando ubicaciones cada 5 segundos");
        System.out.println("🔌 WebSocket: ws://localhost:8090/ws-delivery");
        System.out.println("📊 Suscríbete a: /topic/delivery/123");
        System.out.println("🌐 Swagger UI: http://localhost:8090/swagger-ui.html");
        System.out.println("");
    }
}
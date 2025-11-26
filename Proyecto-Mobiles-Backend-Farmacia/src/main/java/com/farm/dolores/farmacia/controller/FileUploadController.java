package com.farm.dolores.farmacia.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/upload")
@Tag(name = "📤 Archivos", description = "Subida y gestión de archivos (imágenes, documentos)")
@SecurityRequirement(name = "bearer-jwt")
public class FileUploadController {

    @Value("${app.upload.dir:src/main/resources/static/uploads}")
    private String uploadDir;

    @PostMapping("/perfil")
    public ResponseEntity<Map<String, String>> uploadProfileImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam("tipo") String tipo) {
        
        Map<String, String> response = new HashMap<>();
        
        try {
            // Validar archivo
            if (file.isEmpty()) {
                response.put("error", "Archivo vacío");
                return ResponseEntity.badRequest().body(response);
            }
            
            // Validar tipo de archivo
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                response.put("error", "Solo se permiten imágenes");
                return ResponseEntity.badRequest().body(response);
            }
            
            // Validar tamaño (máximo 5MB)
            if (file.getSize() > 5 * 1024 * 1024) {
                response.put("error", "Archivo muy grande (máximo 5MB)");
                return ResponseEntity.badRequest().body(response);
            }
            
            // Crear directorio si no existe
            String subDir = "perfiles";
            Path uploadPath = Paths.get(uploadDir, subDir);
            Files.createDirectories(uploadPath);
            
            // Generar nombre único
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename != null && originalFilename.contains(".") 
                ? originalFilename.substring(originalFilename.lastIndexOf("."))
                : ".jpg";
            
            String filename = tipo + "_" + UUID.randomUUID().toString() + extension;
            Path filePath = uploadPath.resolve(filename);
            
            // Guardar archivo
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            
            // URL de acceso
            String fileUrl = "/uploads/" + subDir + "/" + filename;
            
            response.put("success", "Archivo subido correctamente");
            response.put("filename", filename);
            response.put("url", fileUrl);
            response.put("size", String.valueOf(file.getSize()));
            
            return ResponseEntity.ok(response);
            
        } catch (IOException e) {
            response.put("error", "Error al guardar archivo: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @PostMapping("/producto")
    public ResponseEntity<Map<String, String>> uploadProductImage(
            @RequestParam("file") MultipartFile file) {
        
        Map<String, String> response = new HashMap<>();
        
        try {
            // Validaciones similares
            if (file.isEmpty()) {
                response.put("error", "Archivo vacío");
                return ResponseEntity.badRequest().body(response);
            }
            
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                response.put("error", "Solo se permiten imágenes");
                return ResponseEntity.badRequest().body(response);
            }
            
            if (file.getSize() > 5 * 1024 * 1024) {
                response.put("error", "Archivo muy grande (máximo 5MB)");
                return ResponseEntity.badRequest().body(response);
            }
            
            // Crear directorio
            String subDir = "productos";
            Path uploadPath = Paths.get(uploadDir, subDir);
            Files.createDirectories(uploadPath);
            
            // Generar nombre
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename != null && originalFilename.contains(".") 
                ? originalFilename.substring(originalFilename.lastIndexOf("."))
                : ".jpg";
            
            String filename = "producto_" + System.currentTimeMillis() + extension;
            Path filePath = uploadPath.resolve(filename);
            
            // Guardar
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            
            String fileUrl = "/uploads/" + subDir + "/" + filename;
            
            response.put("success", "Imagen de producto subida");
            response.put("filename", filename);
            response.put("url", fileUrl);
            
            return ResponseEntity.ok(response);
            
        } catch (IOException e) {
            response.put("error", "Error al guardar imagen: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @PostMapping("/receta")
    public ResponseEntity<Map<String, String>> uploadRecetaImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam("clienteId") Long clienteId) {
        
        Map<String, String> response = new HashMap<>();
        
        try {
            // Validaciones
            if (file.isEmpty()) {
                response.put("error", "Archivo vacío");
                return ResponseEntity.badRequest().body(response);
            }
            
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                response.put("error", "Solo se permiten imágenes");
                return ResponseEntity.badRequest().body(response);
            }
            
            if (file.getSize() > 10 * 1024 * 1024) { // 10MB para recetas
                response.put("error", "Archivo muy grande (máximo 10MB)");
                return ResponseEntity.badRequest().body(response);
            }
            
            // Crear directorio
            String subDir = "recetas";
            Path uploadPath = Paths.get(uploadDir, subDir);
            Files.createDirectories(uploadPath);
            
            // Generar nombre
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename != null && originalFilename.contains(".") 
                ? originalFilename.substring(originalFilename.lastIndexOf("."))
                : ".jpg";
            
            String filename = "receta_cliente" + clienteId + "_" + System.currentTimeMillis() + extension;
            Path filePath = uploadPath.resolve(filename);
            
            // Guardar
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            
            String fileUrl = "/uploads/" + subDir + "/" + filename;
            
            response.put("success", "Receta subida correctamente");
            response.put("filename", filename);
            response.put("url", fileUrl);
            response.put("clienteId", clienteId.toString());
            
            return ResponseEntity.ok(response);
            
        } catch (IOException e) {
            response.put("error", "Error al guardar receta: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @DeleteMapping("/archivo/{tipo}/{filename}")
    public ResponseEntity<Map<String, String>> deleteFile(
            @PathVariable String tipo,
            @PathVariable String filename) {
        
        Map<String, String> response = new HashMap<>();
        
        try {
            Path filePath = Paths.get(uploadDir, tipo, filename);
            
            if (Files.exists(filePath)) {
                Files.delete(filePath);
                response.put("success", "Archivo eliminado correctamente");
                return ResponseEntity.ok(response);
            } else {
                response.put("error", "Archivo no encontrado");
                return ResponseEntity.notFound().build();
            }
            
        } catch (IOException e) {
            response.put("error", "Error al eliminar archivo: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
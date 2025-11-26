package com.farm.dolores.farmacia.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Configuration
public class FirebaseConfig {

    private static final Logger logger = LoggerFactory.getLogger(FirebaseConfig.class);

    @PostConstruct
    public void initialize() {
        try {
            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseOptions options = null;

                // Opción 1: Desde variable de entorno (Producción)
                String firebaseConfigJson = System.getenv("FIREBASE_CONFIG");
                if (firebaseConfigJson != null && !firebaseConfigJson.isEmpty()) {
                    logger.info("Inicializando Firebase desde variable de entorno...");
                    InputStream stream = new ByteArrayInputStream(
                            firebaseConfigJson.getBytes(StandardCharsets.UTF_8)
                    );
                    options = FirebaseOptions.builder()
                            .setCredentials(GoogleCredentials.fromStream(stream))
                            .build();
                } else {
                    // Opción 2: Desde archivo (Desarrollo)
                    logger.info("Inicializando Firebase desde archivo...");
                    try {
                        FileInputStream serviceAccount = new FileInputStream("firebase-service-account.json");
                        options = FirebaseOptions.builder()
                                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                                .build();
                    } catch (IOException e) {
                        logger.warn("Archivo firebase-service-account.json no encontrado. " +
                                "Las notificaciones push no estarán disponibles.");
                        logger.warn("Para habilitar notificaciones push:");
                        logger.warn("1. Descarga el archivo desde Firebase Console");
                        logger.warn("2. Renómbralo a 'firebase-service-account.json'");
                        logger.warn("3. Colócalo en la raíz del proyecto");
                        return;
                    }
                }

                if (options != null) {
                    FirebaseApp.initializeApp(options);
                    logger.info("✅ Firebase inicializado correctamente");
                }
            } else {
                logger.info("Firebase ya está inicializado");
            }
        } catch (IOException e) {
            logger.error("❌ Error inicializando Firebase: {}", e.getMessage());
            logger.error("Las notificaciones push no estarán disponibles");
        } catch (Exception e) {
            logger.error("❌ Error inesperado inicializando Firebase: {}", e.getMessage(), e);
        }
    }
}

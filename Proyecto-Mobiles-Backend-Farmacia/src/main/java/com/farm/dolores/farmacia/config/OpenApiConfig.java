package com.farm.dolores.farmacia.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Value("${server.port:8090}")
    private String serverPort;

    @Bean
    public OpenAPI customOpenAPI() {
        String description = "API REST para gestión completa de farmacia con las siguientes funcionalidades:\n\n" +
                "✅ Autenticación JWT y gestión de usuarios\n" +
                "✅ Catálogo de productos con búsqueda avanzada\n" +
                "✅ Gestión de pedidos y delivery\n" +
                "✅ Recetas digitales con OCR\n" +
                "✅ Programa de fidelización y puntos\n" +
                "✅ Notificaciones push (Firebase)\n" +
                "✅ Tracking en tiempo real vía WebSocket\n\n" +
                "**Autenticación:** La mayoría de endpoints requieren JWT. Obtén un token en /api/auth/login\n\n" +
                "**WebSocket:** Endpoint de tracking en tiempo real: ws://localhost:" + serverPort + "/ws-delivery";
        
        return new OpenAPI()
                .info(new Info()
                        .title("API Farmacia Dolores")
                        .version("1.0.0")
                        .description(description)
                        .contact(new Contact()
                                .name("Equipo Farmacia Dolores")
                                .email("dev@farmaciadolores.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:" + serverPort)
                                .description("Servidor Local"),
                        new Server()
                                .url("https://api.farmaciadolores.com")
                                .description("Servidor Producción")
                ))
                .components(new Components()
                        .addSecuritySchemes("bearer-jwt", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .in(SecurityScheme.In.HEADER)
                                .name("Authorization")
                                .description("Token JWT. Formato: Bearer {token}")))
                .addSecurityItem(new SecurityRequirement().addList("bearer-jwt"));
    }
}

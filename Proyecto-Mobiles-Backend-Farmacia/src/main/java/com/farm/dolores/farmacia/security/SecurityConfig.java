package com.farm.dolores.farmacia.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/auth/**",
                                "/actuator/health",
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                // WebSocket endpoints (sin autenticación)
                                "/ws-delivery/**",
                                "/app/**",
                                "/topic/**",
                                // catálogo público (opcional, ajusta según necesidad)
                                "/api/productos/catalogo",
                                "/api/productos/catalogo/search",
                                // Archivos estáticos (QR, imágenes)
                                "/uploads/**"
                        ).permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/productos/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/categorias/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/productos/**").hasRole("FARMACEUTICO")
                        .requestMatchers(HttpMethod.PUT, "/api/productos/**").hasRole("FARMACEUTICO")
                        .requestMatchers(HttpMethod.DELETE, "/api/productos/**").hasRole("FARMACEUTICO")

                        // Administración de roles y asignaciones: solo ADMIN
                        .requestMatchers("/api/roles/**").hasRole("ADMIN")
                        .requestMatchers("/api/usuariorol/**").hasRole("ADMIN")

                        // Flujos específicos por dominio
                        .requestMatchers(HttpMethod.POST, "/api/recetas/**").hasRole("MEDICO")
                        .requestMatchers(HttpMethod.POST, "/api/repartos/**").hasRole("REPARTIDOR")
                        .requestMatchers(HttpMethod.POST, "/api/pedidos/**").hasAnyRole("CLIENTE", "FARMACEUTICO", "MEDICO")
                        
                        // Recetas digitales (OCR) - Cliente y Admin pueden procesar
                        .requestMatchers("/api/recetas-digitales/**").hasAnyRole("CLIENTE", "ADMIN", "FARMACEUTICO")
                        
                        .anyRequest().authenticated()
                )
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}

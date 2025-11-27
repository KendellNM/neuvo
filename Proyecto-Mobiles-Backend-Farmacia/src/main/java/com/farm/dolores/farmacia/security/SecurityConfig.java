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
                                // catálogo público
                                "/api/productos/catalogo",
                                "/api/productos/catalogo/search",
                                "/api/productos/buscar",
                                // Categorías públicas
                                "/api/categorias",
                                "/api/categorias/**",
                                // Archivos estáticos (QR, imágenes)
                                "/uploads/**"
                        ).permitAll()
                        // Productos: GET público, POST/PUT/DELETE solo FARMACEUTICO o ADMIN
                        .requestMatchers(HttpMethod.GET, "/api/productos/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/productos").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/productos/**").hasAnyRole("FARMACEUTICO", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/productos/**").hasAnyRole("FARMACEUTICO", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/productos/**").hasAnyRole("FARMACEUTICO", "ADMIN")

                        // Administración de roles y asignaciones: solo ADMIN
                        .requestMatchers("/api/roles/**").hasRole("ADMIN")
                        .requestMatchers("/api/usuariorol/**").hasRole("ADMIN")

                        // Flujos específicos por dominio
                        .requestMatchers(HttpMethod.POST, "/api/recetas/**").hasRole("MEDICO")
                        .requestMatchers(HttpMethod.POST, "/api/repartos/**").hasRole("REPARTIDOR")
                        
                        // Pedidos - Cliente puede crear y ver sus pedidos
                        .requestMatchers(HttpMethod.GET, "/api/pedidos/**").hasAnyRole("CLIENTE", "FARMACEUTICO", "ADMIN", "REPARTIDOR")
                        .requestMatchers(HttpMethod.POST, "/api/pedidos/**").hasAnyRole("CLIENTE", "FARMACEUTICO", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/pedidos/**").hasAnyRole("CLIENTE", "FARMACEUTICO", "ADMIN", "REPARTIDOR")
                        
                        // Usuario actual - todos los autenticados
                        .requestMatchers("/api/usuarios/me").authenticated()
                        .requestMatchers("/api/usuarios/current").authenticated()
                        
                        // Direcciones - Cliente puede gestionar sus direcciones
                        .requestMatchers("/api/direcciones/**").hasAnyRole("CLIENTE", "ADMIN")
                        
                        // Recetas digitales (OCR) - Cliente y Admin pueden procesar
                        .requestMatchers("/api/recetas-digitales/**").hasAnyRole("CLIENTE", "ADMIN", "FARMACEUTICO")
                        
                        // Repartidores - ver ubicación
                        .requestMatchers(HttpMethod.GET, "/api/repartidores/**").hasAnyRole("CLIENTE", "ADMIN", "REPARTIDOR")
                        .requestMatchers(HttpMethod.PUT, "/api/repartidores/**").hasAnyRole("REPARTIDOR", "ADMIN")
                        
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

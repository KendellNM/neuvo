package com.farm.dolores.farmacia.controller;

import com.farm.dolores.farmacia.dto.AuthRequest;
import com.farm.dolores.farmacia.dto.AuthResponse;
import com.farm.dolores.farmacia.dto.PasswordResetRequest;
import com.farm.dolores.farmacia.dto.ResetPasswordDto;
import com.farm.dolores.farmacia.dto.RegisterRequest;
import com.farm.dolores.farmacia.dto.RegisterClienteRequest;
import com.farm.dolores.farmacia.dto.RegisterClienteResponse;
import com.farm.dolores.farmacia.entity.Usuarios;
import com.farm.dolores.farmacia.repository.UsuariosRepository;
import com.farm.dolores.farmacia.security.JwtUtil;
import com.farm.dolores.farmacia.security.CustomUserDetailsService;
import com.farm.dolores.farmacia.service.RegistroClienteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/auth")
@CrossOrigin("http://localhost:5173/")
@Tag(name = "🔐 Autenticación", description = "Endpoints para autenticación, registro y gestión de sesiones de usuarios")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UsuariosRepository usuariosRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RegistroClienteService registroClienteService;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Operation(
            summary = "Iniciar sesión",
            description = """
                    Autentica un usuario con sus credenciales y devuelve un token JWT.
                    
                    El token debe ser incluido en el header Authorization de las siguientes peticiones:
                    `Authorization: Bearer {token}`
                    
                    **Usuarios de prueba:**
                    - Admin: `admin@test.com` / `password123`
                    - Cliente: `cliente001@test.com` / `password123`
                    - Farmacéutico: `farmaceutico001@test.com` / `password123`
                    """,
            tags = {"🔐 Autenticación"}
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Login exitoso - Token JWT generado",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AuthResponse.class),
                            examples = @ExampleObject(
                                    name = "Respuesta exitosa",
                                    value = """
                                            {
                                              "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
                                              "roles": ["ROLE_CLIENTE"]
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Credenciales inválidas",
                    content = @Content(
                            mediaType = "text/plain",
                            examples = @ExampleObject(value = "Credenciales inválidas")
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor",
                    content = @Content(
                            mediaType = "text/plain",
                            examples = @ExampleObject(value = "Error de autenticación")
                    )
            )
    })
    @SecurityRequirement(name = "")
    @PostMapping("/login")
    public ResponseEntity<?> login(
            @Parameter(
                    description = "Credenciales de acceso (correo/usuario y contraseña)",
                    required = true,
                    content = @Content(
                            examples = @ExampleObject(
                                    name = "Ejemplo de login",
                                    value = """
                                            {
                                              "username": "admin@test.com",
                                              "password": "password123"
                                            }
                                            """
                            )
                    )
            )
            @Valid @RequestBody AuthRequest request) {
        try {
            // El authenticationManager ya llama a loadUserByUsername internamente
            var authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );
            
            // Obtener UserDetails del objeto Authentication (sin hacer otra consulta)
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            
            String token = jwtUtil.generateToken(userDetails.getUsername());
            List<String> roles = userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(new AuthResponse(token, roles));
        } catch (BadCredentialsException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales inválidas");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error de autenticación");
        }
    }

    @Operation(
            summary = "Registrar nuevo usuario",
            description = """
                    Crea una nueva cuenta de usuario en el sistema.
                    
                    El usuario se crea con estado ACTIVO y sin roles asignados por defecto.
                    La contraseña se encripta automáticamente antes de almacenarse.
                    
                    Retorna un token JWT que puede usarse inmediatamente.
                    """,
            tags = {"🔐 Autenticación"}
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Usuario registrado exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AuthResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos inválidos o incompletos",
                    content = @Content(mediaType = "text/plain")
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "El correo ya está registrado",
                    content = @Content(
                            mediaType = "text/plain",
                            examples = @ExampleObject(value = "Correo ya registrado")
                    )
            )
    })
    @SecurityRequirement(name = "")
    @PostMapping("/register")
    public ResponseEntity<?> register(
            @Parameter(
                    description = "Datos del nuevo usuario",
                    required = true,
                    content = @Content(
                            examples = @ExampleObject(
                                    name = "Ejemplo de registro",
                                    value = """
                                            {
                                              "usuario": "juanperez",
                                              "correo": "juan.perez@example.com",
                                              "password": "MiPassword123!"
                                            }
                                            """
                            )
                    )
            )
            @Valid @RequestBody RegisterRequest dto) {
        // Validación mínima
        if (dto.getPassword() == null || dto.getPassword().isBlank() ||
                dto.getCorreo() == null || dto.getCorreo().isBlank()) {
            return ResponseEntity.badRequest().body("correo y password son obligatorios");
        }
        if (usuariosRepository.findByCorreo(dto.getCorreo()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Correo ya registrado");
        }

        Usuarios u = new Usuarios();
        // usuario es opcional ahora; si se envía, se guarda, de lo contrario queda nulo
        u.setUsuario(dto.getUsuario());
        u.setCorreo(dto.getCorreo());
        u.setContrasena(passwordEncoder.encode(dto.getPassword()));
        u.setEstado("ACTIVO");
        usuariosRepository.save(u);

        String token = jwtUtil.generateToken(dto.getCorreo());
        UserDetails userDetails = userDetailsService.loadUserByUsername(dto.getCorreo());
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.CREATED).body(new AuthResponse(token, roles));
    }

    // Registro de cliente
    @PostMapping("/register-cliente")
    public ResponseEntity<?> registerCliente(@RequestBody RegisterClienteRequest request) {
        try {
            RegisterClienteResponse resp = registroClienteService.registerCliente(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(resp);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("No se pudo registrar al cliente");
        }
    }

    // Solicitar reset de contraseña por correo
    @PostMapping("/request-reset")
    public ResponseEntity<?> requestReset(@RequestBody PasswordResetRequest request) {
        Optional<Usuarios> opt = usuariosRepository.findByCorreo(request.getCorreo());
        if (opt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Correo no registrado");
        }
        String correo = opt.get().getCorreo();
        String resetToken = jwtUtil.generateResetToken(correo, 15 * 60 * 1000); // 15 min
        // TODO: Enviar por correo electrónico en producción
        return ResponseEntity.ok(new AuthResponse(resetToken, List.of()));
    }

    // Consumir el token de reset para establecer nueva contraseña
    @PostMapping("/reset")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordDto dto) {
        try {
            String correo = jwtUtil.extractUsername(dto.getToken());
            if (!jwtUtil.isTokenValid(dto.getToken(), correo)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token inválido o expirado");
            }
            Usuarios u = usuariosRepository.findByCorreo(correo)
                    .orElseThrow(() -> new RuntimeException("Correo no encontrado"));
            u.setContrasena(passwordEncoder.encode(dto.getNewPassword()));
            usuariosRepository.save(u);
            return ResponseEntity.ok("Contraseña actualizada");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No se pudo actualizar la contraseña");
        }
    }
}


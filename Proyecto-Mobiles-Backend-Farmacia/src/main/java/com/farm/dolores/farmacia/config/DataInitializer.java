package com.farm.dolores.farmacia.config;

import com.farm.dolores.farmacia.entity.Roles;
import com.farm.dolores.farmacia.entity.UsuarioRol;
import com.farm.dolores.farmacia.entity.Usuarios;
import com.farm.dolores.farmacia.repository.RolesRepository;
import com.farm.dolores.farmacia.repository.UsuarioRolRepository;
import com.farm.dolores.farmacia.repository.UsuariosRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UsuariosRepository usuariosRepository;
    private final RolesRepository rolesRepository;
    private final UsuarioRolRepository usuarioRolRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        initializeAdminUser();
    }

    private void initializeAdminUser() {
        // Verificar si el usuario admin ya existe
        Optional<Usuarios> existingAdmin = usuariosRepository.findByUsuario("admin");
        
        if (existingAdmin.isPresent()) {
            log.info("Usuario admin ya existe en la base de datos");
            return;
        }

        log.info("Inicializando usuario admin...");

        try {
            // Crear o buscar el rol de administrador (asumiendo que el ID 1 es ADMIN)
            Roles adminRole = rolesRepository.findById(1L)
                    .orElseGet(() -> {
                        Roles newRole = new Roles();
                        newRole.setNombre("ADMIN");
                        newRole.setDescripcion("Administrador del sistema");
                        newRole.setEstado("activo");
                        return rolesRepository.save(newRole);
                    });

            // Crear el usuario admin
            Usuarios adminUser = new Usuarios();
            adminUser.setUsuario("admin");
            adminUser.setCorreo("admin@farmacia.com");
            adminUser.setContrasena(passwordEncoder.encode("admin123")); // Contraseña por defecto
            adminUser.setEstado("activo");
            adminUser.setFecha_creacion(new Date());
            adminUser.setFecha_actualizacion(new Date());

            // Guardar el usuario
            Usuarios savedUser = usuariosRepository.save(adminUser);

            // Asignar el rol de administrador al usuario
            UsuarioRol usuarioRol = new UsuarioRol();
            usuarioRol.setUsuarios(savedUser);
            usuarioRol.setRoles(adminRole);
            usuarioRol.setEstado("activo");
            usuarioRolRepository.save(usuarioRol);

            log.info("✓ Usuario admin creado exitosamente");
            log.info("  - Usuario: admin");
            log.info("  - Contraseña: admin123");
            log.info("  - Correo: admin@farmacia.com");
            log.warn("⚠ IMPORTANTE: Cambia la contraseña del admin después del primer inicio de sesión");

        } catch (Exception e) {
            log.error("Error al crear el usuario admin: {}", e.getMessage(), e);
        }
    }
}

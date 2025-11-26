package com.farm.dolores.farmacia.config;

import com.farm.dolores.farmacia.entity.Categoria;
import com.farm.dolores.farmacia.entity.Productos;
import com.farm.dolores.farmacia.entity.Roles;
import com.farm.dolores.farmacia.entity.UsuarioRol;
import com.farm.dolores.farmacia.entity.Usuarios;
import com.farm.dolores.farmacia.repository.CategoriaRepository;
import com.farm.dolores.farmacia.repository.ProductosRepository;
import com.farm.dolores.farmacia.repository.RolesRepository;
import com.farm.dolores.farmacia.repository.UsuarioRolRepository;
import com.farm.dolores.farmacia.repository.UsuariosRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
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
    private final CategoriaRepository categoriaRepository;
    private final ProductosRepository productosRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        initializeRoles();
        initializeAdminUser();
        initializeTestUsers();
        initializeCategorias();
        initializeProductos();
    }

    private void initializeRoles() {
        if (rolesRepository.count() > 0) {
            log.info("Roles ya existen en la base de datos");
            return;
        }

        log.info("Inicializando roles...");

        String[][] roles = {
            {"ADMIN", "Administrador del sistema"},
            {"CLIENTE", "Cliente de la farmacia"},
            {"REPARTIDOR", "Repartidor de pedidos"},
            {"FARMACEUTICO", "Farmacéutico"}
        };

        for (String[] rol : roles) {
            Roles r = new Roles();
            r.setNombre(rol[0]);
            r.setDescripcion(rol[1]);
            r.setEstado("activo");
            rolesRepository.save(r);
        }

        log.info("✓ {} roles creados", roles.length);
    }

    private void initializeTestUsers() {
        log.info("═══════════════════════════════════════════════════════════");
        log.info("Inicializando usuarios de prueba...");
        log.info("═══════════════════════════════════════════════════════════");

        // Usuario Cliente
        log.info("Creando usuario CLIENTE...");
        crearUsuarioSiNoExiste("cliente@dolores.com", "cliente123", "CLIENTE", "12345678");
        
        // Usuario Repartidor (DELIVERY)
        log.info("Creando usuario REPARTIDOR...");
        crearUsuarioSiNoExiste("delivery@dolores.com", "delivery123", "REPARTIDOR", "87654321");
        
        // Usuario Farmacéutico
        log.info("Creando usuario FARMACEUTICO...");
        crearUsuarioSiNoExiste("farmaceutico@dolores.com", "farmaceutico123", "FARMACEUTICO", "11223344");

        log.info("═══════════════════════════════════════════════════════════");
        log.info("  ✅ USUARIOS DE PRUEBA DISPONIBLES:");
        log.info("  📧 admin@dolores.com / admin123 (ADMIN)");
        log.info("  📧 cliente@dolores.com / cliente123 (CLIENTE)");
        log.info("  📧 delivery@dolores.com / delivery123 (REPARTIDOR)");
        log.info("  📧 farmaceutico@dolores.com / farmaceutico123 (FARMACEUTICO)");
        log.info("═══════════════════════════════════════════════════════════");
    }

    private void crearUsuarioSiNoExiste(String correo, String password, String rolNombre, String dni) {
        if (usuariosRepository.findByCorreo(correo).isPresent()) {
            log.info("Usuario {} ya existe, omitiendo...", correo);
            return;
        }

        try {
            Roles rol = rolesRepository.findAll().stream()
                .filter(r -> r.getNombre().equals(rolNombre))
                .findFirst().orElse(null);

            if (rol == null) {
                log.error("❌ No se encontró el rol {} para crear usuario {}", rolNombre, correo);
                return;
            }

            Usuarios user = new Usuarios();
            user.setUsuario(correo.split("@")[0]);
            user.setCorreo(correo);
            user.setContrasena(passwordEncoder.encode(password));
            user.setEstado("activo");
            user.setFecha_creacion(new Date());
            user.setFecha_actualizacion(new Date());

            Usuarios savedUser = usuariosRepository.save(user);
            log.info("✓ Usuario creado: {} (ID: {})", correo, savedUser.getId());

            UsuarioRol usuarioRol = new UsuarioRol();
            usuarioRol.setUsuarios(savedUser);
            usuarioRol.setRoles(rol);
            usuarioRol.setEstado("activo");
            usuarioRolRepository.save(usuarioRol);
            log.info("✓ Rol {} asignado a {}", rolNombre, correo);

        } catch (Exception e) {
            log.error("❌ Error creando usuario {}: {}", correo, e.getMessage(), e);
        }
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
            adminUser.setCorreo("admin@dolores.com");
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
            log.info("  - Correo: admin@dolores.com");
            log.warn("⚠ IMPORTANTE: Cambia la contraseña del admin después del primer inicio de sesión");

        } catch (Exception e) {
            log.error("Error al crear el usuario admin: {}", e.getMessage(), e);
        }
    }

    private void initializeCategorias() {
        if (categoriaRepository.count() > 0) {
            log.info("Categorías ya existen en la base de datos");
            return;
        }

        log.info("Inicializando categorías...");

        String[] categorias = {
            "Medicamentos", "Vitaminas", "Cuidado Personal", 
            "Bebés", "Dermocosméticos", "Primeros Auxilios"
        };

        for (String nombre : categorias) {
            Categoria cat = new Categoria();
            cat.setNombre(nombre);
            categoriaRepository.save(cat);
        }

        log.info("✓ {} categorías creadas", categorias.length);
    }

    private void initializeProductos() {
        if (productosRepository.count() > 0) {
            log.info("Productos ya existen en la base de datos");
            return;
        }

        log.info("Inicializando productos de prueba...");

        Categoria medicamentos = categoriaRepository.findAll().stream()
            .filter(c -> c.getNombre().equals("Medicamentos")).findFirst().orElse(null);
        Categoria vitaminas = categoriaRepository.findAll().stream()
            .filter(c -> c.getNombre().equals("Vitaminas")).findFirst().orElse(null);
        Categoria cuidado = categoriaRepository.findAll().stream()
            .filter(c -> c.getNombre().equals("Cuidado Personal")).findFirst().orElse(null);

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.YEAR, 2);
        Date fechaVenc = cal.getTime();

        // Medicamentos
        crearProducto("7501234567890", "Paracetamol 500mg", "Analgésico y antipirético", 
            "Paracetamol", "500mg", false, 8.50, 50, medicamentos, fechaVenc);
        crearProducto("7501234567891", "Ibuprofeno 400mg", "Antiinflamatorio no esteroideo", 
            "Ibuprofeno", "400mg", false, 12.00, 40, medicamentos, fechaVenc);
        crearProducto("7501234567892", "Amoxicilina 500mg", "Antibiótico de amplio espectro", 
            "Amoxicilina", "500mg", true, 25.00, 30, medicamentos, fechaVenc);
        crearProducto("7501234567893", "Omeprazol 20mg", "Protector gástrico", 
            "Omeprazol", "20mg", false, 15.00, 45, medicamentos, fechaVenc);
        crearProducto("7501234567894", "Loratadina 10mg", "Antihistamínico", 
            "Loratadina", "10mg", false, 10.00, 60, medicamentos, fechaVenc);

        // Vitaminas
        crearProducto("7502234567890", "Vitamina C 1000mg", "Suplemento vitamínico", 
            "Ácido Ascórbico", "1000mg", false, 18.00, 80, vitaminas, fechaVenc);
        crearProducto("7502234567891", "Vitamina D3 2000UI", "Suplemento de vitamina D", 
            "Colecalciferol", "2000UI", false, 22.00, 55, vitaminas, fechaVenc);
        crearProducto("7502234567892", "Complejo B", "Vitaminas del complejo B", 
            "Complejo B", "N/A", false, 20.00, 70, vitaminas, fechaVenc);
        crearProducto("7502234567893", "Omega 3", "Ácidos grasos esenciales", 
            "EPA/DHA", "1000mg", false, 35.00, 40, vitaminas, fechaVenc);

        // Cuidado Personal
        crearProducto("7503234567890", "Alcohol en Gel 500ml", "Desinfectante de manos", 
            "Alcohol", "70%", false, 12.00, 100, cuidado, fechaVenc);
        crearProducto("7503234567891", "Protector Solar SPF50", "Protección solar alta", 
            "Filtros UV", "SPF50", false, 45.00, 35, cuidado, fechaVenc);
        crearProducto("7503234567892", "Crema Hidratante", "Hidratación facial", 
            "Ácido Hialurónico", "N/A", false, 38.00, 25, cuidado, fechaVenc);

        log.info("✓ Productos de prueba creados exitosamente");
    }

    private void crearProducto(String codigo, String nombre, String descripcion,
            String principio, String concentracion, boolean receta, double precio,
            int stock, Categoria categoria, Date fechaVenc) {
        Productos p = new Productos();
        p.setCodigoBarras(codigo);
        p.setNombre(nombre);
        p.setDescripcion(descripcion);
        p.setPrincipioActivo(principio);
        p.setConcentracion(concentracion);
        p.setRequerireReceta(receta);
        p.setPrecio(precio);
        p.setStock(stock);
        p.setStockMin(10);
        p.setCategoria(categoria);
        p.setFecha_vencimiento(fechaVenc);
        p.setFecha_registro(new Date());
        p.setEstado("activo");
        productosRepository.save(p);
    }
}

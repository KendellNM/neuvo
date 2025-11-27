package com.farm.dolores.farmacia.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.farm.dolores.farmacia.entity.*;
import com.farm.dolores.farmacia.repository.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

// @Component - DESHABILITADO: Usar DataInitializer en su lugar
@Order(1) // Ejecutar primero
public class DataSeeder implements CommandLineRunner {

    @Autowired private RolesRepository rolesRepository;
    @Autowired private UsuariosRepository usuariosRepository;
    @Autowired private UsuarioRolRepository usuarioRolRepository;
    @Autowired private ClientesRepository clientesRepository;
    @Autowired private CategoriaRepository categoriaRepository;
    @Autowired private LaboratoriosRepository laboratoriosRepository;
    @Autowired private ProductosRepository productosRepository;
    @Autowired private DireccionesRepository direccionesRepository;
    @Autowired private PedidosRepository pedidosRepository;
    @Autowired private PedidoDetalleRepository pedidoDetalleRepository;
    @Autowired private ProgramaFidelizacionRepository programaFidelizacionRepository;
    @Autowired private MedicosRepository medicosRepository;
    @Autowired private FarmaceuticosRepository farmaceuticosRepository;
    @Autowired private RepartidoresRepository repartidoresRepository;
    @Autowired private RecetaDigitalRepository recetaDigitalRepository;
    @Autowired private RecetaDigitalDetalleRepository recetaDigitalDetalleRepository;
    @Autowired private MovimientoPuntosRepository movimientoPuntosRepository;
    @Autowired private CuponRepository cuponRepository;
    @Autowired private NotificacionPushRepository notificacionPushRepository;
    @Autowired private DispositivoClienteRepository dispositivoClienteRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    private Random random = new Random();
    private List<Usuarios> usuarios = new ArrayList<>();
    private List<Clientes> clientes = new ArrayList<>();
    private List<Categoria> categorias = new ArrayList<>();
    private List<Laboratorios> laboratorios = new ArrayList<>();
    private List<Productos> productos = new ArrayList<>();
    private List<Medicos> medicos = new ArrayList<>();
    private List<Farmaceuticos> farmaceuticos = new ArrayList<>();
    private List<Repartidores> repartidores = new ArrayList<>();
    private List<Pedidos> pedidos = new ArrayList<>();
    private List<ProgramaFidelizacion> programasFidelizacion = new ArrayList<>();

    @Override
    public void run(String... args) throws Exception {
        if (rolesRepository.count() == 0) {
            seedData();
            printSummary();
        }
    }

    private void seedData() {
        System.out.println("🔄 Iniciando carga de datos completos...");
        
        // 1. ROLES (5 roles)
        Roles adminRole = createRole(1L, "ROLE_ADMIN", "Administrador del sistema");
        Roles clienteRole = createRole(2L, "ROLE_CLIENTE", "Cliente de la farmacia");
        Roles farmaceuticoRole = createRole(3L, "ROLE_FARMACEUTICO", "Farmacéutico");
        Roles medicoRole = createRole(4L, "ROLE_MEDICO", "Médico");
        Roles repartidorRole = createRole(5L, "ROLE_REPARTIDOR", "Repartidor");

        // 2. USUARIOS (25 usuarios)
        createUsuarios(adminRole, clienteRole, farmaceuticoRole, medicoRole, repartidorRole);

        // 3. CATEGORÍAS (10 categorías)
        createCategorias();

        // 4. LABORATORIOS (15 laboratorios)
        createLaboratorios();

        // 5. CLIENTES (20 clientes)
        createClientes();

        // 6. MÉDICOS (8 médicos)
        createMedicos();

        // 7. FARMACÉUTICOS (6 farmacéuticos)
        createFarmaceuticos();

        // 8. REPARTIDORES (10 repartidores)
        createRepartidores();

        // 9. PRODUCTOS (50 productos)
        createProductos();

        // 10. DIRECCIONES (30 direcciones)
        createDirecciones();

        // 11. PEDIDOS (25 pedidos)
        createPedidos();

        // 12. PROGRAMA DE FIDELIZACIÓN (20 programas)
        createProgramasFidelizacion();

        // 13. MOVIMIENTOS DE PUNTOS (40 movimientos)
        createMovimientosPuntos();

        // 14. CUPONES (15 cupones)
        createCupones();

        // 15. RECETAS DIGITALES (12 recetas)
        createRecetasDigitales();

        // 16. DISPOSITIVOS CLIENTES (25 dispositivos)
        createDispositivosClientes();

        // 17. NOTIFICACIONES PUSH (30 notificaciones)
        createNotificacionesPush();
    }

    private Roles createRole(Long id, String nombre, String descripcion) {
        // Check if role already exists by name instead of ID
        return rolesRepository.findByNombre(nombre).orElseGet(() -> {
            Roles role = new Roles();
            // Don't set ID manually - let it auto-generate
            role.setNombre(nombre);
            role.setDescripcion(descripcion);
            role.setEstado("ACTIVO");
            return rolesRepository.save(role);
        });
    }

    private Usuarios createUser(Long id, String correo, String usuario) {
        Usuarios user = new Usuarios();
        user.setIdUsuarios(id);
        user.setCorreo(correo);
        user.setContrasena(passwordEncoder.encode("password123"));
        user.setUsuario(usuario);
        user.setEstado("ACTIVO");
        user.setFecha_creacion(new Date());
        user.setFecha_actualizacion(new Date());
        return usuariosRepository.save(user);
    }

    private UsuarioRol createUsuarioRol(Long id, Usuarios usuario, Roles rol) {
        UsuarioRol usuarioRol = new UsuarioRol();
        usuarioRol.setIdUsuarioRol(id);
        usuarioRol.setUsuarios(usuario);
        usuarioRol.setRoles(rol);
        usuarioRol.setEstado("ACTIVO");
        return usuarioRolRepository.save(usuarioRol);
    }

    private Clientes createCliente(Long id, String nombres, String apellidos, String dni, 
                                 String telefono, Usuarios usuario) {
        Clientes cliente = new Clientes();
        cliente.setIdClientes(id);
        cliente.setNombres(nombres);
        cliente.setApellidos(apellidos);
        cliente.setDni(dni);
        cliente.setTelefono(telefono);
        cliente.setFecha_nacimiento(new Date());
        cliente.setGenero("M");
        cliente.setEstado("ACTIVO");
        cliente.setClientes(usuario);
        return clientesRepository.save(cliente);
    }

    private Categoria createCategoria(Long id, String nombre, String descripcion) {
        Categoria categoria = new Categoria();
        categoria.setId(id);
        categoria.setNombre(nombre);
        return categoriaRepository.save(categoria);
    }

    private Laboratorios createLaboratorio(Long id, String nombre, String pais, 
                                         String telefono, String email) {
        Laboratorios lab = new Laboratorios();
        lab.setIdLaboratorios(id);
        lab.setNombre(nombre);
        lab.setPais_orien(pais);
        lab.setTelefono(telefono);
        lab.setCorreo(email);
        lab.setEstado("ACTIVO");
        return laboratoriosRepository.save(lab);
    }

    private Productos createProducto(Long id, String nombre, String descripcion, 
                                   BigDecimal precio, Integer stock, String codigoBarras,
                                   boolean requiereReceta, Categoria categoria, Laboratorios laboratorio) {
        Productos producto = new Productos();
        producto.setIdProductos(id);
        producto.setNombre(nombre);
        producto.setDescripcion(descripcion);
        producto.setPrecio(precio.doubleValue());
        producto.setStock(stock);
        producto.setFecha_vencimiento(new Date());
        producto.setRequerireReceta(requiereReceta);
        producto.setCodigoBarras(codigoBarras);
        producto.setEstado("ACTIVO");
        producto.setFecha_registro(new Date());
        producto.setCategoria(categoria);
        producto.setLaboratorios(laboratorio);
        return productosRepository.save(producto);
    }

    private Direcciones createDireccion(Long id, String direccion, String distrito, 
                                      String provincia, String departamento, 
                                      String referencia, boolean esPrincipal, Clientes cliente) {
        Direcciones dir = new Direcciones();
        dir.setIdDirecciones(id);
        dir.setDireccion(direccion);
        dir.setDistrito(distrito);
        dir.setProvincia(provincia);
        dir.setDepartamento(departamento);
        dir.setReferencia(referencia);
        dir.setEstado("ACTIVO");
        dir.setClientes(cliente);
        return direccionesRepository.save(dir);
    }

    private Pedidos createPedido(Long id, String estado, BigDecimal total, 
                               String metodoPago, Clientes cliente, Direcciones direccion) {
        Pedidos pedido = new Pedidos();
        pedido.setIdPedidos(id);
        pedido.setFechaPedido(new Date());
        pedido.setEstado(estado);
        pedido.setTotal(total.doubleValue());
        pedido.setMetodoPago(metodoPago);
        pedido.setClientes(cliente);
        pedido.setDirecciones(direccion);
        return pedidosRepository.save(pedido);
    }

    private PedidoDetalle createPedidoDetalle(Long id, Integer cantidad, 
                                            BigDecimal precioUnitario, BigDecimal subtotal,
                                            Pedidos pedido, Productos producto) {
        PedidoDetalle detalle = new PedidoDetalle();
        detalle.setIdPedidoDetalle(id);
        detalle.setCantidad(cantidad);
        detalle.setPrecioUnitario(precioUnitario.doubleValue());
        detalle.setSubtotal(subtotal.doubleValue());
        detalle.setProductos(producto);
        return pedidoDetalleRepository.save(detalle);
    }

    private ProgramaFidelizacion createProgramaFidelizacion(Long id, Clientes cliente, 
                                                          Integer puntosActuales, 
                                                          Integer puntosAcumulados, 
                                                          String nivelMembresia) {
        ProgramaFidelizacion programa = new ProgramaFidelizacion();
        programa.setIdProgramaFidelizacion(id);
        programa.setCliente(cliente);
        programa.setPuntosActuales(puntosActuales);
        programa.setPuntosAcumulados(puntosAcumulados);
        programa.setNivelMembresia(nivelMembresia);
        programa.setFechaRegistro(new Date());
        programa.setFechaUltimaActualizacion(new Date());
        return programaFidelizacionRepository.save(programa);
    }

    private void createUsuarios(Roles adminRole, Roles clienteRole, Roles farmaceuticoRole,
                               Roles medicoRole, Roles repartidorRole) {
        // Admin
        Usuarios admin = createUser(1L, "admin@test.com", "admin");
        usuarios.add(admin);
        createUsuarioRol(1L, admin, adminRole);

        // Clientes (20)
        for (int i = 1; i <= 20; i++) {
            Usuarios user = createUser((long)(i + 1), "cliente" + String.format("%03d", i) + "@test.com", "cliente" + String.format("%03d", i));
            usuarios.add(user);
            createUsuarioRol((long)(i + 1), user, clienteRole);
        }

        // Farmacéuticos (6)
        for (int i = 1; i <= 6; i++) {
            Usuarios user = createUser((long)(i + 21), "farmaceutico" + String.format("%03d", i) + "@test.com", "farmaceutico" + String.format("%03d", i));
            usuarios.add(user);
            createUsuarioRol((long)(i + 21), user, farmaceuticoRole);
        }

        // Médicos (8)
        for (int i = 1; i <= 8; i++) {
            Usuarios user = createUser((long)(i + 27), "medico" + String.format("%03d", i) + "@test.com", "medico" + String.format("%03d", i));
            usuarios.add(user);
            createUsuarioRol((long)(i + 27), user, medicoRole);
        }

        // Repartidores (10)
        for (int i = 1; i <= 10; i++) {
            Usuarios user = createUser((long)(i + 35), "repartidor" + String.format("%03d", i) + "@test.com", "repartidor" + String.format("%03d", i));
            usuarios.add(user);
            createUsuarioRol((long)(i + 35), user, repartidorRole);
        }
    }

    private void createCategorias() {
        String[] nombres = {
            "Analgésicos", "Antibióticos", "Vitaminas", "Antiinflamatorios", "Dermatológicos",
            "Digestivos", "Respiratorios", "Cardiovasculares", "Neurológicos", "Oftalmológicos"
        };
        String[] descripciones = {
            "Medicamentos para el dolor", "Medicamentos antibacterianos", "Suplementos vitamínicos",
            "Medicamentos antiinflamatorios", "Productos para la piel", "Medicamentos digestivos",
            "Medicamentos respiratorios", "Medicamentos cardiovasculares", "Medicamentos neurológicos",
            "Productos oftalmológicos"
        };

        for (int i = 0; i < nombres.length; i++) {
            Categoria cat = createCategoria((long)(i + 1), nombres[i], descripciones[i]);
            categorias.add(cat);
        }
    }

    private void createLaboratorios() {
        String[] nombres = {
            "Bayer", "Pfizer", "Roche", "Novartis", "Sanofi", "Johnson & Johnson", "Abbott", "Merck",
            "GSK", "AstraZeneca", "Boehringer", "Takeda", "Eli Lilly", "Bristol Myers", "Amgen"
        };
        String[] paises = {
            "Alemania", "Estados Unidos", "Suiza", "Suiza", "Francia", "Estados Unidos", "Estados Unidos", "Estados Unidos",
            "Reino Unido", "Reino Unido", "Alemania", "Japón", "Estados Unidos", "Estados Unidos", "Estados Unidos"
        };

        for (int i = 0; i < nombres.length; i++) {
            Laboratorios lab = createLaboratorio((long)(i + 1), nombres[i], paises[i], 
                                               "+1" + String.format("%07d", random.nextInt(9999999)), 
                                               "contacto@" + nombres[i].toLowerCase().replace(" ", "") + ".com");
            laboratorios.add(lab);
        }
    }

    private void createClientes() {
        String[] nombres = {
            "Cliente", "Usuario", "Persona", "Paciente", "Comprador", "Visitante", "Miembro", "Suscriptor",
            "Beneficiario", "Titular", "Portador", "Afiliado", "Registrado", "Activo", "Regular",
            "Frecuente", "Nuevo", "Antiguo", "Preferido", "Premium"
        };

        for (int i = 0; i < 20; i++) {
            String dni = String.format("%08d", 10000000 + i);
            String telefono = "9" + String.format("%08d", random.nextInt(99999999));
            String genero = (i % 2 == 0) ? "M" : "F";
            
            Usuarios usuario = usuarios.get(i + 1); // +1 porque el admin es el índice 0
            
            Clientes cliente = createCliente((long)(i + 1), nombres[i], String.format("Test%03d", i + 1), 
                                           dni, telefono, usuario);
            clientes.add(cliente);
        }
    }

    private void createMedicos() {
        String[] especialidades = {
            "Medicina General", "Pediatría", "Cardiología", "Dermatología", "Neurología", 
            "Gastroenterología", "Oftalmología", "Traumatología"
        };

        for (int i = 0; i < 8; i++) {
            String cmp = "CMP" + String.format("%05d", 10000 + i);
            String telefono = "9" + String.format("%08d", random.nextInt(99999999));
            
            Usuarios usuario = usuarios.get(i + 28); // Médicos empiezan en índice 28
            
            Medicos medico = createMedico((long)(i + 1), "Dr. Médico", String.format("Test%03d", i + 1),
                                        especialidades[i], cmp, telefono, usuario);
            medicos.add(medico);
        }
    }

    private void createFarmaceuticos() {
        String[] turnos = {"MAÑANA", "TARDE", "NOCHE", "MAÑANA", "TARDE", "NOCHE"};

        for (int i = 0; i < 6; i++) {
            String cqf = "CQF" + String.format("%05d", 20000 + i);
            String telefono = "9" + String.format("%08d", random.nextInt(99999999));
            
            Usuarios usuario = usuarios.get(i + 22); // Farmacéuticos empiezan en índice 22
            
            Farmaceuticos farmaceutico = createFarmaceutico((long)(i + 1), "Farmacéutico", String.format("Test%03d", i + 1),
                                                           cqf, telefono, turnos[i], usuario);
            farmaceuticos.add(farmaceutico);
        }
    }

    private void createRepartidores() {
        String[] tiposVehiculo = {"MOTO", "BICICLETA", "AUTO", "MOTO", "BICICLETA", "MOTO", "AUTO", "MOTO", "BICICLETA", "MOTO"};

        for (int i = 0; i < 10; i++) {
            String dni = String.format("%08d", 20000000 + i);
            String telefono = "9" + String.format("%08d", random.nextInt(99999999));
            String placa = String.format("ABC-%03d", 100 + i);
            
            Usuarios usuario = usuarios.get(i + 36); // Repartidores empiezan en índice 36
            
            Repartidores repartidor = createRepartidor((long)(i + 1), "Repartidor", String.format("Test%03d", i + 1),
                                                      dni, telefono, placa, tiposVehiculo[i], usuario);
            repartidores.add(repartidor);
        }
    }

    private void createProductos() {
        String[] medicamentos = {
            "Aspirina 500mg", "Paracetamol 500mg", "Ibuprofeno 400mg", "Amoxicilina 500mg", "Diclofenaco 50mg",
            "Omeprazol 20mg", "Loratadina 10mg", "Cetirizina 10mg", "Vitamina C 1000mg", "Vitamina D3 1000UI",
            "Complejo B", "Calcio + Magnesio", "Omega 3", "Probióticos", "Multivitamínico",
            "Crema Hidratante", "Protector Solar SPF50", "Shampoo Anticaspa", "Pasta Dental", "Enjuague Bucal",
            "Alcohol en Gel", "Mascarillas N95", "Termómetro Digital", "Tensiómetro", "Glucómetro",
            "Azitromicina 500mg", "Ciprofloxacino 500mg", "Metformina 850mg", "Atorvastatina 20mg", "Losartán 50mg",
            "Amlodipino 5mg", "Enalapril 10mg", "Furosemida 40mg", "Digoxina 0.25mg", "Warfarina 5mg",
            "Insulina Glargina", "Insulina Regular", "Salbutamol Inhalador", "Beclometasona Spray", "Montelukast 10mg",
            "Prednisona 20mg", "Dexametasona 4mg", "Hidrocortisona Crema", "Betametasona Crema", "Clotrimazol Crema",
            "Ketoconazol Shampoo", "Minoxidil 5%", "Finasteride 1mg", "Sildenafil 50mg", "Tadalafil 20mg"
        };

        for (int i = 0; i < 50; i++) {
            BigDecimal precio = new BigDecimal(5 + random.nextDouble() * 95); // Entre 5 y 100
            precio = precio.setScale(2, BigDecimal.ROUND_HALF_UP);
            
            int stock = 10 + random.nextInt(191); // Entre 10 y 200
            String codigoBarras = "75012345" + String.format("%05d", 67890 + i);
            boolean requiereReceta = i >= 25; // Los últimos 25 requieren receta
            
            Categoria categoria = categorias.get(random.nextInt(categorias.size()));
            Laboratorios laboratorio = laboratorios.get(random.nextInt(laboratorios.size()));
            
            Productos producto = createProducto((long)(i + 1), medicamentos[i], 
                                              "Descripción detallada de " + medicamentos[i],
                                              precio, stock, codigoBarras, requiereReceta, categoria, laboratorio);
            productos.add(producto);
        }
    }

    private void createDirecciones() {
        String[] distritos = {
            "Miraflores", "San Isidro", "Surco", "La Molina", "San Borja", "Barranco", "Chorrillos", "Magdalena",
            "Pueblo Libre", "Jesús María", "Lince", "Breña", "Lima", "Rímac", "Los Olivos"
        };
        String[] calles = {
            "Av. Principal", "Jr. Central", "Calle Real", "Av. Universitaria", "Jr. Comercio", "Calle Lima",
            "Av. Grau", "Jr. Unión", "Calle Bolívar", "Av. Arequipa", "Jr. Cusco", "Calle Piura",
            "Av. Brasil", "Jr. Tacna", "Calle Junín"
        };

        for (int i = 0; i < 30; i++) {
            String direccion = calles[random.nextInt(calles.length)] + " " + (100 + random.nextInt(900));
            String distrito = distritos[random.nextInt(distritos.length)];
            String referencia = "Referencia " + (i + 1);
            
            Clientes cliente = clientes.get(i % clientes.size());
            boolean esPrincipal = (i < 20); // Una dirección principal por cliente
            
            createDireccion((long)(i + 1), direccion, distrito, "Lima", "Lima", referencia, esPrincipal, cliente);
        }
    }

    private void createPedidos() {
        String[] estados = {"PENDIENTE", "EN_PREPARACION", "LISTO", "ASIGNADO", "EN_CAMINO", "ENTREGADO"};
        String[] metodosPago = {"EFECTIVO", "TARJETA", "YAPE", "PLIN"};
        String[] observaciones = {
            "Entregar en portería", "Llamar al llegar", "Dejar con el vecino",
            "Tocar timbre 2 veces", "Urgente", "Entregar antes de las 6pm",
            "Cliente frecuente", "Pago exacto", null, null
        };

        for (int i = 0; i < 25; i++) {
            String estado = estados[random.nextInt(estados.length)];
            String metodoPago = metodosPago[random.nextInt(metodosPago.length)];
            BigDecimal total = new BigDecimal(20 + random.nextDouble() * 180);
            total = total.setScale(2, BigDecimal.ROUND_HALF_UP);
            
            Clientes cliente = clientes.get(random.nextInt(clientes.size()));
            Direcciones direccion = cliente.getDireccioness().isEmpty() ? null : 
                                   cliente.getDireccioness().iterator().next();
            
            Long pedidoId = (i == 0) ? 123L : (long)(i + 1);
            if (i == 0) estado = "EN_CAMINO";
            
            Pedidos pedido = createPedido(pedidoId, estado, total, metodoPago, cliente, direccion);
            
            // Asignar repartidor a pedidos ASIGNADO, EN_CAMINO o ENTREGADO
            if (estado.equals("ASIGNADO") || estado.equals("EN_CAMINO") || estado.equals("ENTREGADO")) {
                if (!repartidores.isEmpty()) {
                    Repartidores repartidor = repartidores.get(random.nextInt(repartidores.size()));
                    pedido.setRepartidor(repartidor);
                }
            }
            
            // Agregar observaciones aleatorias
            String obs = observaciones[random.nextInt(observaciones.length)];
            if (obs != null) {
                pedido.setObservaciones(obs);
            }
            
            // Generar número de pedido descriptivo
            pedido.setNumeroPedido(1000 + i);
            
            pedidos.add(pedido);
            
            // Crear detalles del pedido (1-3 productos)
            int numProductos = 1 + random.nextInt(3);
            BigDecimal totalCalculado = BigDecimal.ZERO;
            
            for (int j = 0; j < numProductos; j++) {
                Productos producto = productos.get(random.nextInt(productos.size()));
                int cantidad = 1 + random.nextInt(3);
                BigDecimal precioUnitario = BigDecimal.valueOf(producto.getPrecio());
                BigDecimal subtotal = precioUnitario.multiply(new BigDecimal(cantidad));
                totalCalculado = totalCalculado.add(subtotal);
                
                createPedidoDetalle((long)(i * 10 + j + 1), cantidad, precioUnitario, subtotal, pedido, producto);
            }
            
            pedido.setTotal(totalCalculado.doubleValue());
            pedidosRepository.save(pedido);
        }
    }

    private void createProgramasFidelizacion() {
        String[] niveles = {"BRONCE", "PLATA", "ORO", "PLATINO"};
        
        for (int i = 0; i < 20; i++) {
            Clientes cliente = clientes.get(i);
            
            int puntosAcumulados = random.nextInt(15000);
            int puntosActuales = Math.max(0, puntosAcumulados - random.nextInt(5000));
            
            String nivel;
            if (puntosAcumulados < 2000) nivel = "BRONCE";
            else if (puntosAcumulados < 5000) nivel = "PLATA";
            else if (puntosAcumulados < 10000) nivel = "ORO";
            else nivel = "PLATINO";
            
            ProgramaFidelizacion programa = createProgramaFidelizacion((long)(i + 1), cliente, 
                                                                     puntosActuales, puntosAcumulados, nivel);
            programasFidelizacion.add(programa);
        }
    }

    private void createMovimientosPuntos() {
        String[] tipos = {"ACUMULACION", "CANJE", "EXPIRACION", "BONIFICACION"};
        String[] descripciones = {
            "Compra de productos", "Canje por cupón", "Puntos expirados", "Bonificación especial",
            "Compra con receta", "Referido nuevo cliente", "Cumpleaños", "Promoción mensual"
        };

        for (int i = 0; i < 40; i++) {
            ProgramaFidelizacion programa = programasFidelizacion.get(random.nextInt(programasFidelizacion.size()));
            String tipo = tipos[random.nextInt(tipos.length)];
            String descripcion = descripciones[random.nextInt(descripciones.length)];
            
            int puntos;
            if (tipo.equals("CANJE") || tipo.equals("EXPIRACION")) {
                puntos = -(50 + random.nextInt(451)); // Negativos entre -50 y -500
            } else {
                puntos = 25 + random.nextInt(476); // Positivos entre 25 y 500
            }
            
            Pedidos pedido = null;
            if (tipo.equals("ACUMULACION") && !pedidos.isEmpty()) {
                pedido = pedidos.get(random.nextInt(pedidos.size()));
            }
            
            createMovimientoPuntos((long)(i + 1), programa, puntos, tipo, descripcion, pedido);
        }
    }

    private void createCupones() {
        String[] codigos = {
            "DESC10", "DESC15", "DESC20", "DESC25", "FIJO5", "FIJO10", "FIJO15", "FIJO20",
            "NUEVO10", "VIP15", "CUMPLE20", "REFERIDO", "PROMO25", "ESPECIAL", "WEEKEND"
        };
        String[] descripciones = {
            "Descuento 10% en toda la tienda", "Descuento 15% en medicamentos", "Descuento 20% en vitaminas",
            "Descuento 25% para VIP", "Descuento fijo S/ 5.00", "Descuento fijo S/ 10.00", "Descuento fijo S/ 15.00",
            "Descuento fijo S/ 20.00", "Descuento 10% nuevos clientes", "Descuento 15% clientes VIP",
            "Descuento 20% cumpleaños", "Cupón por referido", "Promoción 25% especial", "Oferta especial",
            "Descuento fin de semana"
        };

        for (int i = 0; i < 15; i++) {
            int puntosRequeridos = 100 + (i * 50);
            double descuentoPorcentaje = 0.0;
            double descuentoMonto = 0.0;
            
            if (i < 8) {
                descuentoPorcentaje = 10.0 + (i * 2.5);
            } else {
                descuentoMonto = 5.0 + ((i - 8) * 5.0);
            }
            
            int usoMaximo = 10 + random.nextInt(91); // Entre 10 y 100
            int usoActual = random.nextInt(usoMaximo / 2);
            
            createCupon((long)(i + 1), codigos[i], descripciones[i], puntosRequeridos,
                       descuentoPorcentaje, descuentoMonto, usoMaximo, usoActual);
        }
    }

    private void createRecetasDigitales() {
        String[] estados = {"PENDIENTE", "PROCESADA", "VALIDADA", "RECHAZADA"};
        String[] textosExtraidos = {
            "Amoxicilina 500mg - 1 tableta cada 8 horas por 7 días",
            "Ibuprofeno 400mg - 1 tableta cada 6 horas por 3 días",
            "Omeprazol 20mg - 1 cápsula en ayunas por 14 días",
            "Loratadina 10mg - 1 tableta diaria por 5 días",
            "Azitromicina 500mg - 1 tableta diaria por 3 días",
            "Paracetamol 500mg - 1 tableta cada 8 horas según necesidad",
            "Vitamina D3 1000UI - 1 cápsula diaria por 30 días",
            "Metformina 850mg - 1 tableta cada 12 horas con alimentos",
            "Atorvastatina 20mg - 1 tableta nocturna",
            "Losartán 50mg - 1 tableta diaria en la mañana",
            "Salbutamol inhalador - 2 puff cada 6 horas según necesidad",
            "Prednisona 20mg - 1 tableta diaria por 5 días"
        };

        for (int i = 0; i < 12; i++) {
            String imagenUrl = "/uploads/recetas/receta_" + String.format("%03d", i + 1) + ".jpg";
            String estado = estados[random.nextInt(estados.length)];
            String textoExtraido = (estado.equals("PENDIENTE")) ? null : textosExtraidos[i];
            
            Clientes cliente = clientes.get(random.nextInt(clientes.size()));
            Medicos medico = (estado.equals("PENDIENTE")) ? null : medicos.get(random.nextInt(medicos.size()));
            
            LocalDateTime fechaCreacion = LocalDateTime.now().minusDays(random.nextInt(30));
            LocalDateTime fechaProcesamiento = (estado.equals("PENDIENTE")) ? null : fechaCreacion.plusHours(1 + random.nextInt(24));
            
            RecetaDigital receta = createRecetaDigital((long)(i + 1), imagenUrl, textoExtraido, estado,
                                                     fechaCreacion, fechaProcesamiento, cliente, medico);
            
            // Crear detalles si la receta está procesada
            if (!estado.equals("PENDIENTE") && i < productos.size()) {
                Productos producto = productos.get(i + 25); // Usar productos que requieren receta
                createRecetaDigitalDetalle((long)(i + 1), textosExtraidos[i].split(" - ")[0], 
                                         1 + random.nextInt(3), textosExtraidos[i].split(" - ")[1],
                                         !estado.equals("RECHAZADA"), receta, producto);
            }
        }
    }

    private void createDispositivosClientes() {
        String[] plataformas = {"ANDROID", "IOS"};
        
        for (int i = 0; i < 25; i++) {
            Clientes cliente = clientes.get(i % clientes.size());
            String plataforma = plataformas[random.nextInt(plataformas.length)];
            String fcmToken = "fcm_token_" + plataforma.toLowerCase() + "_" + String.format("%06d", i + 1);
            boolean activo = random.nextBoolean();
            
            createDispositivoCliente((long)(i + 1), cliente, fcmToken, plataforma, activo);
        }
    }

    private void createNotificacionesPush() {
        String[] tipos = {"PEDIDO", "PROMOCION", "SISTEMA", "RECORDATORIO"};
        String[] titulos = {
            "Pedido Confirmado", "Pedido en Preparación", "Pedido Listo", "Repartidor en Camino", "Pedido Entregado",
            "Promoción Especial", "Descuento Exclusivo", "Oferta Limitada", "Nueva Promoción",
            "Puntos Acumulados", "Receta Validada", "Cupón Disponible", "Nivel Actualizado",
            "Recordatorio Medicamento", "Cita Médica", "Renovar Receta"
        };
        String[] mensajes = {
            "Tu pedido ha sido confirmado y está en preparación", "Tu pedido está siendo preparado por nuestro equipo",
            "Tu pedido está listo para envío", "El repartidor está en camino a tu dirección", "Tu pedido ha sido entregado exitosamente",
            "Descuento 20% en vitaminas. ¡Aprovecha ahora!", "Oferta exclusiva para ti", "Promoción por tiempo limitado",
            "Nueva promoción disponible en tu app", "Has acumulado puntos con tu última compra", "Tu receta digital ha sido validada",
            "Tienes un cupón disponible para canjear", "Tu nivel de membresía ha sido actualizado",
            "Recordatorio: es hora de tomar tu medicamento", "Tienes una cita médica programada", "Es momento de renovar tu receta"
        };

        for (int i = 0; i < 30; i++) {
            Clientes cliente = clientes.get(random.nextInt(clientes.size()));
            String tipo = tipos[random.nextInt(tipos.length)];
            String titulo = titulos[random.nextInt(titulos.length)];
            String mensaje = mensajes[random.nextInt(mensajes.length)];
            boolean leida = random.nextBoolean();
            
            Pedidos pedido = null;
            if (tipo.equals("PEDIDO") && !pedidos.isEmpty()) {
                pedido = pedidos.get(random.nextInt(pedidos.size()));
            }
            
            LocalDateTime fechaEnvio = LocalDateTime.now().minusDays(random.nextInt(7));
            
            createNotificacionPush((long)(i + 1), cliente, titulo, mensaje, tipo, leida, fechaEnvio, pedido);
        }
    }

    private void printSummary() {
        System.out.println("");
        System.out.println("🎉 ¡SISTEMA COMPLETO CARGADO!");
        System.out.println("================================");
        System.out.println("👤 Usuarios: " + usuariosRepository.count());
        System.out.println("👥 Clientes: " + clientesRepository.count());
        System.out.println("👨‍⚕️ Médicos: " + medicosRepository.count());
        System.out.println("👩‍⚕️ Farmacéuticos: " + farmaceuticosRepository.count());
        System.out.println("🚚 Repartidores: " + repartidoresRepository.count());
        System.out.println("📦 Productos: " + productosRepository.count());
        System.out.println("🛒 Pedidos: " + pedidosRepository.count());
        System.out.println("🎯 Programas Fidelización: " + programaFidelizacionRepository.count());
        System.out.println("📋 Recetas Digitales: " + recetaDigitalRepository.count());
        System.out.println("🔔 Notificaciones: " + notificacionPushRepository.count());
        System.out.println("");
        System.out.println("🔑 CREDENCIALES PRINCIPALES:");
        System.out.println("👤 Admin: admin@test.com / password123");
        System.out.println("👤 Cliente: cliente001@test.com / password123 (DNI: 10000000)");
        System.out.println("👨‍⚕️ Médico: medico001@test.com / password123");
        System.out.println("👩‍⚕️ Farmacéutico: farmaceutico001@test.com / password123");
        System.out.println("🚚 Repartidor: repartidor001@test.com / password123");
        System.out.println("");
        System.out.println("🔌 WEBSOCKET LISTO:");
        System.out.println("📊 Pedido #123 en estado EN_CAMINO");
        System.out.println("📡 Suscripción: /topic/delivery/123");
        System.out.println("");
    }

    private Medicos createMedico(Long id, String nombres, String apellidos, String especialidad,
                               String cmp, String telefono, Usuarios usuario) {
        Medicos medico = new Medicos();
        medico.setIdMedicos(id);
        medico.setNombres(nombres);
        medico.setApellidos(apellidos);
        medico.setEspecialidad(especialidad);
        medico.setCmp(cmp);
        medico.setTelefono(telefono);
        medico.setCorreo(usuario.getCorreo());
        medico.setEstado("ACTIVO");
        return medicosRepository.save(medico);
    }

    private Farmaceuticos createFarmaceutico(Long id, String nombres, String apellidos, String cqf,
                                           String telefono, String turno, Usuarios usuario) {
        Farmaceuticos farmaceutico = new Farmaceuticos();
        farmaceutico.setIdFarmaceuticos(id);
        farmaceutico.setNombres(nombres);
        farmaceutico.setApellidos(apellidos);
        farmaceutico.setCqf(cqf);
        farmaceutico.setTelefono(telefono);
        farmaceutico.setEstado("ACTIVO");
        farmaceutico.setFarmaceuticos(usuario);
        return farmaceuticosRepository.save(farmaceutico);
    }

    private Repartidores createRepartidor(Long id, String nombres, String apellidos, String dni,
                                        String telefono, String placa, String tipoVehiculo, Usuarios usuario) {
        Repartidores repartidor = new Repartidores();
        repartidor.setIdRepartidores(id);
        repartidor.setNombres(nombres);
        repartidor.setApellidos(apellidos);
        repartidor.setDni(dni);
        repartidor.setTelefono(telefono);
        repartidor.setPlacaVehiculo(placa);
        repartidor.setVehiculo(tipoVehiculo);
        repartidor.setEstado("ACTIVO");
        repartidor.setFechaIngreso(new Date());
        repartidor.setRepartidores(usuario);
        return repartidoresRepository.save(repartidor);
    }

    private MovimientoPuntos createMovimientoPuntos(Long id, ProgramaFidelizacion programa, Integer puntos,
                                                  String tipo, String descripcion, Pedidos pedido) {
        MovimientoPuntos movimiento = new MovimientoPuntos();
        movimiento.setIdMovimientoPuntos(id);
        movimiento.setProgramaFidelizacion(programa);
        movimiento.setPuntos(puntos);
        movimiento.setTipo(tipo);
        movimiento.setDescripcion(descripcion);
        movimiento.setFecha(new Date());
        movimiento.setPedido(pedido);
        return movimientoPuntosRepository.save(movimiento);
    }

    private Cupon createCupon(Long id, String codigo, String descripcion, Integer puntosRequeridos,
                            Double descuentoPorcentaje, Double descuentoMonto, Integer usoMaximo, Integer usoActual) {
        Cupon cupon = new Cupon();
        cupon.setIdCupon(id);
        cupon.setCodigo(codigo);
        cupon.setDescripcion(descripcion);
        cupon.setPuntosRequeridos(puntosRequeridos);
        cupon.setDescuentoPorcentaje(descuentoPorcentaje);
        cupon.setDescuentoMonto(descuentoMonto);
        cupon.setUsoMaximo(usoMaximo);
        cupon.setUsoActual(usoActual);
        cupon.setActivo(true);
        cupon.setFechaInicio(new Date());
        return cuponRepository.save(cupon);
    }

    private RecetaDigital createRecetaDigital(Long id, String imagenUrl, String textoExtraido, String estado,
                                            LocalDateTime fechaCreacion, LocalDateTime fechaProcesamiento,
                                            Clientes cliente, Medicos medico) {
        RecetaDigital receta = new RecetaDigital();
        receta.setIdRecetaDigital(id);
        receta.setImagenUrl(imagenUrl);
        receta.setTextoExtraido(textoExtraido);
        receta.setEstado(estado);
        receta.setFechaCreacion(new Date());
        receta.setFechaProcesamiento(fechaProcesamiento != null ? new Date() : null);
        receta.setCliente(cliente);
        receta.setMedico(medico);
        return recetaDigitalRepository.save(receta);
    }

    private RecetaDigitalDetalle createRecetaDigitalDetalle(Long id, String medicamento, Integer cantidad,
                                                          String indicaciones, Boolean aprobado,
                                                          RecetaDigital receta, Productos producto) {
        RecetaDigitalDetalle detalle = new RecetaDigitalDetalle();
        detalle.setIdRecetaDigitalDetalle(id);
        detalle.setMedicamentoTexto(medicamento);
        detalle.setCantidad(cantidad);
        detalle.setDosificacion(indicaciones);
        detalle.setValidado(aprobado);
        detalle.setRecetaDigital(receta);
        detalle.setProducto(producto);
        return recetaDigitalDetalleRepository.save(detalle);
    }

    private DispositivoCliente createDispositivoCliente(Long id, Clientes cliente, String fcmToken,
                                                      String plataforma, Boolean activo) {
        DispositivoCliente dispositivo = new DispositivoCliente();
        dispositivo.setIdDispositivo(id);
        dispositivo.setCliente(cliente);
        dispositivo.setFcmToken(fcmToken);
        dispositivo.setPlataforma(plataforma);
        dispositivo.setActivo(activo);
        dispositivo.setFechaRegistro(new Date());
        dispositivo.setFechaUltimaActividad(new Date());
        return dispositivoClienteRepository.save(dispositivo);
    }

    private NotificacionPush createNotificacionPush(Long id, Clientes cliente, String titulo, String mensaje,
                                                  String tipo, Boolean leida, LocalDateTime fechaEnvio, Pedidos pedido) {
        NotificacionPush notificacion = new NotificacionPush();
        notificacion.setIdNotificacion(id);
        notificacion.setCliente(cliente);
        notificacion.setTitulo(titulo);
        notificacion.setMensaje(mensaje);
        notificacion.setTipo(tipo);
        notificacion.setLeida(leida);
        notificacion.setFechaEnvio(new Date());
        notificacion.setPedido(pedido);
        return notificacionPushRepository.save(notificacion);
    }
}

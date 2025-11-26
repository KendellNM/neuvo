package com.farm.dolores.farmacia.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import com.farm.dolores.farmacia.entity.Pedidos;
import com.farm.dolores.farmacia.entity.Repartidores;
import com.farm.dolores.farmacia.repository.PedidosRepository;
import com.farm.dolores.farmacia.repository.RepartidoresRepository;
import com.farm.dolores.farmacia.service.PedidosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.Date;
import java.util.stream.Collectors;
import com.farm.dolores.farmacia.dto.CrearPedidoMobileRequest;
import com.farm.dolores.farmacia.dto.CrearPedidoPresencialRequest;
import com.farm.dolores.farmacia.repository.ClientesRepository;
import com.farm.dolores.farmacia.repository.DireccionesRepository;
import com.farm.dolores.farmacia.repository.ProductosRepository;
import com.farm.dolores.farmacia.repository.PedidoDetalleRepository;
import com.farm.dolores.farmacia.entity.Clientes;
import com.farm.dolores.farmacia.entity.Direcciones;
import com.farm.dolores.farmacia.entity.Productos;
import com.farm.dolores.farmacia.entity.PedidoDetalle;

@RestController
@CrossOrigin("http://localhost:5173/")
@RequestMapping("api/pedidos")
@Tag(name = "🛒 Pedidos", description = "Gestión de pedidos y compras de la farmacia")
@SecurityRequirement(name = "bearer-jwt")
public class PedidosController {

    @Autowired
    private PedidosService pedidosService;
    
    @Autowired
    private PedidosRepository pedidosRepository;
    
    @Autowired
    private RepartidoresRepository repartidoresRepository;
    
    @Autowired
    private ClientesRepository clientesRepository;
    
    @Autowired
    private DireccionesRepository direccionesRepository;
    
    @Autowired
    private ProductosRepository productosRepository;
    
    @Autowired
    private PedidoDetalleRepository pedidoDetalleRepository;

    @GetMapping
    public ResponseEntity<List<Pedidos>> readAll() {
        try {
            List<Pedidos> pedidoss = pedidosService.readAll();

            if (pedidoss.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(pedidoss, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @PostMapping
    public ResponseEntity<Pedidos> create(@Valid @RequestBody Pedidos pedidos) {
        try {
            Pedidos pedidosCreated = pedidosService.create(pedidos);
            return new ResponseEntity<>(pedidosCreated, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Crear pedido desde app móvil", description = "Endpoint optimizado para crear pedidos desde la aplicación Android")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Pedido creado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos"),
        @ApiResponse(responseCode = "404", description = "Cliente o producto no encontrado")
    })
    @PostMapping("/mobile")
    public ResponseEntity<?> crearPedidoMobile(@RequestBody CrearPedidoMobileRequest request) {
        try {
            // Validar cliente
            Optional<Clientes> clienteOpt = clientesRepository.findById(request.getClienteId());
            if (clienteOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cliente no encontrado");
            }
            
            // Crear pedido
            Pedidos pedido = new Pedidos();
            pedido.setClientes(clienteOpt.get());
            pedido.setFechaPedido(new Date());
            pedido.setEstado("PENDIENTE");
            pedido.setMetodoPago(request.getMetodoPago() != null ? request.getMetodoPago() : "EFECTIVO");
            pedido.setObservaciones(request.getNotas());
            
            // Buscar o crear dirección
            if (request.getDireccionId() != null) {
                Optional<Direcciones> dirOpt = direccionesRepository.findById(request.getDireccionId());
                dirOpt.ifPresent(pedido::setDirecciones);
            } else if (request.getDireccionEntrega() != null) {
                // Crear nueva dirección temporal
                Direcciones nuevaDir = new Direcciones();
                nuevaDir.setDireccion(request.getDireccionEntrega());
                nuevaDir.setClientes(clienteOpt.get());
                if (request.getLatitud() != null) {
                    nuevaDir.setLatitud(String.valueOf(request.getLatitud()));
                }
                if (request.getLongitud() != null) {
                    nuevaDir.setLongitud(String.valueOf(request.getLongitud()));
                }
                nuevaDir.setEstado("ACTIVO");
                nuevaDir = direccionesRepository.save(nuevaDir);
                pedido.setDirecciones(nuevaDir);
            }
            
            // Calcular totales
            double subtotal = 0.0;
            if (request.getDetalles() != null) {
                for (CrearPedidoMobileRequest.PedidoDetalleRequest detalle : request.getDetalles()) {
                    subtotal += detalle.getCantidad() * detalle.getPrecioUnitario();
                }
            }
            
            pedido.setSubtotal(subtotal);
            pedido.setCostoDelivery(5.0); // Costo fijo de delivery
            pedido.setDescuento(0.0);
            pedido.setTotal(subtotal + 5.0);
            
            // Generar número de pedido
            Long count = pedidosRepository.count();
            pedido.setNumeroPedido((int) (count + 1));
            
            // Guardar pedido
            Pedidos pedidoGuardado = pedidosRepository.save(pedido);
            
            // Crear detalles del pedido
            if (request.getDetalles() != null) {
                for (CrearPedidoMobileRequest.PedidoDetalleRequest detalleReq : request.getDetalles()) {
                    Optional<Productos> productoOpt = productosRepository.findById(detalleReq.getProductoId());
                    if (productoOpt.isPresent()) {
                        PedidoDetalle detalle = new PedidoDetalle();
                        detalle.setProductos(productoOpt.get());
                        detalle.setCantidad(detalleReq.getCantidad());
                        detalle.setPrecioUnitario(detalleReq.getPrecioUnitario());
                        detalle.setSubtotal(detalleReq.getCantidad() * detalleReq.getPrecioUnitario());
                        pedidoDetalleRepository.save(detalle);
                        
                        // Actualizar stock
                        Productos producto = productoOpt.get();
                        if (producto.getStock() != null) {
                            producto.setStock(producto.getStock() - detalleReq.getCantidad());
                            productosRepository.save(producto);
                        }
                    }
                }
            }
            
            return ResponseEntity.status(HttpStatus.CREATED).body(pedidoGuardado);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error al crear pedido: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pedidos> getById(@PathVariable("id") Long id) {
        try {
            Pedidos pedidos = pedidosService.read(id).get();
            return new ResponseEntity<>(pedidos, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Pedidos> delete(@PathVariable("id") Long id) {
        try {
            pedidosService.delete(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable("id") Long id, @Valid @RequestBody Pedidos pedidos) {

        Optional<Pedidos> pedidosOptional = pedidosService.read(id);
        if (pedidosOptional.isPresent()) {
            return new ResponseEntity<>(pedidosService.update(pedidos), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // ==================== ENDPOINTS PARA REPARTIDORES ====================

    @Operation(summary = "Obtener pedidos asignados a un repartidor")
    @GetMapping("/repartidor/{repartidorId}")
    public ResponseEntity<?> getPedidosByRepartidor(@PathVariable Long repartidorId) {
        try {
            List<Pedidos> pedidos = pedidosRepository.findAll().stream()
                .filter(p -> p.getRepartidor() != null && p.getRepartidor().getIdRepartidores().equals(repartidorId))
                .collect(Collectors.toList());
            return ResponseEntity.ok(pedidos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al obtener pedidos");
        }
    }

    @Operation(summary = "Obtener pedidos listos para asignar (sin repartidor)")
    @GetMapping("/listos-para-asignar")
    public ResponseEntity<?> getPedidosListosParaAsignar() {
        try {
            List<Pedidos> pedidos = pedidosRepository.findAll().stream()
                .filter(p -> p.getRepartidor() == null && 
                            ("LISTO".equals(p.getEstado()) || "PREPARADO".equals(p.getEstado())))
                .collect(Collectors.toList());
            return ResponseEntity.ok(pedidos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al obtener pedidos");
        }
    }

    @Operation(summary = "Asignar pedido a un repartidor")
    @PutMapping("/{pedidoId}/asignar/{repartidorId}")
    public ResponseEntity<?> asignarPedido(
            @PathVariable Long pedidoId, 
            @PathVariable Long repartidorId) {
        try {
            Optional<Pedidos> pedidoOpt = pedidosRepository.findById(pedidoId);
            Optional<Repartidores> repartidorOpt = repartidoresRepository.findById(repartidorId);
            
            if (pedidoOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Pedido no encontrado");
            }
            if (repartidorOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Repartidor no encontrado");
            }
            
            Pedidos pedido = pedidoOpt.get();
            pedido.setRepartidor(repartidorOpt.get());
            pedido.setEstado("ASIGNADO");
            pedidosRepository.save(pedido);
            
            return ResponseEntity.ok(pedido);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al asignar pedido");
        }
    }

    @Operation(summary = "Cambiar estado del pedido")
    @PutMapping("/{pedidoId}/estado")
    public ResponseEntity<?> cambiarEstado(
            @PathVariable Long pedidoId,
            @RequestParam String nuevoEstado) {
        try {
            Optional<Pedidos> pedidoOpt = pedidosRepository.findById(pedidoId);
            
            if (pedidoOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Pedido no encontrado");
            }
            
            Pedidos pedido = pedidoOpt.get();
            pedido.setEstado(nuevoEstado);
            
            // Si el estado es ENTREGADO, registrar fecha de entrega
            if ("ENTREGADO".equals(nuevoEstado)) {
                pedido.setFechaEntregaReal(new java.util.Date());
            }
            
            pedidosRepository.save(pedido);
            return ResponseEntity.ok(pedido);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al cambiar estado");
        }
    }

    @Operation(summary = "Obtener pedidos por estado")
    @GetMapping("/estado/{estado}")
    public ResponseEntity<?> getPedidosByEstado(@PathVariable String estado) {
        try {
            List<Pedidos> pedidos = pedidosRepository.findAll().stream()
                .filter(p -> estado.equalsIgnoreCase(p.getEstado()))
                .collect(Collectors.toList());
            return ResponseEntity.ok(pedidos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al obtener pedidos");
        }
    }

    @Operation(summary = "Obtener pedidos de un cliente")
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<?> getPedidosByCliente(@PathVariable Long clienteId) {
        try {
            List<Pedidos> pedidos = pedidosRepository.findAll().stream()
                .filter(p -> p.getClientes() != null && p.getClientes().getIdClientes().equals(clienteId))
                .collect(Collectors.toList());
            return ResponseEntity.ok(pedidos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al obtener pedidos del cliente");
        }
    }

    // ==================== VENTA PRESENCIAL (SIN DELIVERY) ====================

    @Operation(summary = "Crear pedido presencial (venta en farmacia)", 
               description = "Endpoint para que el farmacéutico registre ventas presenciales escaneando productos. Sin delivery.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Venta registrada exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos"),
        @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    @PostMapping("/presencial")
    public ResponseEntity<?> crearPedidoPresencial(@RequestBody CrearPedidoPresencialRequest request) {
        try {
            // Crear pedido presencial
            Pedidos pedido = new Pedidos();
            pedido.setFechaPedido(new Date());
            pedido.setEstado("COMPLETADO"); // Venta presencial se completa inmediatamente
            pedido.setMetodoPago("EFECTIVO");
            pedido.setObservaciones(request.getObservaciones() != null ? request.getObservaciones() : "Venta presencial");
            pedido.setTipoVenta("PRESENCIAL"); // Marcar como venta presencial
            
            // Calcular totales
            double subtotal = 0.0;
            if (request.getDetalles() != null) {
                for (CrearPedidoPresencialRequest.DetallePedidoRequest detalle : request.getDetalles()) {
                    subtotal += detalle.getCantidad() * detalle.getPrecioUnitario();
                }
            }
            
            pedido.setSubtotal(subtotal);
            pedido.setCostoDelivery(0.0); // Sin costo de delivery
            pedido.setDescuento(0.0);
            pedido.setTotal(subtotal);
            
            // Generar número de pedido
            Long count = pedidosRepository.count();
            pedido.setNumeroPedido((int) (count + 1));
            
            // Guardar pedido
            Pedidos pedidoGuardado = pedidosRepository.save(pedido);
            
            // Crear detalles del pedido y actualizar stock
            if (request.getDetalles() != null) {
                for (CrearPedidoPresencialRequest.DetallePedidoRequest detalleReq : request.getDetalles()) {
                    Optional<Productos> productoOpt = productosRepository.findById(detalleReq.getProductoId());
                    if (productoOpt.isPresent()) {
                        PedidoDetalle detalle = new PedidoDetalle();
                        detalle.setProductos(productoOpt.get());
                        detalle.setCantidad(detalleReq.getCantidad());
                        detalle.setPrecioUnitario(detalleReq.getPrecioUnitario());
                        detalle.setSubtotal(detalleReq.getCantidad() * detalleReq.getPrecioUnitario());
                        pedidoDetalleRepository.save(detalle);
                        
                        // Actualizar stock
                        Productos producto = productoOpt.get();
                        if (producto.getStock() != null) {
                            producto.setStock(producto.getStock() - detalleReq.getCantidad());
                            productosRepository.save(producto);
                        }
                    }
                }
            }
            
            return ResponseEntity.status(HttpStatus.CREATED).body(pedidoGuardado);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error al registrar venta presencial: " + e.getMessage());
        }
    }
}

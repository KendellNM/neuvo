package com.farm.dolores.farmacia.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import com.farm.dolores.farmacia.dto.RecetaDigitalDto;
import com.farm.dolores.farmacia.dto.RecetaConUbicacionRequest;
import com.farm.dolores.farmacia.dto.ProcesarRecetaFarmaceuticoRequest;
import com.farm.dolores.farmacia.dto.PedidoDto;
import com.farm.dolores.farmacia.service.RecetaDigitalService;
import com.farm.dolores.farmacia.exception.ResourceNotFoundException;
import com.farm.dolores.farmacia.exception.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("api/recetas-digitales")
@Tag(name = "Recetas Digitales", description = "API para procesamiento de recetas médicas con OCR")
@SecurityRequirement(name = "bearer-jwt")
public class RecetaDigitalController {

    @Autowired
    private RecetaDigitalService recetaDigitalService;

    @PostMapping("/procesar")
    @PreAuthorize("hasAnyRole('CLIENTE', 'ADMIN', 'FARMACEUTICO')")
    @Operation(
        summary = "Procesar receta médica con OCR",
        description = "Sube una imagen de receta médica, extrae el texto con OCR y detecta medicamentos automáticamente"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Receta procesada exitosamente",
            content = @Content(schema = @Schema(implementation = RecetaDigitalDto.class))),
        @ApiResponse(responseCode = "400", description = "Imagen inválida o error en procesamiento"),
        @ApiResponse(responseCode = "401", description = "No autorizado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<com.farm.dolores.farmacia.dto.ApiResponse<RecetaDigitalDto>> procesarReceta(
            @Parameter(description = "Imagen de la receta médica (JPG, PNG)", required = true)
            @RequestParam("imagen") MultipartFile imagen,
            @Parameter(description = "ID del cliente", required = true)
            @RequestParam("clienteId") Long clienteId) {
        if (imagen.isEmpty()) {
            throw new BadRequestException("La imagen no puede estar vacía");
        }
        RecetaDigitalDto receta = recetaDigitalService.procesarReceta(imagen, clienteId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(com.farm.dolores.farmacia.dto.ApiResponse.success(receta, "Receta procesada exitosamente"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RecetaDigitalDto> obtenerReceta(@PathVariable Long id) {
        try {
            return recetaDigitalService.obtenerReceta(id)
                    .map(receta -> new ResponseEntity<>(receta, HttpStatus.OK))
                    .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/cliente/{clienteId}")
    @PreAuthorize("hasAnyRole('CLIENTE', 'ADMIN')")
    public ResponseEntity<List<RecetaDigitalDto>> obtenerRecetasPorCliente(@PathVariable Long clienteId) {
        try {
            List<RecetaDigitalDto> recetas = recetaDigitalService.obtenerRecetasPorCliente(clienteId);
            return new ResponseEntity<>(recetas, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}/validar")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RecetaDigitalDto> validarReceta(
            @PathVariable Long id,
            @RequestBody List<Long> productosIds) {
        try {
            RecetaDigitalDto receta = recetaDigitalService.validarReceta(id, productosIds);
            return new ResponseEntity<>(receta, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/procesar-farmaceutico")
    @PreAuthorize("hasRole('FARMACEUTICO')")
    @Operation(
        summary = "Procesar receta médica (Farmacéutico)",
        description = "Permite al farmacéutico procesar una receta médica con OCR sin necesidad de cliente asociado. Útil para ventas presenciales."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Receta procesada exitosamente",
            content = @Content(schema = @Schema(implementation = RecetaDigitalDto.class))),
        @ApiResponse(responseCode = "400", description = "Imagen inválida o error en procesamiento"),
        @ApiResponse(responseCode = "401", description = "No autorizado"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado - Solo farmacéuticos"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<com.farm.dolores.farmacia.dto.ApiResponse<RecetaDigitalDto>> procesarRecetaFarmaceutico(
            @Parameter(description = "Imagen de la receta médica (JPG, PNG)", required = true)
            @RequestParam("imagen") MultipartFile imagen,
            @Parameter(description = "Observaciones del farmacéutico", required = false)
            @RequestParam(value = "observaciones", required = false) String observaciones) {
        if (imagen.isEmpty()) {
            throw new BadRequestException("La imagen no puede estar vacía");
        }
        RecetaDigitalDto receta = recetaDigitalService.procesarRecetaFarmaceutico(imagen, observaciones);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(com.farm.dolores.farmacia.dto.ApiResponse.success(receta, "Receta procesada exitosamente por farmacéutico"));
    }

    // ==================== FLUJO CLIENTE -> FARMACÉUTICO -> DELIVERY ====================

    @PostMapping("/enviar-con-ubicacion")
    @PreAuthorize("hasRole('CLIENTE')")
    @Operation(
        summary = "Enviar receta con ubicación de entrega",
        description = "El cliente sube su receta médica junto con su ubicación para recibir los medicamentos en su domicilio"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Receta enviada exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos"),
        @ApiResponse(responseCode = "401", description = "No autorizado")
    })
    public ResponseEntity<com.farm.dolores.farmacia.dto.ApiResponse<RecetaDigitalDto>> enviarRecetaConUbicacion(
            @RequestParam("imagen") MultipartFile imagen,
            @RequestParam("direccionEntrega") String direccionEntrega,
            @RequestParam("latitud") Double latitud,
            @RequestParam("longitud") Double longitud,
            @RequestParam(value = "telefonoContacto", required = false) String telefonoContacto,
            @RequestParam(value = "observaciones", required = false) String observaciones) {
        
        if (imagen.isEmpty()) {
            throw new BadRequestException("La imagen de la receta es requerida");
        }
        if (direccionEntrega == null || direccionEntrega.trim().isEmpty()) {
            throw new BadRequestException("La dirección de entrega es requerida");
        }
        
        RecetaConUbicacionRequest request = new RecetaConUbicacionRequest();
        request.setDireccionEntrega(direccionEntrega);
        request.setLatitud(latitud);
        request.setLongitud(longitud);
        request.setTelefonoContacto(telefonoContacto);
        request.setObservaciones(observaciones);
        
        RecetaDigitalDto receta = recetaDigitalService.enviarRecetaConUbicacion(imagen, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(com.farm.dolores.farmacia.dto.ApiResponse.success(receta, 
                    "Receta enviada exitosamente. Un farmacéutico la revisará pronto."));
    }

    @GetMapping("/pendientes")
    @PreAuthorize("hasRole('FARMACEUTICO')")
    @Operation(
        summary = "Obtener recetas pendientes de procesar",
        description = "Lista todas las recetas enviadas por clientes que están pendientes de revisión"
    )
    public ResponseEntity<com.farm.dolores.farmacia.dto.ApiResponse<List<RecetaDigitalDto>>> obtenerRecetasPendientes() {
        List<RecetaDigitalDto> recetas = recetaDigitalService.obtenerRecetasPendientes();
        return ResponseEntity.ok(com.farm.dolores.farmacia.dto.ApiResponse.success(recetas, 
            "Recetas pendientes obtenidas"));
    }

    @PostMapping("/procesar-y-enviar")
    @PreAuthorize("hasRole('FARMACEUTICO')")
    @Operation(
        summary = "Procesar receta y crear pedido con delivery",
        description = "El farmacéutico revisa la receta, selecciona los productos disponibles y crea el pedido para enviar a la ubicación del cliente"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Pedido creado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos o productos sin stock"),
        @ApiResponse(responseCode = "404", description = "Receta no encontrada")
    })
    public ResponseEntity<com.farm.dolores.farmacia.dto.ApiResponse<PedidoDto>> procesarYEnviarReceta(
            @RequestBody ProcesarRecetaFarmaceuticoRequest request) {
        
        if (request.getRecetaId() == null) {
            throw new BadRequestException("El ID de la receta es requerido");
        }
        if (request.getProductos() == null || request.getProductos().isEmpty()) {
            throw new BadRequestException("Debe seleccionar al menos un producto");
        }
        
        PedidoDto pedido = recetaDigitalService.procesarRecetaYCrearPedido(request);
        return ResponseEntity.ok(com.farm.dolores.farmacia.dto.ApiResponse.success(pedido, 
            "Pedido creado exitosamente. Se enviará a la ubicación del cliente."));
    }

    @PutMapping("/{id}/rechazar")
    @PreAuthorize("hasRole('FARMACEUTICO')")
    @Operation(
        summary = "Rechazar receta",
        description = "El farmacéutico rechaza una receta indicando el motivo"
    )
    public ResponseEntity<com.farm.dolores.farmacia.dto.ApiResponse<RecetaDigitalDto>> rechazarReceta(
            @PathVariable Long id,
            @RequestParam("motivo") String motivo) {
        
        RecetaDigitalDto receta = recetaDigitalService.rechazarReceta(id, motivo);
        return ResponseEntity.ok(com.farm.dolores.farmacia.dto.ApiResponse.success(receta, 
            "Receta rechazada"));
    }
}

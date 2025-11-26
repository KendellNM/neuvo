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
import com.farm.dolores.farmacia.entity.Repartidores;
import com.farm.dolores.farmacia.service.RepartidoresService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin("http://localhost:5173/")
@RequestMapping("api/repartidores")
@Tag(name = "🚚 Repartidores", description = "Gestión de repartidores para delivery de pedidos")
@SecurityRequirement(name = "bearer-jwt")
public class RepartidoresController {

    @Autowired
    private RepartidoresService repartidoresService;

    @Operation(
            summary = "Listar todos los repartidores",
            description = "Obtiene la lista completa de repartidores registrados en el sistema"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de repartidores obtenida exitosamente"),
            @ApiResponse(responseCode = "204", description = "No hay repartidores registrados"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping
    public ResponseEntity<List<Repartidores>> readAll() {
        try {
            List<Repartidores> repartidoress = repartidoresService.readAll();

            if (repartidoress.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(repartidoress, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @Operation(
            summary = "Registrar nuevo repartidor",
            description = "Crea un nuevo repartidor en el sistema con sus datos personales y vehículo"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Repartidor creado exitosamente",
                    content = @Content(schema = @Schema(implementation = Repartidores.class))
            ),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping
    public ResponseEntity<Repartidores> create(
            @Parameter(description = "Datos del nuevo repartidor", required = true)
            @Valid @RequestBody Repartidores repartidores) {
        try {
            Repartidores repartidoresCreated = repartidoresService.create(repartidores);
            return new ResponseEntity<>(repartidoresCreated, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(
            summary = "Obtener repartidor por ID",
            description = "Busca y retorna un repartidor específico por su identificador único"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Repartidor encontrado",
                    content = @Content(schema = @Schema(implementation = Repartidores.class))
            ),
            @ApiResponse(responseCode = "404", description = "Repartidor no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Repartidores> getById(
            @Parameter(description = "ID del repartidor", required = true, example = "1")
            @PathVariable("id") Long id) {
        try {
            Repartidores repartidores = repartidoresService.read(id).get();
            return new ResponseEntity<>(repartidores, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(
            summary = "Eliminar repartidor",
            description = "Elimina un repartidor del sistema por su ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Repartidor eliminado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Repartidor no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Repartidores> delete(
            @Parameter(description = "ID del repartidor a eliminar", required = true, example = "1")
            @PathVariable("id") Long id) {
        try {
            repartidoresService.delete(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(
            summary = "Actualizar repartidor",
            description = "Actualiza los datos de un repartidor existente"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Repartidor actualizado exitosamente",
                    content = @Content(schema = @Schema(implementation = Repartidores.class))
            ),
            @ApiResponse(responseCode = "204", description = "Repartidor no encontrado"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PutMapping("/{id}")
    public ResponseEntity<?> update(
            @Parameter(description = "ID del repartidor a actualizar", required = true, example = "1")
            @PathVariable("id") Long id,
            @Parameter(description = "Datos actualizados del repartidor", required = true)
            @Valid @RequestBody Repartidores repartidores) {

        Optional<Repartidores> repartidoresOptional = repartidoresService.read(id);
        if (repartidoresOptional.isPresent()) {
            return new ResponseEntity<>(repartidoresService.update(repartidores), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}

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
}

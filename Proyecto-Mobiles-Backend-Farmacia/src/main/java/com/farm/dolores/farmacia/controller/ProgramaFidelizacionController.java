package com.farm.dolores.farmacia.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import com.farm.dolores.farmacia.dto.CanjearPuntosRequest;
import com.farm.dolores.farmacia.dto.ProgramaFidelizacionDto;
import com.farm.dolores.farmacia.entity.MovimientoPuntos;
import com.farm.dolores.farmacia.service.ProgramaFidelizacionService;
import com.farm.dolores.farmacia.exception.ResourceNotFoundException;
import com.farm.dolores.farmacia.exception.InsufficientPointsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("api/fidelizacion")
@Tag(name = "🎁 Fidelización", description = "Programa de puntos, niveles de membresía y cupones")
@SecurityRequirement(name = "bearer-jwt")
public class ProgramaFidelizacionController {

    @Autowired
    private ProgramaFidelizacionService fidelizacionService;

    @PostMapping("/crear")
    @PreAuthorize("hasAnyRole('CLIENTE', 'ADMIN')")
    public ResponseEntity<ProgramaFidelizacionDto> crearPrograma(@RequestParam Long clienteId) {
        try {
            ProgramaFidelizacionDto programa = fidelizacionService.crearPrograma(clienteId);
            return new ResponseEntity<>(programa, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/cliente/{clienteId}")
    @PreAuthorize("hasAnyRole('CLIENTE', 'ADMIN')")
    @Operation(
        summary = "Obtener programa de fidelización del cliente",
        description = "Retorna puntos actuales, nivel de membresía y puntos para siguiente nivel"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Programa encontrado",
            content = @Content(schema = @Schema(implementation = ProgramaFidelizacionDto.class))),
        @ApiResponse(responseCode = "404", description = "Programa no encontrado"),
        @ApiResponse(responseCode = "401", description = "No autorizado")
    })
    public ResponseEntity<ProgramaFidelizacionDto> obtenerPorCliente(
            @Parameter(description = "ID del cliente", required = true)
            @PathVariable Long clienteId) {
        ProgramaFidelizacionDto programa = fidelizacionService.obtenerPorCliente(clienteId)
                .orElseThrow(() -> new ResourceNotFoundException("Programa de fidelización", "clienteId", clienteId));
        return ResponseEntity.ok(programa);
    }

    @PostMapping("/canjear")
    @PreAuthorize("hasAnyRole('CLIENTE', 'ADMIN')")
    @Operation(
        summary = "Canjear puntos por cupón",
        description = "Permite al cliente canjear sus puntos acumulados por cupones de descuento"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Puntos canjeados exitosamente",
            content = @Content(schema = @Schema(implementation = ProgramaFidelizacionDto.class))),
        @ApiResponse(responseCode = "400", description = "Puntos insuficientes o cupón inválido"),
        @ApiResponse(responseCode = "401", description = "No autorizado")
    })
    public ResponseEntity<com.farm.dolores.farmacia.dto.ApiResponse<ProgramaFidelizacionDto>> canjearPuntos(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Datos para canjear puntos",
                required = true,
                content = @Content(schema = @Schema(implementation = CanjearPuntosRequest.class))
            )
            @RequestBody CanjearPuntosRequest request) {
        ProgramaFidelizacionDto programa = fidelizacionService.canjearPuntos(
                request.getClienteId(),
                request.getPuntos(),
                "Canje de cupón: " + request.getCodigoCupon()
        );
        return ResponseEntity.ok(
                com.farm.dolores.farmacia.dto.ApiResponse.success(programa, "Puntos canjeados exitosamente")
        );
    }

    @GetMapping("/historial/{clienteId}")
    @PreAuthorize("hasAnyRole('CLIENTE', 'ADMIN')")
    public ResponseEntity<List<MovimientoPuntos>> obtenerHistorial(@PathVariable Long clienteId) {
        try {
            List<MovimientoPuntos> historial = fidelizacionService.obtenerHistorialPuntos(clienteId);
            return new ResponseEntity<>(historial, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

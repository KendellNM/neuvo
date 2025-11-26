package com.farm.dolores.farmacia.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import com.farm.dolores.farmacia.dto.NotificacionPushDto;
import com.farm.dolores.farmacia.dto.RegistrarDispositivoRequest;
import com.farm.dolores.farmacia.service.NotificacionPushService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("api/notificaciones-push")
@Tag(name = "🔔 Notificaciones Push", description = "Notificaciones push con Firebase Cloud Messaging")
@SecurityRequirement(name = "bearer-jwt")
public class NotificacionPushController {

    @Autowired
    private NotificacionPushService notificacionService;

    @PostMapping("/registrar-dispositivo")
    @PreAuthorize("hasAnyRole('CLIENTE', 'ADMIN')")
    @Operation(
        summary = "Registrar dispositivo para notificaciones push",
        description = "Registra el token FCM del dispositivo móvil para recibir notificaciones push"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Dispositivo registrado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos"),
        @ApiResponse(responseCode = "401", description = "No autorizado")
    })
    public ResponseEntity<com.farm.dolores.farmacia.dto.ApiResponse<String>> registrarDispositivo(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Datos del dispositivo a registrar",
                required = true,
                content = @Content(schema = @Schema(implementation = RegistrarDispositivoRequest.class))
            )
            @RequestBody RegistrarDispositivoRequest request) {
        notificacionService.registrarDispositivo(request);
        return ResponseEntity.ok(
                com.farm.dolores.farmacia.dto.ApiResponse.success("OK", "Dispositivo registrado exitosamente")
        );
    }

    @GetMapping("/cliente/{clienteId}")
    @PreAuthorize("hasAnyRole('CLIENTE', 'ADMIN')")
    public ResponseEntity<List<NotificacionPushDto>> obtenerNotificaciones(@PathVariable Long clienteId) {
        try {
            List<NotificacionPushDto> notificaciones = notificacionService.obtenerNotificacionesCliente(clienteId);
            return new ResponseEntity<>(notificaciones, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}/marcar-leida")
    @PreAuthorize("hasAnyRole('CLIENTE', 'ADMIN')")
    public ResponseEntity<?> marcarComoLeida(@PathVariable Long id) {
        try {
            notificacionService.marcarComoLeida(id);
            return new ResponseEntity<>("Notificación marcada como leída", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/enviar-promocion")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> enviarPromocion(
            @RequestParam Long clienteId,
            @RequestParam String mensaje) {
        try {
            notificacionService.enviarNotificacionPromocion(clienteId, mensaje);
            return new ResponseEntity<>("Promoción enviada", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

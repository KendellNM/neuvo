package com.farm.dolores.farmacia.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import com.farm.dolores.farmacia.dto.DeliveryLocationDto;
import com.farm.dolores.farmacia.dto.DeliveryStatusUpdateDto;
import com.farm.dolores.farmacia.service.DeliveryTrackingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("*")
@RequestMapping("api/delivery")
@Tag(name = "🚚 Delivery REST", description = "API REST para actualización de delivery (alternativa a WebSocket)")
@SecurityRequirement(name = "bearer-jwt")
public class DeliveryRestController {

    @Autowired
    private DeliveryTrackingService deliveryTrackingService;

    @PostMapping("/location")
    public ResponseEntity<?> updateLocation(@RequestBody DeliveryLocationDto location) {
        try {
            deliveryTrackingService.sendLocationUpdate(
                location.getPedidoId(),
                location.getDeliveryId(),
                location.getLatitud(),
                location.getLongitud(),
                location.getEstado()
            );
            return new ResponseEntity<>("Ubicación actualizada", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/status")
    public ResponseEntity<?> updateStatus(@RequestBody DeliveryStatusUpdateDto status) {
        try {
            deliveryTrackingService.sendStatusUpdate(
                status.getPedidoId(),
                status.getEstado(),
                status.getMensaje(),
                status.getDeliveryNombre()
            );
            return new ResponseEntity<>("Estado actualizado", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

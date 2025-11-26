package com.farm.dolores.farmacia.controller;

import com.farm.dolores.farmacia.dto.DeliveryLocationDto;
import com.farm.dolores.farmacia.dto.DeliveryStatusUpdateDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Controller
public class DeliveryTrackingController {

    private static final Logger log = LoggerFactory.getLogger(DeliveryTrackingController.class);

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/delivery/location")
    public void updateLocation(@Payload DeliveryLocationDto location) {
        log.info("📍 Ubicación recibida - Pedido: {}, Lat: {}, Lng: {}", 
                location.getPedidoId(), location.getLatitud(), location.getLongitud());
        
        location.setTimestamp(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        
        String topic = "/topic/delivery/" + location.getPedidoId();
        log.info("📤 Enviando a topic: {}", topic);
        
        messagingTemplate.convertAndSend(topic, location);
        
        log.info("✅ Mensaje enviado exitosamente");
    }

    @MessageMapping("/delivery/status")
    public void updateStatus(@Payload DeliveryStatusUpdateDto status) {
        log.info("🔄 Estado recibido - Pedido: {}, Estado: {}", 
                status.getPedidoId(), status.getEstado());
        
        status.setTimestamp(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        
        String topic = "/topic/delivery/" + status.getPedidoId() + "/status";
        log.info("📤 Enviando a topic: {}", topic);
        
        messagingTemplate.convertAndSend(topic, status);
        
        log.info("✅ Estado enviado exitosamente");
    }
}

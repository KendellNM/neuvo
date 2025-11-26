package com.farm.dolores.farmacia.serviceImpl;

import com.farm.dolores.farmacia.dto.DeliveryLocationDto;
import com.farm.dolores.farmacia.dto.DeliveryStatusUpdateDto;
import com.farm.dolores.farmacia.service.DeliveryTrackingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class DeliveryTrackingServiceImpl implements DeliveryTrackingService {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Override
    public void sendLocationUpdate(Long pedidoId, Long deliveryId, Double latitud, Double longitud, String estado) {
        DeliveryLocationDto location = DeliveryLocationDto.builder()
                .pedidoId(pedidoId)
                .deliveryId(deliveryId)
                .latitud(latitud)
                .longitud(longitud)
                .estado(estado)
                .timestamp(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .build();
        
        messagingTemplate.convertAndSend("/topic/delivery/" + pedidoId, location);
    }

    @Override
    public void sendStatusUpdate(Long pedidoId, String estado, String mensaje, String deliveryNombre) {
        DeliveryStatusUpdateDto status = DeliveryStatusUpdateDto.builder()
                .pedidoId(pedidoId)
                .estado(estado)
                .mensaje(mensaje)
                .deliveryNombre(deliveryNombre)
                .timestamp(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .build();
        
        messagingTemplate.convertAndSend("/topic/delivery/" + pedidoId + "/status", status);
    }
}

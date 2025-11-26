package com.farm.dolores.farmacia.service;

import com.farm.dolores.farmacia.dto.DeliveryLocationDto;
import com.farm.dolores.farmacia.dto.DeliveryStatusUpdateDto;

public interface DeliveryTrackingService {
    void sendLocationUpdate(Long pedidoId, Long deliveryId, Double latitud, Double longitud, String estado);
    void sendStatusUpdate(Long pedidoId, String estado, String mensaje, String deliveryNombre);
}

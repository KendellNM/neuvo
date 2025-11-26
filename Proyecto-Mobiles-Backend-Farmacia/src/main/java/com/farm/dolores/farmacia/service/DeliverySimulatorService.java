package com.farm.dolores.farmacia.service;

import com.farm.dolores.farmacia.dto.DeliveryLocationDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class DeliverySimulatorService {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    private int currentLocationIndex = 0;
    private boolean simulationActive = true;

    // Ruta simulada del repartidor (5 puntos)
    private final List<DeliveryLocationDto> route = new ArrayList<>();

    public DeliverySimulatorService() {
        // Inicializar ruta de ejemplo
        route.add(createLocation(123L, -12.0464, -77.0428)); // Farmacia
        route.add(createLocation(123L, -12.0480, -77.0440)); // Saliendo
        route.add(createLocation(123L, -12.0500, -77.0450)); // En camino
        route.add(createLocation(123L, -12.0530, -77.0470)); // Cerca
        route.add(createLocation(123L, -12.0550, -77.0480)); // Llegando
    }

    // Ejecutar cada 5 segundos
    @Scheduled(fixedDelay = 5000, initialDelay = 10000)
    public void simulateDelivery() {
        if (!simulationActive || route.isEmpty()) {
            return;
        }

        // Obtener ubicación actual
        DeliveryLocationDto location = route.get(currentLocationIndex);
        location.setTimestamp(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

        // Enviar ubicación por WebSocket
        messagingTemplate.convertAndSend("/topic/delivery/" + location.getPedidoId(), location);

        System.out.println("📍 Ubicación enviada: Pedido " + location.getPedidoId() + 
                          " - Lat: " + location.getLatitud() + 
                          ", Lng: " + location.getLongitud());

        // Avanzar al siguiente punto
        currentLocationIndex++;

        // Si llegamos al final, reiniciar
        if (currentLocationIndex >= route.size()) {
            currentLocationIndex = 0;
            System.out.println("🔄 Ruta completada, reiniciando simulación...");
        }
    }

    private DeliveryLocationDto createLocation(Long pedidoId, double lat, double lng) {
        DeliveryLocationDto location = new DeliveryLocationDto();
        location.setPedidoId(pedidoId);
        location.setLatitud(lat);
        location.setLongitud(lng);
        return location;
    }

    public void stopSimulation() {
        this.simulationActive = false;
        System.out.println("⏸️ Simulación detenida");
    }

    public void startSimulation() {
        this.simulationActive = true;
        this.currentLocationIndex = 0;
        System.out.println("▶️ Simulación iniciada");
    }
}

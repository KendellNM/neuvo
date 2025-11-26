package com.farm.dolores.farmacia.service;

import com.farm.dolores.farmacia.dto.NotificacionPushDto;
import com.farm.dolores.farmacia.dto.RegistrarDispositivoRequest;

import java.util.List;

public interface NotificacionPushService {
    void registrarDispositivo(RegistrarDispositivoRequest request);
    void enviarNotificacion(Long clienteId, String titulo, String mensaje, String tipo, Long pedidoId);
    void enviarNotificacionPedidoListo(Long pedidoId);
    void enviarNotificacionPromocion(Long clienteId, String mensaje);
    List<NotificacionPushDto> obtenerNotificacionesCliente(Long clienteId);
    void marcarComoLeida(Long notificacionId);
}

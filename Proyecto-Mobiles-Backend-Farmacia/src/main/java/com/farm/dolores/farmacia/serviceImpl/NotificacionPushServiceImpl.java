package com.farm.dolores.farmacia.serviceImpl;

import com.farm.dolores.farmacia.dto.NotificacionPushDto;
import com.farm.dolores.farmacia.dto.RegistrarDispositivoRequest;
import com.farm.dolores.farmacia.entity.Clientes;
import com.farm.dolores.farmacia.entity.DispositivoCliente;
import com.farm.dolores.farmacia.entity.NotificacionPush;
import com.farm.dolores.farmacia.entity.Pedidos;
import com.farm.dolores.farmacia.repository.ClientesRepository;
import com.farm.dolores.farmacia.repository.DispositivoClienteRepository;
import com.farm.dolores.farmacia.repository.NotificacionPushRepository;
import com.farm.dolores.farmacia.repository.PedidosRepository;
import com.farm.dolores.farmacia.service.NotificacionPushService;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificacionPushServiceImpl implements NotificacionPushService {

    @Autowired
    private NotificacionPushRepository notificacionRepository;

    @Autowired
    private DispositivoClienteRepository dispositivoRepository;

    @Autowired
    private ClientesRepository clienteRepository;

    @Autowired
    private PedidosRepository pedidoRepository;

    @Override
    public void registrarDispositivo(RegistrarDispositivoRequest request) {
        Clientes cliente = clienteRepository.findById(request.getClienteId())
                .orElseThrow(() -> new com.farm.dolores.farmacia.exception.ResourceNotFoundException(
                        "Cliente", "id", request.getClienteId()));

        DispositivoCliente dispositivo = dispositivoRepository
                .findByFcmToken(request.getFcmToken())
                .orElse(new DispositivoCliente());

        dispositivo.setCliente(cliente);
        dispositivo.setFcmToken(request.getFcmToken());
        dispositivo.setPlataforma(request.getPlataforma());
        dispositivo.setActivo(true);
        dispositivo.setFechaRegistro(dispositivo.getFechaRegistro() != null ? 
                dispositivo.getFechaRegistro() : new Date());
        dispositivo.setFechaUltimaActividad(new Date());

        dispositivoRepository.save(dispositivo);
    }

    @Override
    public void enviarNotificacion(Long clienteId, String titulo, String mensaje, String tipo, Long pedidoId) {
        Clientes cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new com.farm.dolores.farmacia.exception.ResourceNotFoundException(
                        "Cliente", "id", clienteId));

        NotificacionPush notificacion = new NotificacionPush();
        notificacion.setCliente(cliente);
        notificacion.setTitulo(titulo);
        notificacion.setMensaje(mensaje);
        notificacion.setTipo(tipo);
        notificacion.setLeida(false);
        notificacion.setFechaEnvio(new Date());

        if (pedidoId != null) {
            pedidoRepository.findById(pedidoId).ifPresent(notificacion::setPedido);
        }

        notificacionRepository.save(notificacion);

        List<DispositivoCliente> dispositivos = dispositivoRepository
                .findByClienteIdClientesAndActivoTrue(clienteId);

        for (DispositivoCliente dispositivo : dispositivos) {
            try {
                enviarPushFirebase(dispositivo.getFcmToken(), titulo, mensaje);
            } catch (Exception e) {
                System.err.println("Error enviando push: " + e.getMessage());
            }
        }
    }

    @Override
    public void enviarNotificacionPedidoListo(Long pedidoId) {
        Pedidos pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));

        String titulo = "¡Tu pedido está listo!";
        String mensaje = "Tu pedido #" + pedidoId + " está listo para ser entregado";

        enviarNotificacion(pedido.getClientes().getIdClientes(), titulo, mensaje, "PEDIDO", pedidoId);
    }

    @Override
    public void enviarNotificacionPromocion(Long clienteId, String mensaje) {
        enviarNotificacion(clienteId, "¡Nueva Promoción!", mensaje, "PROMOCION", null);
    }

    @Override
    public List<NotificacionPushDto> obtenerNotificacionesCliente(Long clienteId) {
        return notificacionRepository.findByClienteIdClientesOrderByFechaEnvioDesc(clienteId)
                .stream()
                .map(this::convertirADto)
                .collect(Collectors.toList());
    }

    @Override
    public void marcarComoLeida(Long notificacionId) {
        NotificacionPush notificacion = notificacionRepository.findById(notificacionId)
                .orElseThrow(() -> new RuntimeException("Notificación no encontrada"));
        
        notificacion.setLeida(true);
        notificacionRepository.save(notificacion);
    }

    private void enviarPushFirebase(String token, String titulo, String mensaje) {
        try {
            Message message = Message.builder()
                    .setToken(token)
                    .setNotification(Notification.builder()
                            .setTitle(titulo)
                            .setBody(mensaje)
                            .build())
                    .build();

            FirebaseMessaging.getInstance().send(message);
        } catch (Exception e) {
            throw new RuntimeException("Error enviando notificación Firebase: " + e.getMessage());
        }
    }

    private NotificacionPushDto convertirADto(NotificacionPush notificacion) {
        return NotificacionPushDto.builder()
                .id(notificacion.getIdNotificacion())
                .titulo(notificacion.getTitulo())
                .mensaje(notificacion.getMensaje())
                .tipo(notificacion.getTipo())
                .leida(notificacion.getLeida())
                .fechaEnvio(notificacion.getFechaEnvio())
                .pedidoId(notificacion.getPedido() != null ? notificacion.getPedido().getIdPedidos() : null)
                .data(notificacion.getData())
                .build();
    }
}

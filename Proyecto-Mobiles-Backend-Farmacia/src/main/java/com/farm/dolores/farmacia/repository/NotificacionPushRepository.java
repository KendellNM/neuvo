package com.farm.dolores.farmacia.repository;

import com.farm.dolores.farmacia.entity.NotificacionPush;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificacionPushRepository extends JpaRepository<NotificacionPush, Long> {
    List<NotificacionPush> findByClienteIdClientesOrderByFechaEnvioDesc(Long clienteId);
    List<NotificacionPush> findByClienteIdClientesAndLeidaFalse(Long clienteId);
}

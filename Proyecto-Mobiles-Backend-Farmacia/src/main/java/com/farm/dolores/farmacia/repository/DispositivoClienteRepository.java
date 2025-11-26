package com.farm.dolores.farmacia.repository;

import com.farm.dolores.farmacia.entity.DispositivoCliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DispositivoClienteRepository extends JpaRepository<DispositivoCliente, Long> {
    List<DispositivoCliente> findByClienteIdClientesAndActivoTrue(Long clienteId);
    Optional<DispositivoCliente> findByFcmToken(String fcmToken);
}

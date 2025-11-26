package com.farm.dolores.farmacia.repository;

import com.farm.dolores.farmacia.entity.ProgramaFidelizacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProgramaFidelizacionRepository extends JpaRepository<ProgramaFidelizacion, Long> {
    Optional<ProgramaFidelizacion> findByClienteIdClientes(Long clienteId);
}

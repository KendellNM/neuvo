package com.farm.dolores.farmacia.repository;

import com.farm.dolores.farmacia.entity.RecetaDigital;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecetaDigitalRepository extends JpaRepository<RecetaDigital, Long> {
    List<RecetaDigital> findByClienteIdClientes(Long clienteId);
    List<RecetaDigital> findByEstado(String estado);
}

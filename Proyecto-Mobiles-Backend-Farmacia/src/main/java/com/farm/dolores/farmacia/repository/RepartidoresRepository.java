package com.farm.dolores.farmacia.repository;

import com.farm.dolores.farmacia.entity.Repartidores;
import com.farm.dolores.farmacia.entity.Usuarios;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RepartidoresRepository extends JpaRepository<Repartidores, Long> {
    // Buscar repartidor por usuario asociado
    Optional<Repartidores> findByRepartidores(Usuarios usuario);
}

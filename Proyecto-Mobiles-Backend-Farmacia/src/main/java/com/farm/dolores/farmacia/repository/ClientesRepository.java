package com.farm.dolores.farmacia.repository;

import com.farm.dolores.farmacia.entity.Clientes;
import com.farm.dolores.farmacia.entity.Usuarios;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClientesRepository extends JpaRepository<Clientes, Long> {
    Optional<Clientes> findByDni(String dni);
    
    // Buscar cliente por usuario asociado
    Optional<Clientes> findByClientes(Usuarios usuario);
}

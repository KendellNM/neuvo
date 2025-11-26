package com.farm.dolores.farmacia.repository;

import com.farm.dolores.farmacia.entity.Clientes;
import com.farm.dolores.farmacia.entity.Usuarios;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClientesRepository extends JpaRepository<Clientes, Long> {
    Optional<Clientes> findByDni(String dni);
    
    // Buscar cliente por usuario asociado
    // Nota: El atributo en la entidad se llama "Clientes" (con C mayúscula)
    @Query("SELECT c FROM Clientes c WHERE c.Clientes = :usuario")
    Optional<Clientes> findByUsuario(@Param("usuario") Usuarios usuario);
}

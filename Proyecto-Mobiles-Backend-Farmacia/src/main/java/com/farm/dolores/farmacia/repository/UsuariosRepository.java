package com.farm.dolores.farmacia.repository;

import com.farm.dolores.farmacia.entity.Usuarios;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuariosRepository extends JpaRepository<Usuarios, Long> {
    @EntityGraph(attributePaths = { "usuariorols", "usuariorols.roles" })
    Optional<Usuarios> findByUsuario(String usuario);

    @EntityGraph(attributePaths = { "usuariorols", "usuariorols.roles" })
    Optional<Usuarios> findByCorreo(String correo);
}
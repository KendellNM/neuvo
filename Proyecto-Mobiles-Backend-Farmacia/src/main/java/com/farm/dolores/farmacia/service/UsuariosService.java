package com.farm.dolores.farmacia.service;

import com.farm.dolores.farmacia.entity.Usuarios;

import java.util.List;
import java.util.Optional;

public interface UsuariosService {

    Usuarios create(Usuarios usuarios);

    Usuarios update(Usuarios usuarios);

    void delete(Long id);

    Optional<Usuarios> read(Long id);

    List<Usuarios> readAll();

    /**
     * Busca un usuario por su nombre de usuario
     */
    Optional<Usuarios> findByUsuario(String usuario);

     /**
      * Busca un usuario por su correo electrónico
      */
     Optional<Usuarios> findByCorreo(String correo);
}

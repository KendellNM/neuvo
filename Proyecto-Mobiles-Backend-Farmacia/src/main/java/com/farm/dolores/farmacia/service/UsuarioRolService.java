package com.farm.dolores.farmacia.service;

import com.farm.dolores.farmacia.entity.UsuarioRol;

import java.util.List;
import java.util.Optional;

public interface UsuarioRolService {

    UsuarioRol create(UsuarioRol usuariorol);

    UsuarioRol update(UsuarioRol usuariorol);

    void delete(Long id);

    Optional<UsuarioRol> read(Long id);

    List<UsuarioRol> readAll();
}

package com.farm.dolores.farmacia.daoImpl;

import com.farm.dolores.farmacia.dao.UsuariosDao;
import com.farm.dolores.farmacia.entity.Usuarios;
import com.farm.dolores.farmacia.repository.UsuariosRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class UsuariosDaoImpl implements UsuariosDao {

    @Autowired
    private UsuariosRepository usuariosRepository;

    @Override
    public Usuarios create(Usuarios usuarios) {
        return usuariosRepository.save(usuarios);
    }

    @Override
    public Usuarios update(Usuarios usuarios) {
        return usuariosRepository.save(usuarios);
    }

    @Override
    public void delete(Long id) {
        usuariosRepository.deleteById(id);
    }

    @Override
    public Optional<Usuarios> read(Long id) {
        return usuariosRepository.findById(id);
    }

    @Override
    public List<Usuarios> readAll() {
        return usuariosRepository.findAll();
    }

    @Override
    public Optional<Usuarios> findByUsuario(String usuario) {
        return usuariosRepository.findByUsuario(usuario);
    }

    @Override
    public Optional<Usuarios> findByCorreo(String correo) {
        return usuariosRepository.findByCorreo(correo);
    }
}

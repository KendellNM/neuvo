package com.farm.dolores.farmacia.serviceImpl;

import com.farm.dolores.farmacia.dao.UsuariosDao;
import com.farm.dolores.farmacia.entity.Usuarios;
import com.farm.dolores.farmacia.service.UsuariosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuariosServiceImpl implements UsuariosService {

    @Autowired
    private UsuariosDao usuariosDao;

    @Override
    public Usuarios create(Usuarios usuarios) {
        return usuariosDao.create(usuarios);
    }

    @Override
    public Usuarios update(Usuarios usuarios) {
        return usuariosDao.update(usuarios);
    }

    @Override
    public void delete(Long id) {
        usuariosDao.delete(id);
    }

    @Override
    public Optional<Usuarios> read(Long id) {
        return usuariosDao.read(id);
    }

    @Override
    public List<Usuarios> readAll() {
        return usuariosDao.readAll();
    }

    @Override
    public Optional<Usuarios> findByUsuario(String usuario) {
        return usuariosDao.findByUsuario(usuario);
    }

    @Override
    public Optional<Usuarios> findByCorreo(String correo) {
        return usuariosDao.findByCorreo(correo);
    }
}

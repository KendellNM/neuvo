package com.farm.dolores.farmacia.serviceImpl;

import com.farm.dolores.farmacia.dao.UsuarioRolDao;
import com.farm.dolores.farmacia.entity.UsuarioRol;
import com.farm.dolores.farmacia.service.UsuarioRolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioRolServiceImpl implements UsuarioRolService {

    @Autowired
    private UsuarioRolDao usuariorolDao;

    @Override
    public UsuarioRol create(UsuarioRol usuariorol) {
        return usuariorolDao.create(usuariorol);
    }

    @Override
    public UsuarioRol update(UsuarioRol usuariorol) {
        return usuariorolDao.update(usuariorol);
    }

    @Override
    public void delete(Long id) {
        usuariorolDao.delete(id);
    }

    @Override
    public Optional<UsuarioRol> read(Long id) {
        return usuariorolDao.read(id);
    }

    @Override
    public List<UsuarioRol> readAll() {
        return usuariorolDao.readAll();
    }
}

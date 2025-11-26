package com.farm.dolores.farmacia.daoImpl;

import com.farm.dolores.farmacia.dao.UsuarioRolDao;
import com.farm.dolores.farmacia.entity.UsuarioRol;
import com.farm.dolores.farmacia.repository.UsuarioRolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class UsuarioRolDaoImpl implements UsuarioRolDao {

    @Autowired
    private UsuarioRolRepository usuariorolRepository;

    @Override
    public UsuarioRol create(UsuarioRol usuariorol) {
        return usuariorolRepository.save(usuariorol);
    }

    @Override
    public UsuarioRol update(UsuarioRol usuariorol) {
        return usuariorolRepository.save(usuariorol);
    }

    @Override
    public void delete(Long id) {
        usuariorolRepository.deleteById(id);
    }

    @Override
    public Optional<UsuarioRol> read(Long id) {
        return usuariorolRepository.findById(id);
    }

    @Override
    public List<UsuarioRol> readAll() {
        return usuariorolRepository.findAll();
    }
}

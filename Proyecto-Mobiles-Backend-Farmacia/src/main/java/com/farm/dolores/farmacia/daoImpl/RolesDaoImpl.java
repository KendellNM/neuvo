package com.farm.dolores.farmacia.daoImpl;

import com.farm.dolores.farmacia.dao.RolesDao;
import com.farm.dolores.farmacia.entity.Roles;
import com.farm.dolores.farmacia.repository.RolesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class RolesDaoImpl implements RolesDao {

    @Autowired
    private RolesRepository rolesRepository;

    @Override
    public Roles create(Roles roles) {
        return rolesRepository.save(roles);
    }

    @Override
    public Roles update(Roles roles) {
        return rolesRepository.save(roles);
    }

    @Override
    public void delete(Long id) {
        rolesRepository.deleteById(id);
    }

    @Override
    public Optional<Roles> read(Long id) {
        return rolesRepository.findById(id);
    }

    @Override
    public List<Roles> readAll() {
        return rolesRepository.findAll();
    }
}

package com.farm.dolores.farmacia.serviceImpl;

import com.farm.dolores.farmacia.dao.RolesDao;
import com.farm.dolores.farmacia.entity.Roles;
import com.farm.dolores.farmacia.service.RolesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RolesServiceImpl implements RolesService {

    @Autowired
    private RolesDao rolesDao;

    @Override
    public Roles create(Roles roles) {
        return rolesDao.create(roles);
    }

    @Override
    public Roles update(Roles roles) {
        return rolesDao.update(roles);
    }

    @Override
    public void delete(Long id) {
        rolesDao.delete(id);
    }

    @Override
    public Optional<Roles> read(Long id) {
        return rolesDao.read(id);
    }

    @Override
    public List<Roles> readAll() {
        return rolesDao.readAll();
    }
}

package com.farm.dolores.farmacia.dao;

import com.farm.dolores.farmacia.entity.Roles;

import java.util.List;
import java.util.Optional;

public interface RolesDao {

    Roles create(Roles roles);

    Roles update(Roles roles);

    void delete(Long id);

    Optional<Roles> read(Long id);

    List<Roles> readAll();
}

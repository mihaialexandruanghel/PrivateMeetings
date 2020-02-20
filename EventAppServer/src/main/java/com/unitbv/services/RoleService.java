package com.unitbv.services;

import com.unitbv.entity.RoleEntity;

import java.util.List;

public interface RoleService {

    void persist(RoleEntity entity);

    void update(RoleEntity entity);

    RoleEntity findById(int id);

    void delete(RoleEntity entity);

    List<RoleEntity> findAll();

    void deleteAll();
}

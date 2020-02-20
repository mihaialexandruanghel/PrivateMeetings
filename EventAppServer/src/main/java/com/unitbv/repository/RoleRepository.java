package com.unitbv.repository;

import com.unitbv.entity.RoleEntity;

import java.util.List;

public interface RoleRepository {

    void persist(RoleEntity entity);

    void update(RoleEntity entity);

    RoleEntity findById(int id);

    void delete(RoleEntity entity);

    List<RoleEntity> findAll();

    void deleteAll();
}

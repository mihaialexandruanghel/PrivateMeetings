package com.unitbv.services.impl;

import com.unitbv.entity.RoleEntity;
import com.unitbv.repository.impl.RoleRepositoryImpl;
import com.unitbv.services.RoleService;

import java.util.List;

public class RoleServiceImpl implements RoleService {
    private RoleRepositoryImpl roleRepository;

    public RoleServiceImpl() {
        roleRepository = new RoleRepositoryImpl();
    }

    @Override
    public void persist(RoleEntity entity) {
        roleRepository.persist(entity);
        roleRepository.close();
    }

    @Override
    public void update(RoleEntity entity) {
        roleRepository.update(entity);
        roleRepository.close();
    }

    @Override
    public RoleEntity findById(int id) {
        return roleRepository.findById(id);
    }

    @Override
    public void delete(RoleEntity entity) {
        roleRepository.delete(entity);
        roleRepository.close();
    }

    @Override
    public List<RoleEntity> findAll() {
        return roleRepository.findAll();
    }

    @Override
    public void deleteAll() {
        roleRepository.deleteAll();
        roleRepository.close();
    }

}

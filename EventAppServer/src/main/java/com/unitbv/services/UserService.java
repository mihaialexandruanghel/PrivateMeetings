package com.unitbv.services;

import com.unitbv.entity.UserEntity;

import java.util.List;

public interface UserService {

    void persist(UserEntity entity);

    void update(UserEntity entity);

    UserEntity findById(int id);

    UserEntity findByUserName(String userName);

    void delete(UserEntity entity);

    List<UserEntity> findAll();

    void deleteAll();
}

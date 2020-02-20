package com.unitbv.services.impl;

import com.unitbv.entity.UserEntity;
import com.unitbv.repository.impl.UserRepositoryImpl;
import com.unitbv.services.UserService;

import java.util.List;

public class UserServiceImpl implements UserService {
    private UserRepositoryImpl userRepository;

    public UserServiceImpl() {
        userRepository = new UserRepositoryImpl();
    }

    @Override
    public void persist(UserEntity entity) {
        userRepository.persist(entity);
        userRepository.close();
    }

    @Override
    public void update(UserEntity entity) {
        userRepository.update(entity);
        userRepository.close();
    }

    @Override
    public UserEntity findById(int id) {
        return userRepository.findById(id);
    }

    @Override
    public UserEntity findByUserName(String userName) {
        try {
            UserEntity user = userRepository.findByUserName(userName);
            return user;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void delete(UserEntity entity) {
        userRepository.delete(entity);
        userRepository.close();
    }

    @Override
    public List<UserEntity> findAll() {
        return userRepository.findAll();
    }

    @Override
    public void deleteAll() {
        userRepository.deleteAll();
        userRepository.close();
    }

}

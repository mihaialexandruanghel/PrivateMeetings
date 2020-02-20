package com.unitbv.services.impl;

import com.unitbv.entity.NotificationEntity;
import com.unitbv.entity.UserEntity;
import com.unitbv.repository.impl.NotificationRepositoryImpl;
import com.unitbv.services.NotificationService;

import java.util.List;

public class NotificationServiceImpl implements NotificationService {
    private NotificationRepositoryImpl notificationRepository;

    public NotificationServiceImpl() {
        notificationRepository = new NotificationRepositoryImpl();
    }

    @Override
    public void persist(NotificationEntity entity) {
        notificationRepository.persist(entity);
        notificationRepository.close();
    }

    @Override
    public void update(NotificationEntity entity) {
        notificationRepository.update(entity);
        notificationRepository.close();
    }

    @Override
    public NotificationEntity findById(int id) {
        return notificationRepository.findById(id);
    }

    @Override
    public void delete(NotificationEntity entity) {
        notificationRepository.delete(entity);
        notificationRepository.close();
    }

    @Override
    public List<NotificationEntity> findAll() {
        return notificationRepository.findAll();
    }

    @Override
    public List<NotificationEntity> findAllByUser(UserEntity userId) {
        return notificationRepository.findAllByUser(userId);
    }


    @Override
    public void deleteAll() {
        notificationRepository.deleteAll();
        notificationRepository.close();
    }

}

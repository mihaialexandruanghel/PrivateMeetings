package com.unitbv.repository;

import com.unitbv.entity.EventEntity;
import com.unitbv.entity.NotificationEntity;
import com.unitbv.entity.UserEntity;

import java.util.List;

public interface NotificationRepository {

    void persist(NotificationEntity entity);

    void update(NotificationEntity entity);

    NotificationEntity findById(int id);

    void delete(NotificationEntity entity);

    List<NotificationEntity> findAll();

    List<NotificationEntity> findAllByUser(UserEntity userId);

    void deleteAll();
}

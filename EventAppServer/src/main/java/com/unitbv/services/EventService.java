package com.unitbv.services;

import com.unitbv.entity.EventEntity;
import com.unitbv.entity.UserEntity;

import java.util.List;

public interface EventService {

    void persist(EventEntity entity);

    void update(EventEntity entity);

    EventEntity findById(int id);

    void delete(EventEntity entity);

    List<EventEntity> findAll();

    EventEntity findEventByName(String eventName);

    List<EventEntity> findAllByUser(UserEntity userEntity);

    void deleteAll();
}

package com.unitbv.services.impl;

import com.unitbv.entity.EventEntity;
import com.unitbv.entity.UserEntity;
import com.unitbv.repository.impl.EventRepositoryImpl;
import com.unitbv.services.EventService;

import java.util.List;

public class EventServiceImpl implements EventService {
    private EventRepositoryImpl eventRepository;

    public EventServiceImpl() {
        eventRepository = new EventRepositoryImpl();
    }

    @Override
    public void persist(EventEntity entity) {
        eventRepository.persist(entity);
        eventRepository.close();
    }

    @Override
    public void update(EventEntity entity) {
        eventRepository.update(entity);
        eventRepository.close();
    }

    @Override
    public EventEntity findById(int id) {
        return eventRepository.findById(id);
    }

    @Override
    public void delete(EventEntity entity) {
        eventRepository.delete(entity);
        eventRepository.close();
    }

    @Override
    public List<EventEntity> findAll() {
        return eventRepository.findAll();
    }

    @Override
    public EventEntity findEventByName(String eventName) {
        return eventRepository.findEventByName(eventName);
    }

    @Override
    public List<EventEntity> findAllByUser(UserEntity userEntity) {
        return eventRepository.findAllByUser(userEntity);
    }

    @Override
    public void deleteAll() {
        eventRepository.deleteAll();
        eventRepository.close();
    }

}

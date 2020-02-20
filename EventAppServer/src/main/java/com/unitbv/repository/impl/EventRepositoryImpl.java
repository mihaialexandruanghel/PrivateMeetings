package com.unitbv.repository.impl;

import com.unitbv.entity.EventEntity;
import com.unitbv.entity.UserEntity;
import com.unitbv.repository.EventRepository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class EventRepositoryImpl implements EventRepository {

    private EntityManager em;
    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("persistence");

    public EventRepositoryImpl() {
        this.em = this.emf.createEntityManager();
    }

    @Override
    public void persist(EventEntity entity) {
        emf = Persistence.createEntityManagerFactory("persistence");
        em = emf.createEntityManager();
        em.getTransaction().begin();
        em.persist(entity);
        em.getTransaction().commit();
    }

    @Override
    public void update(EventEntity entity) {
        emf = Persistence.createEntityManagerFactory("persistence");
        em = emf.createEntityManager();
        em.getTransaction().begin();
        em.merge(entity);
        em.getTransaction().commit();
    }

    @Override
    public EventEntity findById(int id) {
        return em.find(EventEntity.class, id);
    }

    @Override
    public void delete(EventEntity entity) {
        em.getTransaction().begin();
        em.remove(entity);
        em.getTransaction().commit();
    }

    @Override
    public List<EventEntity> findAll() {
        emf = Persistence.createEntityManagerFactory("persistence");
        em = emf.createEntityManager();
        Query query = em.createQuery("Select e From EventEntity e");

        if (!query.getResultList().isEmpty()) {
            return query.getResultList();
        }
        return Collections.<EventEntity> emptyList();
    }

    @Override
    public List<EventEntity> findAllByUser(UserEntity userEntity) {
        emf = Persistence.createEntityManagerFactory("persistence");
        em = emf.createEntityManager();
        Query query = em.createQuery("Select e From EventEntity e WHERE :user MEMBER OF e.users");
        query.setParameter("user", userEntity);
        if (!query.getResultList().isEmpty()) {
            return query.getResultList();
        }
        return Collections.<EventEntity> emptyList();
    }

    @Override
    public EventEntity findEventByName(String eventName){
        emf = Persistence.createEntityManagerFactory("persistence");
        em = emf.createEntityManager();
        Query query = em.createQuery("Select e From EventEntity e WHERE e.eventName = :eventName");
        query.setParameter("eventName", eventName);
        if (Objects.nonNull(query.getSingleResult())) {
            return (EventEntity) query.getSingleResult();
        }
        return null;
    }

    @Override
    public void deleteAll() {
        em.getTransaction().begin();
        em.createQuery("DELETE FROM EventEntity").executeUpdate();
        em.getTransaction().commit();
    }

    public void close() {
        emf.close();
    }
}

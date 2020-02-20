package com.unitbv.repository.impl;

import com.unitbv.entity.EventEntity;
import com.unitbv.entity.NotificationEntity;
import com.unitbv.entity.UserEntity;
import com.unitbv.repository.NotificationRepository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class NotificationRepositoryImpl implements NotificationRepository {

    private EntityManager em;
    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("persistence");

    public NotificationRepositoryImpl() {
        this.em = this.emf.createEntityManager();
    }

    @Override
    public void persist(NotificationEntity entity) {
        emf = Persistence.createEntityManagerFactory("persistence");
        em = emf.createEntityManager();
        em.getTransaction().begin();
        em.persist(entity);
        em.getTransaction().commit();
    }

    @Override
    public void update(NotificationEntity entity) {
        em.getTransaction().begin();
        em.merge(entity);
        em.getTransaction().commit();
    }

    @Override
    public NotificationEntity findById(int id) {
        return em.find(NotificationEntity.class, id);
    }

    @Override
    public void delete(NotificationEntity entity) {
        em.getTransaction().begin();
        em.remove(entity);
        em.getTransaction().commit();
    }

    @Override
    public List<NotificationEntity> findAll() {
        List<NotificationEntity> notifications = em.createQuery("FROM NotificationEntity", NotificationEntity.class).getResultList();
        return notifications;
    }

    @Override
    public List<NotificationEntity> findAllByUser(UserEntity userId) {
        emf = Persistence.createEntityManagerFactory("persistence");
        em = emf.createEntityManager();
        try {
            Query query = em.createQuery("Select n From NotificationEntity n where n.user = :userId");
            query.setParameter("userId", userId);

            if (!query.getResultList().isEmpty()) {
                return query.getResultList();
            }
            return Collections.<NotificationEntity> emptyList();
        } catch (
                Exception ex) {
            ex.printStackTrace();
            return Collections.<NotificationEntity>emptyList();
        } finally {
            close();
        }
    }

    @Override
    public void deleteAll() {
        em.getTransaction().begin();
        em.createQuery("DELETE FROM NotificationEntity").executeUpdate();
        em.getTransaction().commit();
    }

    public void close() {
        emf.close();
    }
}

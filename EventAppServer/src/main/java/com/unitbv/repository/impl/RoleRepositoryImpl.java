package com.unitbv.repository.impl;

import com.unitbv.entity.NotificationEntity;
import com.unitbv.entity.RoleEntity;
import com.unitbv.repository.NotificationRepository;
import com.unitbv.repository.RoleRepository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.List;

public class RoleRepositoryImpl implements RoleRepository {

    private EntityManager em;
    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("persistence");

    public RoleRepositoryImpl() {
        this.em = this.emf.createEntityManager();
    }

    @Override
    public void persist(RoleEntity entity) {
        em.getTransaction().begin();
        em.persist(entity);
        em.getTransaction().commit();
    }

    @Override
    public void update(RoleEntity entity) {
        em.getTransaction().begin();
        em.merge(entity);
        em.getTransaction().commit();
    }

    @Override
    public RoleEntity findById(int id) {
        return em.find(RoleEntity.class, id);
    }

    @Override
    public void delete(RoleEntity entity) {
        em.getTransaction().begin();
        em.remove(entity);
        em.getTransaction().commit();
    }

    @Override
    public List<RoleEntity> findAll() {
        List<RoleEntity> roles = em.createQuery("FROM RoleEntity", RoleEntity.class).getResultList();
        return roles;
    }

    @Override
    public void deleteAll() {
        em.getTransaction().begin();
        em.createQuery("DELETE FROM RoleEntity").executeUpdate();
        em.getTransaction().commit();
    }

    public void close() {
        emf.close();
    }
}

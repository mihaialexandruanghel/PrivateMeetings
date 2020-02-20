package com.unitbv.repository.impl;

import com.unitbv.entity.InvitationEntity;
import com.unitbv.entity.NotificationEntity;
import com.unitbv.entity.UserEntity;
import com.unitbv.repository.InvitationRepository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import java.util.Collections;
import java.util.List;

public class InvitationRepositoryImpl implements InvitationRepository {

    private EntityManager em;
    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("persistence");

    public InvitationRepositoryImpl() {
        this.em = this.emf.createEntityManager();
    }

    @Override
    public void persist(InvitationEntity entity) {
        emf = Persistence.createEntityManagerFactory("persistence");
        em = emf.createEntityManager();
        em.getTransaction().begin();
        em.persist(entity);
        em.getTransaction().commit();
    }

    @Override
    public void update(InvitationEntity entity) {
        emf = Persistence.createEntityManagerFactory("persistence");
        em = emf.createEntityManager();
        em.getTransaction().begin();
        em.merge(entity);
        em.getTransaction().commit();
    }

    @Override
    public InvitationEntity findById(int id) {
        return em.find(InvitationEntity.class, id);
    }

    @Override
    public void delete(InvitationEntity entity) {
        em.getTransaction().begin();
        em.remove(entity);
        em.getTransaction().commit();
    }

    @Override
    public List<InvitationEntity> findAllByUser(UserEntity userId) {
        emf = Persistence.createEntityManagerFactory("persistence");
        em = emf.createEntityManager();
        try {
            Query query = em.createQuery("Select i From InvitationEntity i where i.user = :userId");
            query.setParameter("userId", userId);

            if (!query.getResultList().isEmpty()) {
                return query.getResultList();
            }
            return Collections.<InvitationEntity> emptyList();
        } catch (
                Exception ex) {
            ex.printStackTrace();
            return Collections.<InvitationEntity>emptyList();
        } finally {
            close();
        }
    }

    @Override
    public List<InvitationEntity> findAll() {
        List<InvitationEntity> invitations = em.createQuery("FROM InvitationEntity", InvitationEntity.class).getResultList();
        return invitations;
    }

    @Override
    public void deleteAll() {
        em.getTransaction().begin();
        em.createQuery("DELETE FROM InvitationEntity").executeUpdate();
        em.getTransaction().commit();
    }

    public void close() {
        emf.close();
    }
}

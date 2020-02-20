package com.unitbv.repository.impl;

import com.unitbv.entity.UserEntity;
import com.unitbv.repository.UserRepository;
import org.eclipse.persistence.expressions.ExpressionBuilder;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import java.util.List;
import java.util.Objects;

public class UserRepositoryImpl implements UserRepository {

    private EntityManager em;
    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("persistence");

    public UserRepositoryImpl() {
        this.em = this.emf.createEntityManager();
    }

    @Override
    public void persist(UserEntity entity) {
        emf = Persistence.createEntityManagerFactory("persistence");
        em = emf.createEntityManager();
        em.getTransaction().begin();
        em.persist(entity);
        em.getTransaction().commit();
    }

    @Override
    public void update(UserEntity entity) {
        emf = Persistence.createEntityManagerFactory("persistence");
        em = emf.createEntityManager();
        em.getTransaction().begin();
        em.merge(entity);
        em.getTransaction().commit();
    }

    @Override
    public UserEntity findById(int id) {
        return em.find(UserEntity.class, id);
    }

    @Override
    public UserEntity findByUserName(String userName) {
        emf = Persistence.createEntityManagerFactory("persistence");
        em = emf.createEntityManager();
        try {
            Query query = em.createQuery("Select u From UserEntity u Where u.userName = :username");
            query.setParameter("username", userName);
            if (Objects.nonNull(query.getSingleResult())) {
                return (UserEntity) query.getSingleResult();
            }
            return null;
        } catch (
                Exception ex) {
            ex.printStackTrace();
            return null;
        }finally {
            close();
        }
    }

    @Override
    public void delete(UserEntity entity) {
        em.getTransaction().begin();
        em.remove(entity);
        em.getTransaction().commit();
    }

    @Override
    public List<UserEntity> findAll() {
        emf = Persistence.createEntityManagerFactory("persistence");
        em = emf.createEntityManager();
        List<UserEntity> users = em.createQuery("SELECT u FROM UserEntity u", UserEntity.class).getResultList();
        return users;
    }

    @Override
    public void deleteAll() {
        em.getTransaction().begin();
        em.createQuery("DELETE FROM UserEntity").executeUpdate();
        em.getTransaction().commit();
    }

    public void close() {
        emf.close();
    }
}

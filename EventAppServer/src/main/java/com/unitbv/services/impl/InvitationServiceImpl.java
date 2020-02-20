package com.unitbv.services.impl;

import com.unitbv.entity.InvitationEntity;
import com.unitbv.entity.UserEntity;
import com.unitbv.repository.impl.InvitationRepositoryImpl;
import com.unitbv.services.InvitationService;

import java.util.List;

public class InvitationServiceImpl implements InvitationService {
    private InvitationRepositoryImpl invitationRepository;

    public InvitationServiceImpl() {
        invitationRepository = new InvitationRepositoryImpl();
    }

    @Override
    public void persist(InvitationEntity entity) {
        invitationRepository.persist(entity);
        invitationRepository.close();
    }

    @Override
    public void update(InvitationEntity entity) {
        invitationRepository.update(entity);
        invitationRepository.close();
    }

    @Override
    public InvitationEntity findById(int id) {
        return invitationRepository.findById(id);
    }

    @Override
    public void delete(InvitationEntity entity) {
        invitationRepository.delete(entity);
        invitationRepository.close();
    }

    @Override
    public List<InvitationEntity> findAll() {
        return invitationRepository.findAll();
    }

    @Override
    public List<InvitationEntity> findAllByUser(UserEntity userId) {
        return invitationRepository.findAllByUser(userId);
    }

    @Override
    public void deleteAll() {
        invitationRepository.deleteAll();
        invitationRepository.close();
    }

}

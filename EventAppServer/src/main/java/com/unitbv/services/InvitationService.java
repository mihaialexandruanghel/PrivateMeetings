package com.unitbv.services;

import com.unitbv.entity.InvitationEntity;
import com.unitbv.entity.UserEntity;

import java.util.List;

public interface InvitationService {

    void persist(InvitationEntity entity);

    void update(InvitationEntity entity);

    InvitationEntity findById(int id);

    void delete(InvitationEntity entity);

    List<InvitationEntity> findAll();

    List<InvitationEntity> findAllByUser(UserEntity userId);

    void deleteAll();
}

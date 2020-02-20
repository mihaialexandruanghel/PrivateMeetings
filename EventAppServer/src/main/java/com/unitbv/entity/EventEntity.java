package com.unitbv.entity;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
public class EventEntity {

    @Id
    @GeneratedValue
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @Column
    private String eventName;

    @Column
    private LocalDate date;

    @Column
    private String location;

    @ManyToMany(mappedBy = "events")
    private List<UserEntity> users = new ArrayList<UserEntity>();

    @OneToMany
    private List<InvitationEntity> invitations = new ArrayList<InvitationEntity>();

    @Column
    private int numberOfSeats;

    public Long getId() {
        return id;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public List<UserEntity> getUsers() {
        return users;
    }

    public void setUsers(List<UserEntity> users) {
        this.users = users;
    }

    public List<InvitationEntity> getInvitations() {
        return invitations;
    }

    public void setInvitations(List<InvitationEntity> invitations) {
        this.invitations = invitations;
    }

    public int getNumberOfSeats() {
        return numberOfSeats;
    }

    public void setNumberOfSeats(int numberOfSeats) {
        this.numberOfSeats = numberOfSeats;
    }

}

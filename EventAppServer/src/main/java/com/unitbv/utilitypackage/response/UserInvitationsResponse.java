package com.unitbv.utilitypackage.response;

import com.unitbv.data.InvitationData;

import java.util.List;

public class UserInvitationsResponse {
    List<InvitationData> invitations;

    public List<InvitationData> getInvitations() {
        return invitations;
    }

    public void setInvitations(List<InvitationData> invitations) {
        this.invitations = invitations;
    }
}

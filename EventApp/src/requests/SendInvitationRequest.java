package requests;

import java.util.List;

public class SendInvitationRequest {
    private List<String> listOfUsers;

    private String event;

    public List<String> getListOfUsers() {
        return listOfUsers;
    }

    public void setListOfUsers(List<String> listOfUsers) {
        this.listOfUsers = listOfUsers;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }
}

package com.unitbv.utilitypackage.request;

public class AvailabilityRequest {
    private boolean availability;
    private String userName;

    public boolean isAvailability() {
        return availability;
    }

    public void setAvailability(boolean availability) {
        this.availability = availability;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}

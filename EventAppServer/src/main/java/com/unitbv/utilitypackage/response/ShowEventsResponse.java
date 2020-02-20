package com.unitbv.utilitypackage.response;

import com.unitbv.data.EventData;

import java.util.List;

public class ShowEventsResponse {
    private List<EventData> eventDataList;

    private String user;

    public List<EventData> getEventDataList() {
        return eventDataList;
    }

    public void setEventDataList(List<EventData> eventDataList) {
        this.eventDataList = eventDataList;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
}

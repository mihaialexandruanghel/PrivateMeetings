package com.unitbv.utilitypackage.response;

import com.unitbv.data.NotificationData;

import java.util.List;

public class ShowNotificationsResponse {
    private List<NotificationData> notificationDataList;

    public List<NotificationData> getNotificationDataList() {
        return notificationDataList;
    }

    public void setNotificationDataList(List<NotificationData> notificationDataList) {
        this.notificationDataList = notificationDataList;
    }
}

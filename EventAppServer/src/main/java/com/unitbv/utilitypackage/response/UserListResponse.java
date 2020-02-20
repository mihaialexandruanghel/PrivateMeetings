package com.unitbv.utilitypackage.response;

import com.unitbv.data.UserData;

import java.util.List;

public class UserListResponse {
    List<UserData> userDataList;

    public List<UserData> getUserDataList() {
        return userDataList;
    }

    public void setUserDataList(List<UserData> userDataList) {
        this.userDataList = userDataList;
    }
}

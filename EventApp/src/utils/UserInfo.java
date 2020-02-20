package utils;

public class UserInfo {

  public static String currentUserId;

  public static void setCurrentUserId(String currentUserId) {
    UserInfo.currentUserId = currentUserId;
  }

  public static String getCurrentUserId() {
    return currentUserId;
  }
}

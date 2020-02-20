package requests;

public class AcceptInvitationRequest {
  private String user;
  private String secretCode;

  public String getUser() {
    return user;
  }

  public void setUser(String user) {
    this.user = user;
  }

    public String getSecretCode() {
        return secretCode;
    }

    public void setSecretCode(String secretCode) {
        this.secretCode = secretCode;
    }
}

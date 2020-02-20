package response;
//clasa folosita pentru a putea lua datele de la server si le verifica in client. De exemplu mesajul de eroare sau codul raspunsului.

public class LoginResponse {
    String code;
    String message;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

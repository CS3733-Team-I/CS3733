package KioskApplication.entity;

public class Administrator {
    private String Email;
    private String Password;

    public Administrator(String email, String password) {
        Email = email;
        Password = password;
    }

    public String getEmail() {
        return Email;
    }

    public boolean login_check(Administrator A) {
        return Email.equals(A.Email) && Password.equals(A.Password);
    }
}

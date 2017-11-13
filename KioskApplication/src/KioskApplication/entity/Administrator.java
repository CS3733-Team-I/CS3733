package KioskApplication.entity;

public class Administrator {
    public String Name;
    private String Email;
    private String Password;

    public Administrator(String name, String email, String password) {
        Name = name;
        Email = email;
        Password = password;
    }

    public boolean login_check(Administrator A) {
        return Email.equals(A.Email) && Email.equals(A.Password);
    }
}

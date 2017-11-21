package entity;

public class Administrator {
    private String email;
    private String password;

    public Administrator(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public boolean verifyLogin(Administrator admin) {
        return email.equals(admin.email) && password.equals(admin.password);
    }

}

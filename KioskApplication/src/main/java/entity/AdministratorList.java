package entity;

import java.util.HashMap;

public class AdministratorList {
    private HashMap<String, Administrator> administrators;

    private AdministratorList() {
        administrators = new HashMap<>();
        addAdministrator(new Administrator("boss@hospital.com", "123"));
    }

    private static class AdministratorListSingleton {
        private static final AdministratorList _instance = new AdministratorList();
    }

    public static AdministratorList getInstance() {
        return AdministratorListSingleton._instance;
    }

    public void addAdministrator(Administrator admin) {
        administrators.put(admin.getEmail(), admin);
    }

    public boolean isValidLogin(String username, String password) {
        Administrator admin = administrators.get(username);
        if (admin != null) {
            return admin.verifyLogin(new Administrator(username, password));
        } else {
            return false;
        }
    }
}

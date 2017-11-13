package KioskApplication.entity;

import java.util.ArrayList;

public class AdministratorList {
    private ArrayList<Administrator> Administrators;

    public void add_adminstrator(Administrator A) {
        Administrators.add(A);
    }

    public AdministratorList() {
    }

    public boolean validLogin(Administrator A) {
        boolean ret_val = false;
        int i;
        for(i = 0; i < Administrators.size(); i++) {
            ret_val |= Administrators.get(i).login_check(A);
        }
        return ret_val;
    }
}

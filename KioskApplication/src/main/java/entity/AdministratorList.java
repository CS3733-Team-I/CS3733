package entity;

import java.util.ArrayList;

public class AdministratorList {
    private ArrayList<Administrator> Administrators;

    public void add_administrator(Administrator A) {
        Administrators.add(A);
    }

    public AdministratorList() {
        Administrators = new ArrayList<Administrator>();
    }

    public boolean validLogin(Administrator A) {
        boolean ret_val = false;
        int i;
        for(i = 0; i < Administrators.size(); i++) {
            ret_val |= Administrators.get(i).login_check(A);
        }
        return ret_val;
    }

    public ArrayList<Administrator> getAdministrators() {
        return Administrators;
    }
}
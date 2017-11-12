package KioskApplication.Entity;

import KioskApplication.database.objects.Node;

public class Request {
    Node location;
    String employee;

    public Request(Node location, String employee) {
        this.location = location;
        this.employee = employee;
    }
}

package KioskApplication.entity;

import KioskApplication.database.objects.Node;

//Class object that creates a 'request' which stores the information of a request
//This object can then be passed to the database as well as to the active requests list
public class Request {
    String location;
    String employee;

    public Request(String location, String employee) {
        this.location = location;
        this.employee = employee;
    }
}

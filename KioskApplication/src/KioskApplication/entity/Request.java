package KioskApplication.entity;

import KioskApplication.database.DatabaseController;
import KioskApplication.database.objects.Node;

//Class object that creates a 'request' which stores the information of a request
//This object can then be passed to the database as well as to the active requests list
public class Request {
    Node location;
    String employee;
    int requestID;

    public Request(Node location, String employee, int requestID) {
        this.location = location;
        this.employee = employee;
        this.requestID = requestID;
    }

    public Request(String nodeID, String employee, int requestID) {
        this.location = DatabaseController.getNode(nodeID);
        this.employee = employee;
        this.requestID = requestID;
    }

    //Getters for testing
    public String getEmployee(){
        return this.employee;
    }
    public int getRequestID(){
        return this.requestID;
    }
}
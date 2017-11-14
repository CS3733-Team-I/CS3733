package KioskApplication.entity;

import KioskApplication.database.DatabaseController;
import KioskApplication.database.objects.Node;

//Class object that creates a 'request' which stores the information of a request
//This object can then be passed to the database as well as to the active requests list
public abstract class Request {
    Node location;
    String employee;
    int requestID;

    public Request(Node location, String employee) {
        this.location = location;
        this.employee = employee;
        int numRequests = DatabaseController.getAllRequests().size();
        if(numRequests==0){
            this.requestID = 0;
        }else{
            this.requestID = DatabaseController.getAllRequests().get(numRequests-1).getRequestID() + 1;
//            DatabaseController.getAllInterpreterRequests().get(DatabaseController.getAllInterpreterRequests().size()-1).getInterpreterID() + 1;

        };
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
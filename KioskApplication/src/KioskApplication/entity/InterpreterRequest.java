package KioskApplication.entity;

import KioskApplication.database.DatabaseController;
import KioskApplication.database.objects.Node;

//Adds the field language
//Connection between the IRController and DatabaseController
public class InterpreterRequest{
    int interpreterID;
    Node location;
    String employee;
    String language;

    //Node Based
    //Takes location, employee, and language to add an InterpreterRequest to the database
    public InterpreterRequest(Node location, String employee, String language) {
        this.interpreterID = 0;
        this.location = location;
        this.employee = employee;
        this.language = language;
    }

    //ONLY for retrieving interpreterRequests from the database
    public InterpreterRequest(int interpreterID, String locationID, String employee, String language){
        this.interpreterID = interpreterID;
        this.location = DatabaseController.getNode(locationID);
        this.employee = employee;
        this.language = language;
    }

    /*
    //LocationID Based
    public InterpreterRequest(String locationID, String employee, String language,
                              int requestID, int interpreterID) {
        request = DatabaseController.addRequest(requestID, locationID, employee);
        this.language = language;
        this.interpreterID = interpreterID;
    }

    //Gets the Interpreter Request?? NOT SURE
    public InterpreterRequest(String language, int interpreterID, int requestID){
        request = DatabaseController.getRequest(requestID);
        this.language = language;
        this.interpreterID = interpreterID;
    }
    */

    public void place() {
        int numRequests = DatabaseController.getAllInterpreterRequests().size();
        if(numRequests==0){
            this.interpreterID = 0;
        }
        else {
            this.interpreterID = DatabaseController.getAllInterpreterRequests().get(numRequests - 1).getInterpreterID() + 1;
        }
        DatabaseController.addInterpreterRequest(this.interpreterID, this.location.getNodeID(), this.employee, this.language);
    }

    //Completes Request and clears it from the database
    public void completeInterpreterRequest(){
        DatabaseController.deleteInterpreterRequest(this.interpreterID);
    }

    //Getters for testing
    public String getLanguage(){
        return this.language;
    }
    public int getInterpreterID(){
        return this.interpreterID;
    }
    public String getEmployee() {
        return employee;
    }
    public Node getLocation() {
        return location;
    }
}
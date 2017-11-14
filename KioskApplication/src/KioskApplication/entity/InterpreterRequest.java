package KioskApplication.entity;

import KioskApplication.database.DatabaseController;
import KioskApplication.database.objects.Node;

//Adds the field language
public class InterpreterRequest{
    String language;
    int interpreterID;
    Request request;

    //Node Based
    public InterpreterRequest(Node location, String employee, String language,
                              int requestID, int interpreterID) {
        request = DatabaseController.addRequest(requestID, location.getNodeID(), employee);
        this.language = language;
        this.interpreterID = interpreterID;
    }

    //LocationID Based
    public InterpreterRequest(String locationID, String employee, String language,
                              int requestID, int interpreterID) {
        request = DatabaseController.addRequest(requestID, locationID, employee);
        this.language = language;
        this.interpreterID = interpreterID;
    }

    //Gets the Interpreter Request?? NOT SURE
    public InterpreterRequest(String language, int interpreterID, int requestID) {
        request = DatabaseController.getRequest(requestID);
        this.language = language;
        this.interpreterID = interpreterID;
    }

    //Getters for testing
    public String getLanguage(){
        return this.language;
    }
    public int getInterpreterID(){
        return this.interpreterID;
    }
    public Request getRequest() {
        return request;
    }
}
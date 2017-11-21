package entity;

import database.DatabaseController;
import database.objects.InterpreterRequest;
import database.objects.Request;
import utility.Request.Language;
import utility.Request.RequestProgressStatus;

import java.util.HashMap;
import java.util.LinkedList;

public class RequestEntity {
    private HashMap<String,InterpreterRequest> interpreterRequests;
    //private HashMap<String,SecurityRequest> securityRequests;
    //private HashMap<String,FoodRequest> foodRequests;
    //private HashMap<String,JanitorRequest> janitorRequests;
    //private HashMap<String,SecurityRequest> securityRequests;
    //private HashMap<String,SecurityRequest> securityRequests;

    private static RequestEntity instance = null;

    protected RequestEntity() {
        interpreterRequests=new HashMap<>();
    }

    public static RequestEntity getInstance() {
        if (instance == null) instance = new RequestEntity();
        return instance;
    }

    public void readAllFromDatabase(){
        LinkedList<InterpreterRequest> interpreterRequests = DatabaseController.getAllInterpreterRequests();
        for(InterpreterRequest iR:interpreterRequests) {
            String rID = iR.getRequestID();
            //updates the entire hashmap when called
            if (this.interpreterRequests.containsKey(rID)) {
                this.interpreterRequests.replace(rID, iR);
            }
            //adds new InterpreterRequests
            else{
                this.interpreterRequests.put(rID,iR);
            }
        }
    }

    public LinkedList<Request> getAllRequests(){
        LinkedList<Request> allRequests = new LinkedList<>();
        for (InterpreterRequest iR: interpreterRequests.values()){
            allRequests.add(iR);
        }
        return allRequests;
    }

    //Each type of Request has its own table in the database
    //TODO: incorporate search class into application so that nodeID can become location
    public void submitInterpreterRequest(String nodeID, String employee, String note, Language language){
        InterpreterRequest iR = new InterpreterRequest(nodeID, employee, note, language);
        interpreterRequests.putIfAbsent(iR.getRequestID(),iR);
        DatabaseController.addInterpreterRequest(iR);
    }

    public InterpreterRequest getInterpreterRequest(String requestID) throws NullPointerException{
        System.out.println("Getting InterpreterRequest");
        if(interpreterRequests.containsKey(requestID)) {
            return interpreterRequests.get(requestID);
        }
        else{
            readAllFromDatabase();
            if(interpreterRequests.containsKey(requestID)){
                return interpreterRequests.get(requestID);
            }
            else{
                throw new NullPointerException("Unable to find InterpreterRequest in database");
            }
        }
    }

    public void submitSecurityRequest(){

    }

    //Generic request deleting method
    public void deleteRequest(String requestID){
        String requestType = requestID.substring(0,3);
        if(requestType.equals("Int")){
            interpreterRequests.remove(requestID);
            DatabaseController.deleteInterpreterRequest(requestID);
            System.out.println("Deleting InterpreterRequest");
        }
        else if(requestType.equals("Sec")){
            System.out.println("Deleting SecurityRequest");
        }
        else if(requestType.equals("Foo")){
            System.out.println("Deleting FoodRequest");
        }
        else if(requestType.equals("Jan")){
            System.out.println("Deleting JanitorRequest");
        }
        else if(requestType.equals("Ins")){
            System.out.println("Deleting InsideTransportationRequest");
        }
        else if(requestType.equals("Out")){
            System.out.println("Deleting OutsideTransportationRequest");
        }
        else{
            System.out.println("Invalid requestID");
        }
    }

    //Generic request deleting method
    public void inProgressRequest(String requestID){
        String requestType = requestID.substring(0,2);
        if(requestType.equals("Int")){
            interpreterRequests.remove(requestID);
            DatabaseController.deleteInterpreterRequest(requestID);
            System.out.println("In Progress InterpreterRequest");
        }
        else if(requestType.equals("Sec")){
            System.out.println("In Progress SecurityRequest");
        }
        else if(requestType.equals("Foo")){
            System.out.println("In Progress FoodRequest");
        }
        else if(requestType.equals("Jan")){
            System.out.println("In Progress JanitorRequest");
        }
        else if(requestType.equals("Ins")){
            System.out.println("In Progress InsideTransportationRequest");
        }
        else if(requestType.equals("Out")){
            System.out.println("In Progress OutsideTransportationRequest");
        }
        else{
            System.out.println("Invalid requestID");
        }
    }

    //Generic request completing method
    public void completeRequest(String requestID){
        String requestType = requestID.substring(0,3);
        if(requestType.equals("Int")){
            InterpreterRequest iR = interpreterRequests.get(requestID);
            iR.complete();
            interpreterRequests.replace(requestID,iR);
            DatabaseController.updateInterpreterRequest(iR);
            System.out.println("Complete InterpreterRequest");
        }
        else if(requestType.equals("Sec")){
            System.out.println("Complete SecurityRequest");
        }
        else if(requestType.equals("Foo")){
            System.out.println("Complete FoodRequest");
        }
        else if(requestType.equals("Jan")){
            System.out.println("Complete JanitorRequest");
        }
        else if(requestType.equals("Ins")){
            System.out.println("Complete InsideTransportationRequest");
        }
        else if(requestType.equals("Out")){
            System.out.println("Complete OutsideTransportationRequest");
        }
        else{
            System.out.println("Invalid requestID");
        }
    }

    public void updateInterpreterRequest(String requestID, String nodeID, String assigner, String note,
                                         RequestProgressStatus status, Language language){
        InterpreterRequest oldReq = interpreterRequests.get(requestID);
        oldReq.setNodeID(nodeID);
        oldReq.setAssigner(assigner);
        oldReq.setNote(note);
        //not sure if editing the status is needed
        //oldReq.setStatus(status);
        oldReq.setLanguage(language);
        //TODO: figure out how to make update request a generic method
        DatabaseController.updateInterpreterRequest(oldReq);
    }
}

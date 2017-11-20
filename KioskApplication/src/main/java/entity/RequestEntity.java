package entity;

import database.DatabaseController;
import database.objects.InterpreterRequest;
import database.objects.Request;
import utility.Language;
import utility.RequestProgressStatus;

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
        for(InterpreterRequest iR:interpreterRequests)
            addInterpreterRequest(iR);
    }

    public LinkedList<Request> getAllRequests(){
        LinkedList<Request> allRequests = new LinkedList<>();
        for (InterpreterRequest iR: interpreterRequests.values()){
            allRequests.add(iR);
        }
        return allRequests;
    }

    private void addInterpreterRequest(InterpreterRequest iR){
        String rID = iR.getRequestID();
        interpreterRequests.put(rID, iR);
    }

    //Each type of Request has its own table in the database
    //TODO: incorporate search class into application so that nodeID can become location
    public void submitInterpreterRequest(String nodeID, String employee, String note, Language language){
        InterpreterRequest iR = new InterpreterRequest(nodeID, employee, note, language);
        interpreterRequests.putIfAbsent(iR.getRequestID(),iR);
        DatabaseController.addInterpreterRequest(iR);
    }

    public Request getRequest(String requestID){
        String requestType = requestID.substring(0,2);
        if(requestType.equals("Int")){
            System.out.println("Getting InterpreterRequest");
            return interpreterRequests.get(requestID);
        }
        else{
            System.out.println("Invalid requestID");
            return null;
        }
    }

    public void submitSecurityRequest(){

    }

    //Generic request deleting method
    public void deleteRequest(String requestID){
        String requestType = requestID.substring(0,2);
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

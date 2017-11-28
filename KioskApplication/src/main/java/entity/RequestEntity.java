package entity;

//import com.sun.xml.internal.bind.v2.TODO;
import database.DatabaseController;
import database.objects.InterpreterRequest;
import database.objects.Request;
import database.objects.SecurityRequest;
import utility.Request.Language;
import utility.Request.LanguageFrequency;
import utility.Request.RequestProgressStatus;
import utility.Request.RequestType;

import java.security.Security;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.LinkedList;

public class RequestEntity {
    private HashMap<String,InterpreterRequest> interpreterRequests;
    private HashMap<String,SecurityRequest> securityRequests;
    //private HashMap<String,FoodRequest> foodRequests;
    //private HashMap<String,JanitorRequest> janitorRequests;
    //private HashMap<String,SecurityRequest> securityRequests;
    //private HashMap<String,SecurityRequest> securityRequests;

    private long meanTimeToComplete;

    private static RequestEntity instance = null;

    private DatabaseController dbController;

    protected RequestEntity(boolean test) {
        interpreterRequests=new HashMap<>();
        securityRequests=new HashMap<>();

        if(test){
            dbController = DatabaseController.getTestInstance();
        }
        else {
            dbController = DatabaseController.getInstance();
        }
    }

    public static RequestEntity getInstance() {
        return SingletonHelper.instance;
    }

    public static RequestEntity getTestInstance(){
        return SingletonHelper.testInstance;
    }

    private static class SingletonHelper {
        private static final RequestEntity instance = new RequestEntity(false);
        private static final RequestEntity testInstance = new RequestEntity(true);
    }

    //reads all requests from the database
    public void readAllFromDatabase(){
        LinkedList<InterpreterRequest> interpreterRequests = dbController.getAllInterpreterRequests();
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
        LinkedList<SecurityRequest> securityRequests = dbController.getAllSecurityRequests();
        for(SecurityRequest sR: securityRequests) {
            String rID = sR.getRequestID();
            //updates the entire hashmap when called
            if (this.securityRequests.containsKey(rID)) {
                this.securityRequests.replace(rID, sR);
            }
            //adds new SecurityRequests
            else{
                this.securityRequests.put(rID, sR);
            }
        }
    }


    public LinkedList<Request> getAllRequests(){
        LinkedList<Request> allRequests = new LinkedList<>();
        for (InterpreterRequest iR: interpreterRequests.values()){
            allRequests.add(iR);
        }
        for(SecurityRequest sR: securityRequests.values()){
            allRequests.add(sR);
        }
        return allRequests;
    }

    /**
     * Methods for all request types
     */

    public LinkedList<Request> getStatusRequests(RequestProgressStatus status){
        LinkedList<Request> displayedRequests = new LinkedList<>();
        LinkedList<Request> requests = getAllRequests();
        for (int i = 0; i < requests.size(); i++) {
            switch (status){
                case TO_DO:
                    if(requests.get(i).getStatus().equals(RequestProgressStatus.TO_DO)){
                        displayedRequests.add(requests.get(i));
                    }
                    break;
                case IN_PROGRESS:
                    if(requests.get(i).getStatus().equals(RequestProgressStatus.IN_PROGRESS)){
                        displayedRequests.add(requests.get(i));
                    }
                    break;
                case DONE:
                    if(requests.get(i).getStatus().equals(RequestProgressStatus.DONE)){
                        displayedRequests.add(requests.get(i));
                    }
                    break;
            }


        }
        return displayedRequests;
    }

    public RequestType checkRequestType(String requestID) {
        String requestType = requestID.substring(0, 3);
        if (requestType.equals("Int")) {
            return RequestType.INTERPRETER;
        } else if (requestType.equals("Sec")) {
            return RequestType.SECURITY;
        } else if (requestType.equals("Foo")) {
            return RequestType.FOOD;
        } else if (requestType.equals("Jan")) {
            return RequestType.JANITOR;
        //} else if (requestType.equals("Ins")) {
        //} else if (requestType.equals("Out")) {
        } else {
            System.out.println("Invalid requestID");
            return null;
        }
    }

    //Generic request deleting method
    public void deleteRequest(String requestID){
        String requestType = requestID.substring(0,3);
        if(requestType.equals("Int")){
            interpreterRequests.remove(requestID);
            dbController.deleteInterpreterRequest(requestID);
            System.out.println("Deleting InterpreterRequest");
        }
        else if(requestType.equals("Sec")){
            securityRequests.remove(requestID);
            dbController.deleteSecurityRequest(requestID);
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

    //Generic request in progress maker
    public void markInProgress(String completer, String requestID){
        String requestType = requestID.substring(0,3);
        if(requestType.equals("Int")){
            InterpreterRequest iR = interpreterRequests.get(requestID);
            iR.markInProgress(completer);
            dbController.updateInterpreterRequest(iR);
            System.out.println("In Progress InterpreterRequest");
        }
        else if(requestType.equals("Sec")){
            SecurityRequest sR = securityRequests.get(requestID);
            sR.markInProgress(completer);
            dbController.updateSecurityRequest(sR);
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
            dbController.updateInterpreterRequest(iR);
            System.out.println("Complete InterpreterRequest");
        }
        else if(requestType.equals("Sec")){
            SecurityRequest sR = securityRequests.get(requestID);
            sR.complete();
            securityRequests.replace(requestID, sR);
            dbController.updateSecurityRequest(sR);
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


    /**
     * InterpreterRequest methods
     */

    public String submitInterpreterRequest(String nodeID, String assignee, String note,
                                           Language lang){
        long currTime = System.currentTimeMillis();
        Timestamp submittedTime = new Timestamp(currTime);
        Timestamp startedTime = new Timestamp(currTime-1);
        Timestamp completedTime = new Timestamp(currTime-1);
        String rID = "Int"+currTime;
        InterpreterRequest iR = new InterpreterRequest(rID, nodeID, assignee, "", note,
                submittedTime, startedTime, completedTime,RequestProgressStatus.TO_DO,lang);
        interpreterRequests.put(rID, iR);
        dbController.addInterpreterRequest(iR);
        return rID;
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

    public void updateInterpreterRequest(String requestID, String nodeID, String assigner, String note,
                                         Timestamp submittedTime, Timestamp completedTime,
                                         RequestProgressStatus status, Language language){
        InterpreterRequest oldReq = interpreterRequests.get(requestID);
        oldReq.setNodeID(nodeID);
        oldReq.setAssigner(assigner);
        oldReq.setNote(note);
        oldReq.setSubmittedTime(submittedTime);
        oldReq.setCompletedTime(completedTime);
        //not sure if editing the status is needed
        oldReq.setStatus(status);
        oldReq.setLanguage(language);
        //TODO: figure out how to make update request a generic method
        dbController.updateInterpreterRequest(oldReq);
    }

    /**
     * SecurityRequest methods
     */

    public String submitSecurityRequest(String nodeID, String assignee, String note,
                                        int priority){
        long currTime = System.currentTimeMillis();
        Timestamp submittedTime = new Timestamp(currTime);
        Timestamp startedTime = new Timestamp(currTime-1);
        Timestamp completedTime = new Timestamp(currTime-1);
        String rID = "Sec"+currTime;
        SecurityRequest sR = new SecurityRequest(rID, nodeID, assignee, "", note,
                submittedTime, startedTime, completedTime, RequestProgressStatus.TO_DO,priority);
        securityRequests.put(rID, sR);
        dbController.addSecurityRequest(sR);
        return rID;
    }

    public SecurityRequest getSecurityRequest(String requestID) throws NullPointerException{
        System.out.println("Getting Security Request");
        if(securityRequests.containsKey(requestID)) {
            return securityRequests.get(requestID);
        }
        else{
            readAllFromDatabase();
            if(securityRequests.containsKey(requestID)){
                return securityRequests.get(requestID);
            }
            else{
                throw new NullPointerException("Unable to find SecurityReqest in database");
            }
        }
    }

    public void updateSecurityRequest(String requestID, String nodeID, String assigner, String note,
                                      Timestamp submittedTime, Timestamp completedTime,
                                      RequestProgressStatus status, int priority){
        SecurityRequest oldReq = securityRequests.get(requestID);
        oldReq.setNodeID(nodeID);
        oldReq.setAssigner(assigner);
        oldReq.setNote(note);
        oldReq.setSubmittedTime(submittedTime);
        oldReq.setCompletedTime(completedTime);
        //not sure if editing the status is needed
        oldReq.setStatus(status);
        oldReq.setPriority(priority);
        //TODO: figure out how to make update request a generic method
        dbController.updateSecurityRequest(oldReq);
    }

    /**
     * Tracking information
     * what we want:
     * Time from IN_PROGRESS to COMPLETE for requests
     * Heatmap of request locations
     * Statistics on interpreter request languages
     * Statistics on all requests (how many of each type were made
     * Statistics on food ordered for stocking purposes
     * common IT request problems
     */

    // gets mean time to complete a request from IN_PROGRESS to DONE
    public void getMeanTimeToComplete(){
        long sum=0;
        int total=0;
        for (InterpreterRequest iR: interpreterRequests.values()){
            if(iR.getStatus()==RequestProgressStatus.DONE){
                total++;
                sum+=iR.timeToComplete();
            }
        }
        for (SecurityRequest sR: securityRequests.values()){
            if(sR.getStatus()==RequestProgressStatus.DONE){
                total++;
                sum+=sR.timeToComplete();
            }
        }
        if(total!=0){
            meanTimeToComplete=sum/total;
        }
    }

    // gives a frequency histogram for interpreter request languages
    public LinkedList<LanguageFrequency> getLanguageFrequency(){
        LinkedList<LanguageFrequency> freq = new LinkedList<>();
        for (InterpreterRequest iR: interpreterRequests.values()){
            LanguageFrequency currLang = new LanguageFrequency(iR.getLanguage());
            if(freq.contains(currLang)){
                freq.get(freq.indexOf(currLang)).increment();
            }
            else{
                freq.add(currLang);
            }
        }
        return freq;
    }
}

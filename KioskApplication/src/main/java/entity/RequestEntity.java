package entity;

import database.DatabaseController;
import database.connection.NotFoundException;
import database.objects.*;
import database.objects.requests.*;
import database.utility.DatabaseException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.PieChart;
import utility.request.Language;
import utility.request.LanguageFrequency;
import utility.request.RequestProgressStatus;
import utility.request.RequestType;
import utility.request.*;

import java.sql.Timestamp;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;

public class RequestEntity {
    private HashMap<String,InterpreterRequest> interpreterRequests;
    private HashMap<String,SecurityRequest> securityRequests;
    private HashMap<String,FoodRequest> foodRequests;
    private HashMap<String,JanitorRequest> janitorRequests;
    private HashMap<String,ITRequest> itRequests;
    private HashMap<String, MaintenanceRequest> maintenanceRequests;


    private long meanTimeToComplete;

    private static RequestEntity instance = null;

    private DatabaseController dbController;

    private ActivityLogger activityLogger;

    private RequestEntity(boolean test) {
        activityLogger=ActivityLogger.getInstance();
        interpreterRequests=new HashMap<>();
        securityRequests=new HashMap<>();
        foodRequests=new HashMap<>();
        janitorRequests=new HashMap<>();
        itRequests=new HashMap<>();
        maintenanceRequests=new HashMap<>();

        if(test){
            dbController = DatabaseController.getInstance();
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

    /**
     * reads all requests from the database
     */
    public void readAllFromDatabase(){
        // Clears the hashmaps
        interpreterRequests.clear();
        securityRequests.clear();
        foodRequests.clear();
        janitorRequests.clear();
        itRequests.clear();
        maintenanceRequests.clear();
        // Refills the hashmaps from the database
        for(InterpreterRequest interpreterRequest:dbController.getAllInterpreterRequests()){
            interpreterRequests.put(interpreterRequest.getRequestID(),interpreterRequest);
        }
        for(SecurityRequest securityRequest:dbController.getAllSecurityRequests()){
            securityRequests.put(securityRequest.getRequestID(),securityRequest);
        }
        for(FoodRequest foodRequest: dbController.getAllFoodRequests()){
            foodRequests.put(foodRequest.getRequestID(),foodRequest);
        }
        for (JanitorRequest janitorRequest:dbController.getAllJanitorRequests()){
            janitorRequests.put(janitorRequest.getRequestID(),janitorRequest);
        }
        for (ITRequest itRequest:dbController.getAllITRequests()){
            itRequests.put(itRequest.getRequestID(),itRequest);
        }
        for(MaintenanceRequest mtRequest: dbController.getAllmtRequest()){
            maintenanceRequests.put(mtRequest.getRequestID(),mtRequest);
        }
    }

    /**
     * Gets a list of all interpreter requests in the hashmaps
     * @return list of all interpreterRequest found in interpreterRequests hashmap
     */
    public LinkedList<InterpreterRequest> getAllinterpters(){
        LinkedList<InterpreterRequest> intRequests = new LinkedList<>();
        for(InterpreterRequest iR: interpreterRequests.values()){
            intRequests.add(iR);
        }
        return intRequests;
    }

    /**
     * Gets a list of all security requests in the hashmaps
     * @return list of all securityRequest found in securityRequests hashmap
     */
    public LinkedList<Request> getAllSecurity(){
        LinkedList<Request> secRequests = new LinkedList<>();
        for(SecurityRequest sR: securityRequests.values()){
            secRequests.add(sR);
        }
        return secRequests;
    }

    /**
     * Gets a list of all food requests in the hashmaps
     * @return list of all foodRequest found in foodRequests hashmap
     */
    public LinkedList<Request> getAllFoodRequests(){
        LinkedList<Request> fooRequests = new LinkedList<>();
        for(FoodRequest fR: foodRequests.values()){
            fooRequests.add(fR);
        }
        return fooRequests;
    }

    public LinkedList<Request> getAllJanitorRequests(){
        LinkedList<Request> janRequests = new LinkedList<>();
        for(JanitorRequest janitorRequest:janitorRequests.values()){
            janRequests.add(janitorRequest);
        }
        return janRequests;
    }

    public LinkedList<Request> getAllITRequests(){
        LinkedList<Request> itRequests = new LinkedList<>();
        for(ITRequest itRequest:this.itRequests.values()){
            itRequests.add(itRequest);
        }
        return itRequests;
    }

    public LinkedList<Request> getAllmtRequests(){
        LinkedList<Request> mtRequests = new LinkedList<>();
        for(MaintenanceRequest mtRequest:this.maintenanceRequests.values()){
            mtRequests.add(mtRequest);
        }
        return mtRequests;
    }

    /**
     * Generic method to get all requests in the hashmaps
     * @return the requests from the hashmaps
     */
    public LinkedList<Request> getAllRequests(){
        LinkedList<Request> allRequests = new LinkedList<>();
        allRequests.addAll(getAllinterpters());
        allRequests.addAll(getAllSecurity());
        allRequests.addAll(getAllFoodRequests());
        allRequests.addAll(getAllJanitorRequests());
        allRequests.addAll(getAllITRequests());
        allRequests.addAll(getAllmtRequests());
        return allRequests;
    }

    /**
     * method that returns all requests in hashmap
     * @return the requests in hashmap form
     */
    public HashMap<String, Request> getallRequestsHM() {
        HashMap<String,Request> all = new HashMap<>();
            all.putAll(interpreterRequests);
            all.putAll(foodRequests);
            all.putAll(securityRequests);
            all.putAll(janitorRequests);
            all.putAll(itRequests);
            all.putAll(maintenanceRequests);
        return all;
    }

    /**
     * Filters all the requests in the hashmaps by the inputted status
     * @param status
     * @return linkedList of requests that fall under the same request progress status
     */
    public LinkedList<Request> getStatusRequests(RequestProgressStatus status){
        LinkedList<Request> requests = getAllRequests();
        LinkedList<Request> displayedRequests = filterByStatus(requests, status);
        return displayedRequests;
    }

    /**
     * Filters the requests from the inputted list by the inputted status
     * @param requests
     * @param status
     * @return linkedList of requests that fall under the same request progress status
     */
    public LinkedList<Request> filterByStatus(LinkedList<Request> requests, RequestProgressStatus status){
        LinkedList<Request> displayedRequests = new LinkedList<>();
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

    /**
     * Determines the type of request from a requestID
     * @param requestID
     * @return the requestType of a specific request
     */
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
        }else if(requestType.equals("Man")) {
            return RequestType.MAINTENANCE;
        }else if(requestType.equals("ITT")){
            return RequestType.IT;
        //} else if (requestType.equals("Ins")) {
        //} else if (requestType.equals("Out")) {
        }
        else {
            System.out.println("Invalid requestID");
            return null;
        }
    }



    /**
     * Generic request for deleting a request from the database and hashmaps
     * @param requestID
     */
    public void deleteRequest(String requestID){
        RequestType requestType = checkRequestType(requestID);
//        uRequestID--;
        if(requestType.equals(RequestType.INTERPRETER)){
            activityLogger.logRequestDelete(LoginEntity.getInstance().getCurrentLogin(),interpreterRequests.get(requestID));
            interpreterRequests.remove(requestID);
            dbController.deleteInterpreterRequest(requestID);
        }
        else if(requestType.equals(RequestType.SECURITY)){
            activityLogger.logRequestDelete(LoginEntity.getInstance().getCurrentLogin(),securityRequests.get(requestID));
            securityRequests.remove(requestID);
            dbController.deleteSecurityRequest(requestID);
        }
        else if(requestType.equals(RequestType.FOOD)){
            activityLogger.logRequestDelete(LoginEntity.getInstance().getCurrentLogin(),foodRequests.get(requestID));
            foodRequests.remove(requestID);
            dbController.deleteFoodRequest(requestID);
        }
        else if(requestType.equals(RequestType.JANITOR)){
            activityLogger.logRequestDelete(LoginEntity.getInstance().getCurrentLogin(),janitorRequests.get(requestID));
            janitorRequests.remove(requestID);
            dbController.deleteJanitorRequest(requestID);
        }
        else if(requestType.equals(RequestType.IT)){
            activityLogger.logRequestDelete(LoginEntity.getInstance().getCurrentLogin(),itRequests.get(requestID));
            itRequests.remove(requestID);
            dbController.deleteITRequest(requestID);
        }
        else if(requestType.equals(RequestType.MAINTENANCE)){
            activityLogger.logRequestDelete(LoginEntity.getInstance().getCurrentLogin(),maintenanceRequests.get(requestID));
            maintenanceRequests.remove(requestID);
            dbController.deleteMaintenanceRequest(requestID);
        }
        else if(requestType.equals("Ins")){ //TODO: change to Enum
            System.out.println("Deleting InsideTransportationRequest");
        }
        else if(requestType.equals("Out")){ //TODO: change to Enum
            System.out.println("Deleting OutsideTransportationRequest");
        }
        else{
            System.out.println("Invalid requestID");
        }
    }

    //Generic request in progress maker

    /**
     * Generic method to mark a request in progress from a requestID
     * Also adds the completer to the request object completer field
     * @param completerID
     * @param requestID
     */
    public void markInProgress(int completerID, String requestID){
        RequestType requestType = checkRequestType(requestID);
        if(requestType.equals(RequestType.INTERPRETER)){
            InterpreterRequest oldIR = interpreterRequests.get(requestID);
            InterpreterRequest newIR = oldIR;
            newIR.setInProgress(completerID);
            dbController.updateInterpreterRequest(newIR);
            activityLogger.logRequestChange(LoginEntity.getInstance().getCurrentLogin(),newIR,oldIR);
        }
        else if(requestType.equals(RequestType.SECURITY)){
            SecurityRequest oldSR = securityRequests.get(requestID);
            SecurityRequest newSR = oldSR;
            newSR.setInProgress(completerID);
            dbController.updateSecurityRequest(newSR);
            activityLogger.logRequestChange(LoginEntity.getInstance().getCurrentLogin(),newSR,oldSR);
        }
        else if(requestType.equals(RequestType.FOOD)){
            FoodRequest oldFR = foodRequests.get(requestID);
            FoodRequest newFR = oldFR;
            newFR.setInProgress(completerID);
            dbController.updateFoodRequest(newFR);
            activityLogger.logRequestChange(LoginEntity.getInstance().getCurrentLogin(),newFR,oldFR);
        }
        else if(requestType.equals(RequestType.JANITOR)){
            JanitorRequest oldJR = janitorRequests.get(requestID);
            JanitorRequest newJR = oldJR;
            newJR.setInProgress(completerID);
            dbController.updateJanitorRequest(newJR);
            activityLogger.logRequestChange(LoginEntity.getInstance().getCurrentLogin(),newJR,oldJR);
        }
        else if(requestType.equals(RequestType.IT)){
            ITRequest oldITR = itRequests.get(requestID);
            ITRequest newITR = oldITR;
            newITR.setInProgress(completerID);
            dbController.updateITRequest(newITR);
            activityLogger.logRequestChange(LoginEntity.getInstance().getCurrentLogin(),newITR,oldITR);
        }
        else if (requestType.equals(RequestType.MAINTENANCE)) {
            MaintenanceRequest oldMR = maintenanceRequests.get(requestID);
            MaintenanceRequest newMR = oldMR;
            newMR.setInProgress(completerID);
            dbController.updateMaintenanceRequest(newMR);
            activityLogger.logRequestChange(LoginEntity.getInstance().getCurrentLogin(),newMR,oldMR);
        }
//        else if(requestType.equals(RequestType"Ins")){
//            System.out.println("In Progress InsideTransportationRequest");
//        }
//        else if(requestType.equals(RequestType"Out")){
//            System.out.println("In Progress OutsideTransportationRequest");
//        }
        else {
            System.out.println("Invalid requestID");
        }
    }

    //Generic request completing method

    /**
     * Generic method to complete a request and changes request status to DONE
     * @param requestID
     */
    public void completeRequest(String requestID){
        RequestType requestType = checkRequestType(requestID);
        if(requestType.equals(RequestType.INTERPRETER)){
            InterpreterRequest oldIR = interpreterRequests.get(requestID);
            InterpreterRequest newIR = oldIR;
            newIR.setComplete();
            dbController.updateInterpreterRequest(newIR);
            activityLogger.logRequestChange(LoginEntity.getInstance().getCurrentLogin(),newIR,oldIR);
        }
        else if(requestType.equals(RequestType.SECURITY)){
            SecurityRequest oldSR = securityRequests.get(requestID);
            SecurityRequest newSR = oldSR;
            newSR.setComplete();
            dbController.updateSecurityRequest(newSR);
            activityLogger.logRequestChange(LoginEntity.getInstance().getCurrentLogin(),newSR,oldSR);
        }
        else if(requestType.equals(RequestType.FOOD)){
            FoodRequest oldFR = foodRequests.get(requestID);
            FoodRequest newFR = oldFR;
            newFR.setComplete();
            dbController.updateFoodRequest(newFR);
            activityLogger.logRequestChange(LoginEntity.getInstance().getCurrentLogin(),newFR,oldFR);
        }
        else if(requestType.equals(RequestType.JANITOR)){
            JanitorRequest oldJR = janitorRequests.get(requestID);
            JanitorRequest newJR = oldJR;
            newJR.setComplete();
            dbController.updateJanitorRequest(newJR);
            activityLogger.logRequestChange(LoginEntity.getInstance().getCurrentLogin(),newJR,oldJR);
        }
        else if(requestType.equals(RequestType.IT)){
            ITRequest oldITR = itRequests.get(requestID);
            ITRequest newITR = oldITR;
            newITR.setComplete();
            dbController.updateITRequest(newITR);
            activityLogger.logRequestChange(LoginEntity.getInstance().getCurrentLogin(),newITR,oldITR);
        }
        else if (requestType.equals(RequestType.MAINTENANCE)) {
            MaintenanceRequest oldMR = maintenanceRequests.get(requestID);
            MaintenanceRequest newMR = oldMR;
            newMR.setComplete();
            dbController.updateMaintenanceRequest(newMR);
            activityLogger.logRequestChange(LoginEntity.getInstance().getCurrentLogin(), newMR, oldMR);
        }
//        else if(requestType.equals(RequestType"Ins"))
//            System.out.println("Complete InsideTransportationRequest");
//        }
//        else if(requestType.equals(RequestType"Out")){
//            System.out.println("Complete OutsideTransportationRequest");
//        }
        else{
            System.out.println("Invalid requestID");
        }
    }

    /**
     * Generic method to get a request form a requestID
     * @param requestID
     * @return returns the request object attatched to the requestID
     */
    public Request getRequest(String requestID){
        Request request = null;
        if(checkRequestType(requestID).equals(RequestType.INTERPRETER)){
            request = getInterpreterRequest(requestID);
        }
        else if(checkRequestType(requestID).equals(RequestType.FOOD)){
            request = getFoodRequest(requestID);
        }
        else if (checkRequestType(requestID).equals(RequestType.SECURITY)){
            request = getSecurityRequest(requestID);
        }
        else if (checkRequestType(requestID).equals(RequestType.JANITOR)){
            request = getJanitorRequest(requestID);
        }
        else if (checkRequestType(requestID).equals(RequestType.IT)){
            request = getITRequest(requestID);
        }
        else if (checkRequestType(requestID).equals(RequestType.MAINTENANCE)){
            request = getMaintenanceRequest(requestID);
        }
        return request;
    }

    /**
     *
     * @param requestID
     * @return
     */
    public IEmployee getAssigner(String requestID) throws NotFoundException{
        try {
            Request request = getRequest(requestID);
            return dbController.getEmployee(request.getAssignerID());
        } catch (DatabaseException e){
            new NotFoundException("Employee not found");
        }
        return NullEmployee.getInstance();
    }

    /**
     * Updates a request that is already in the database with the given requestID
     * @param requestID
     * @param nodeID
     * @param assignerID
     * @param note
     * @param submittedTime
     * @param completedTime
     * @param status
     */
    private void updateRequest(String requestID, String nodeID, int assignerID, String note,
                              Timestamp submittedTime, Timestamp completedTime,
                              RequestProgressStatus status){
        Request oldReq = getRequest(requestID);
        oldReq.setNodeID(nodeID);
        oldReq.setAssignerID(assignerID);
        oldReq.setNote(note);
        oldReq.setSubmittedTime(submittedTime);
        oldReq.setCompletedTime(completedTime);
        //not sure if editing the status is needed
        oldReq.setStatus(status);
        switch (checkRequestType(requestID)){
            case INTERPRETER:
                dbController.updateInterpreterRequest((InterpreterRequest) oldReq);
                break;
            case SECURITY:
                dbController.updateSecurityRequest((SecurityRequest) oldReq);
                break;
            case FOOD:
                dbController.updateFoodRequest((FoodRequest) oldReq);
                break;
            case JANITOR:
                dbController.updateJanitorRequest((JanitorRequest) oldReq);
                break;
            case IT:
                dbController.updateITRequest((ITRequest) oldReq);
                break;
            case MAINTENANCE:
                dbController.updateMaintenanceRequest((MaintenanceRequest) oldReq);
                break;
        }

    }


    /**
     * Gets the completer of a request if it is in progress
     * @param requestID
     * @return employee
     */
    public IEmployee getCompleter(String requestID) {
        try {
            Request request = getRequest(requestID);
            if(request.getStatus()!=RequestProgressStatus.TO_DO) {
                return dbController.getEmployee(request.getCompleterID());
            }
        } catch (DatabaseException e){

        }
        return NullEmployee.getInstance();
    }

    /**
     * Adds an interpreter request to the database
     * @param nodeID
     * @param assignerID
     * @param note
     * @param lang
     * @return adds an interpreter request to the database and returns that request
     */
    public String submitInterpreterRequest(String nodeID, int assignerID, String note,
                                           Language lang){
        long currTime = System.currentTimeMillis();
        Timestamp submittedTime = new Timestamp(currTime);
        Timestamp startedTime = new Timestamp(currTime-1);
        Timestamp completedTime = new Timestamp(currTime-1);
        String rID = "Int"+currTime;
        InterpreterRequest iR = new InterpreterRequest(rID, nodeID, assignerID, assignerID, note,
                submittedTime, startedTime, completedTime,RequestProgressStatus.TO_DO,lang);
        //dbController.insertRequestIntoView(iR);
        interpreterRequests.put(rID, iR);
        dbController.addInterpreterRequest(iR);
        activityLogger.logRequestAdd(LoginEntity.getInstance().getCurrentLogin(),iR);
        return rID;
    }

    /**
     * gets an interpreter request from the database
     * @param requestID
     * @return gets an interpreter request from the database that matches the given requestID
     * @throws NullPointerException
     */
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

    /**
     * updates an interpreterRequest that is currently
     * @param requestID
     * @param nodeID
     * @param assignerID
     * @param note
     * @param submittedTime
     * @param completedTime
     * @param status
     * @param language
     */
    public void updateInterpreterRequest(String requestID, String nodeID, int assignerID, String note,
                                         Timestamp submittedTime, Timestamp completedTime,
                                         RequestProgressStatus status, Language language){
        InterpreterRequest oldReq = interpreterRequests.get(requestID);
        oldReq.setLanguage(language);
        updateRequest(requestID,nodeID,assignerID,note,submittedTime,completedTime,status);
        dbController.updateInterpreterRequest(oldReq);
    }

    /**
     *
     * @param nodeID
     * @param assignerID
     * @param note
     * @param priority
     * @return
     */
    public String submitSecurityRequest(String nodeID, int assignerID, String note,
                                        int priority){
        long currTime = System.currentTimeMillis();
        Timestamp submittedTime = new Timestamp(currTime);
        Timestamp startedTime = new Timestamp(currTime-1);
        Timestamp completedTime = new Timestamp(currTime-1);
        String rID = "Sec"+currTime;
        SecurityRequest sR = new SecurityRequest(rID, nodeID, assignerID, assignerID, note,
                submittedTime, startedTime, completedTime, RequestProgressStatus.TO_DO,priority);

        securityRequests.put(rID, sR);
        dbController.addSecurityRequest(sR);
        activityLogger.logRequestAdd(LoginEntity.getInstance().getCurrentLogin(),sR);
        return rID;
    }

    /**
     * gets a security request from the database
     * @param requestID
     * @return gets a security request form the database that matches the requestID
     * @throws NullPointerException
     */
    public SecurityRequest getSecurityRequest(String requestID) throws NullPointerException{
        System.out.println("Getting Security request");
        if(securityRequests.containsKey(requestID)) {
            return securityRequests.get(requestID);
        }
        else{
            readAllFromDatabase();
            if(securityRequests.containsKey(requestID)){
                return securityRequests.get(requestID);
            }
            else{
                throw new NullPointerException("Unable to find SecurityRequest in database");
            }
        }
    }

    /**
     * updates a securityRequest that is currently in the database
     * @param requestID
     * @param nodeID
     * @param assignerID
     * @param note
     * @param submittedTime
     * @param completedTime
     * @param status
     * @param priority
     */
    public void updateSecurityRequest(String requestID, String nodeID, int assignerID, String note,
                                      Timestamp submittedTime, Timestamp completedTime,
                                      RequestProgressStatus status, int priority){
        SecurityRequest oldReq = securityRequests.get(requestID);
        updateRequest(requestID,nodeID,assignerID,note,submittedTime,completedTime,status);
        dbController.updateSecurityRequest(oldReq);
    }

    /**
     * Adds food request to the database and the hashmaps
     * @param nodeID
     * @param assignerID
     * @param note
     * @param destinationNodeID
     * @param deliveryDate
     * @return Adds food request to the database and the hashmaps
     */
    public String submitFoodRequest(String nodeID, int assignerID, String note,
                                    String destinationNodeID, LocalTime deliveryDate){
        long currTime = System.currentTimeMillis();
        Timestamp submittedTime = new Timestamp(currTime);
        Timestamp startedTime = new Timestamp(currTime-1);
        Timestamp completedTime = new Timestamp(currTime-1);
        String rID = "Foo"+currTime;

        Calendar now = Calendar.getInstance();
        now.set(Calendar.HOUR, deliveryDate.getHour());
        now.set(Calendar.MINUTE, deliveryDate.getMinute());
        now.set(Calendar.SECOND, deliveryDate.getSecond());

        Timestamp deliveryTime = new Timestamp(now.getTimeInMillis());

        FoodRequest fR = new FoodRequest(rID, nodeID, assignerID, assignerID, note,
                submittedTime, startedTime, completedTime,RequestProgressStatus.TO_DO, destinationNodeID, deliveryTime);

        foodRequests.put(rID, fR);
        dbController.addFoodRequest(fR);
        activityLogger.logRequestAdd(LoginEntity.getInstance().getCurrentLogin(),fR);
        return rID;
    }

    /**
     * gets a food request form the database
     * @param requestID
     * @return gets a food request from the database that matches the given requestID
     * @throws NullPointerException
     */
    public FoodRequest getFoodRequest(String requestID)throws NullPointerException{
        System.out.println("Getting Food Request");
        if (foodRequests.containsKey(requestID)){
            return foodRequests.get(requestID);
        }
        else{
            readAllFromDatabase();
            if(foodRequests.containsKey(requestID)){
                return foodRequests.get(requestID);
            }
            else{
                throw new NullPointerException("Unable to find Food Request in database");
            }
        }
    }

    /**
<<<<<<< HEAD
=======
     * updates a foodRequest that is currently in the database
     * @param requestID
     * @param nodeID
     * @param assignerID
     * @param note
     * @param submittedTime
     * @param completedTime
     * @param status
     * @param destinationNodeID
     * @param deliveryDate
     */
    public void updateFoodRequest(String requestID, String nodeID, int assignerID, String note,
                                  Timestamp submittedTime, Timestamp completedTime,
                                  RequestProgressStatus status, String destinationNodeID,
                                  Timestamp deliveryDate){
        FoodRequest oldReq = foodRequests.get(requestID);
        oldReq.setRestaurantID(destinationNodeID);
        oldReq.setDeliveryDate(deliveryDate);
        updateRequest(requestID,nodeID,assignerID,note,submittedTime,completedTime,status);
        dbController.updateFoodRequest(oldReq);
    }

    /**
>>>>>>> 6b73d08da35171f8afbb5472ea23ad64f6e85038
     * For submitting JanitorRequests
     * @param nodeID
     * @param assignerID
     * @param note
     * @return
     */
    public String submitJanitorRequest(String nodeID, int assignerID, String note){
        long currTime = System.currentTimeMillis();
        Timestamp submittedTime = new Timestamp(currTime);
        Timestamp startedTime = new Timestamp(currTime-1);
        Timestamp completedTime = new Timestamp(currTime-1);
        String rID = "Jan"+currTime;

        JanitorRequest janitorRequest = new JanitorRequest(rID, nodeID, assignerID, assignerID, note,
                submittedTime, startedTime, completedTime,RequestProgressStatus.TO_DO);

        janitorRequests.put(rID, janitorRequest);
        dbController.addJanitorRequest(janitorRequest);
        activityLogger.logRequestAdd(LoginEntity.getInstance().getCurrentLogin(),janitorRequest);
        return rID;
    }

    /**
     *
     * @param requestID
     * @return
     * @throws NullPointerException
     */
    public JanitorRequest getJanitorRequest(String requestID) throws NullPointerException{
        if(janitorRequests.containsKey(requestID)){
            return janitorRequests.get(requestID);
        }
        else {
            readAllFromDatabase();
            if(janitorRequests.containsKey(requestID)){
                return janitorRequests.get(requestID);
            }
            else {
                throw new NullPointerException("Unable to find Janitor request in the database");
            }
        }
    }

    /**
     * Vastly simplified updating method
     * @param janitorRequest
     */
    public void updateJanitorRequest(JanitorRequest janitorRequest){
        janitorRequests.replace(janitorRequest.getRequestID(),janitorRequest);
        dbController.updateJanitorRequest(janitorRequest);
    }

    /**
     * For submitting Maintenance Request
     * @param nodeID
     * @param assignerID
     * @param note
     * @return
     */
    public String submitMaintenanceRequest(String nodeID, int assignerID, String note, int priority){
        long currTime = System.currentTimeMillis();
        Timestamp submittedTime = new Timestamp(currTime);
        Timestamp startedTime = new Timestamp(currTime-1);
        Timestamp completedTime = new Timestamp(currTime-1);
        String rID = "Man"+currTime;

        MaintenanceRequest mtRequest = new MaintenanceRequest(rID, nodeID, assignerID, assignerID, note,
                submittedTime, startedTime, completedTime,RequestProgressStatus.TO_DO, priority);

        //dbController.insertRequestIntoView(itRequest);

        maintenanceRequests.put(rID, mtRequest);
        dbController.addMaintenanceRequest(mtRequest);
        activityLogger.logRequestAdd(LoginEntity.getInstance().getCurrentLogin(),mtRequest);
        return rID;
    }

    /**
     *
     * @param requestID
     * @return
     * @throws NullPointerException
     */
    public MaintenanceRequest getMaintenanceRequest(String requestID) throws NullPointerException{
        if(maintenanceRequests.containsKey(requestID)){
            return maintenanceRequests.get(requestID);
        }
        else {
            readAllFromDatabase();
            if(maintenanceRequests.containsKey(requestID)){
                return maintenanceRequests.get(requestID);
            }
            else {
                throw new NullPointerException("Unable to find Maintenance request in the database");
            }
        }
    }

    /**
     * Vastly simplified updating method
     * @param mtRequest
     */
    public void updateMaintenanceRequest(MaintenanceRequest mtRequest){
        maintenanceRequests.replace(mtRequest.getRequestID(),mtRequest);
        dbController.updateMaintenanceRequest(mtRequest);
    }

    /**
     * For submitting ITRequests
     * @param nodeID
     * @param assignerID
     * @param note
     * @return
     */
    public String submitITRequest(String nodeID, int assignerID, String note, ITService itService){
        long currTime = System.currentTimeMillis();
        Timestamp submittedTime = new Timestamp(currTime);
        Timestamp startedTime = new Timestamp(currTime-1);
        Timestamp completedTime = new Timestamp(currTime-1);
        String rID = "ITT"+currTime;

        ITRequest itRequest = new ITRequest(rID, nodeID, assignerID, assignerID, note,
                submittedTime, startedTime, completedTime,RequestProgressStatus.TO_DO, itService);

        //dbController.insertRequestIntoView(itRequest);

        itRequests.put(rID, itRequest);
        dbController.addITRequest(itRequest);
        activityLogger.logRequestAdd(LoginEntity.getInstance().getCurrentLogin(),itRequest);
        return rID;
    }

    /**
     *
     * @param requestID
     * @return
     * @throws NullPointerException
     */
    public ITRequest getITRequest(String requestID) throws NullPointerException{
        if(itRequests.containsKey(requestID)){
            return itRequests.get(requestID);
        }
        else {
            readAllFromDatabase();
            if(itRequests.containsKey(requestID)){
                return itRequests.get(requestID);
            }
            else {
                throw new NullPointerException("Unable to find IT request in the database");
            }
        }
    }

    /**
     * Vastly simplified updating method
     * @param itRequest
     */
    public void updateITRequest(ITRequest itRequest){
        itRequests.replace(itRequest.getRequestID(),itRequest);
        dbController.updateITRequest(itRequest);
    }

    /*
     * Tracking information
     * what we want:
     * Time from IN_PROGRESS to COMPLETE for requests
     * Heatmap of request locations
     * Statistics on interpreter request languages
     * Statistics on all requests (how many of each type were made
     * Statistics on food ordered for stocking purposes
     * common IT request problems
     */

    /**
     * gets mean time to setComplete a request from IN_PROGRESS to DONE
     */
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

    /**
     * gives a frequency historgram for interpreter request langauges
     * @return a linkedlist of number of interpreter languages requested
     */
    public LinkedList<LanguageFrequency> getLanguageFrequency(){
        dbController.getAllInterpreterRequests();
        LinkedList<LanguageFrequency> freq = new LinkedList<>();
        for (InterpreterRequest iR: interpreterRequests.values()){
            //search output list
            boolean languagePresent=false;
            for (int i = 0; i < freq.size(); i++) {
                if(freq.get(i).isLanguage(iR.getLanguage())){
                    freq.get(i).increment();
                    languagePresent = true;
                    break;
                }
            }
            if(!languagePresent) freq.add(new LanguageFrequency(iR.getLanguage(),1));
        }
        Collections.sort(freq, new SortByFrequency());
        return freq;
    }

    /**
     * creates a pie chart displaying the percentage of request types
     * @return a hashmap of Strings and integers for a pie chart
     */
    public ObservableList<PieChart.Data> getRequestDistribution(){
        ObservableList<PieChart.Data> reqs =
                FXCollections.observableArrayList(
                        new PieChart.Data(RequestType.INTERPRETER.toString(),interpreterRequests.size()),
                        new PieChart.Data(RequestType.SECURITY.toString(),securityRequests.size()),
                        new PieChart.Data(RequestType.FOOD.toString(),foodRequests.size()),
                        new PieChart.Data(RequestType.JANITOR.toString(),janitorRequests.size()),
                        new PieChart.Data(RequestType.IT.toString(),itRequests.size()),
                        new PieChart.Data(RequestType.MAINTENANCE.toString(),maintenanceRequests.size()));
        return reqs;
    }
}

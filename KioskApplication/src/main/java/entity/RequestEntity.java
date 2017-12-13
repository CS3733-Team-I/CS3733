package entity;

import database.DatabaseController;
import database.connection.NotFoundException;
import database.objects.*;
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

    private int uRequestID = 0;

    private long meanTimeToComplete;

    private static RequestEntity instance = null;

    private DatabaseController dbController;

    protected RequestEntity(boolean test) {
        interpreterRequests=new HashMap<>();
        securityRequests=new HashMap<>();
        foodRequests=new HashMap<>();
        janitorRequests=new HashMap<>();

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
        //} else if (requestType.equals("Ins")) {
        //} else if (requestType.equals("Out")) {
        } else {
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
        uRequestID--;
        if(requestType.equals(RequestType.INTERPRETER)){
            interpreterRequests.remove(requestID);
            dbController.deleteInterpreterRequest(requestID);
        }
        else if(requestType.equals(RequestType.SECURITY)){
            securityRequests.remove(requestID);
            dbController.deleteSecurityRequest(requestID);
        }
        else if(requestType.equals(RequestType.FOOD)){
            foodRequests.remove(requestID);
            dbController.deleteFoodRequest(requestID);
        }
        else if(requestType.equals(RequestType.JANITOR)){
            janitorRequests.remove(requestID);
            dbController.deleteJanitorRequest(requestID);
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
            InterpreterRequest iR = interpreterRequests.get(requestID);
            iR.setInProgress(completerID);
            dbController.updateInterpreterRequest(iR);
        }
        else if(requestType.equals(RequestType.SECURITY)){
            SecurityRequest sR = securityRequests.get(requestID);
            sR.setInProgress(completerID);
            dbController.updateSecurityRequest(sR);
        }
        else if(requestType.equals(RequestType.FOOD)){
            FoodRequest fR = foodRequests.get(requestID);
            fR.setInProgress(completerID);
            dbController.updateFoodRequest(fR);
        }
        else if(requestType.equals(RequestType.JANITOR)){
            JanitorRequest janitorRequest = janitorRequests.get(requestID);
            janitorRequest.setInProgress(completerID);
            dbController.updateJanitorRequest(janitorRequest);
        }
//        else if(requestType.equals(RequestType"Ins")){
//            System.out.println("In Progress InsideTransportationRequest");
//        }
//        else if(requestType.equals(RequestType"Out")){
//            System.out.println("In Progress OutsideTransportationRequest");
//        }
        else{
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
            InterpreterRequest iR = interpreterRequests.get(requestID);
            iR.setComplete();
            interpreterRequests.replace(requestID,iR);
            dbController.updateInterpreterRequest(iR);
        }
        else if(requestType.equals(RequestType.SECURITY)){
            SecurityRequest sR = securityRequests.get(requestID);
            sR.setComplete();
            securityRequests.replace(requestID, sR);
            dbController.updateSecurityRequest(sR);
        }
        else if(requestType.equals(RequestType.FOOD)){
            FoodRequest fR = foodRequests.get(requestID);
            fR.setComplete();
            foodRequests.replace(requestID, fR);
            dbController.updateFoodRequest(fR);
        }
        else if(requestType.equals(RequestType.JANITOR)){
            JanitorRequest janitorRequest = janitorRequests.get(requestID);
            janitorRequest.setComplete();
            janitorRequests.replace(requestID,janitorRequest);
            dbController.updateJanitorRequest(janitorRequest);
        }
//        else if(requestType.equals(RequestType"Ins")){
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
        }else if(checkRequestType(requestID).equals(RequestType.FOOD)){
            request = getFoodRequest(requestID);
        }else if(checkRequestType(requestID).equals(RequestType.JANITOR)){
            request = getJanitorRequest(requestID);
        }
        else if(checkRequestType(requestID).equals(RequestType.SECURITY)){ //security request
            request = getSecurityRequest(requestID);
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
    public void updateRequest(String requestID, String nodeID, int assignerID, String note,
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
            case JANITOR:
                dbController.updateJanitorRequest((JanitorRequest) oldReq);
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
        uRequestID++;
        interpreterRequests.put(rID, iR);
        dbController.addInterpreterRequest(iR);
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
        //dbController.insertRequestIntoView(sR);
        uRequestID++;
        securityRequests.put(rID, sR);
        dbController.addSecurityRequest(sR);
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

        //dbController.insertRequestIntoView(fR);
        uRequestID++;

        foodRequests.put(rID, fR);
        dbController.addFoodRequest(fR);
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

        //dbController.insertRequestIntoView(janitorRequest);
        uRequestID++;

        janitorRequests.put(rID, janitorRequest);
        dbController.addJanitorRequest(janitorRequest);
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
                        new PieChart.Data(RequestType.JANITOR.toString(),janitorRequests.size()));
        return reqs;
    }
}

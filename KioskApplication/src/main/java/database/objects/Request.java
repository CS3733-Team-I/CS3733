package database.objects;

import database.DatabaseController;
import database.objects.Node;
import utility.RequestProgressStatus;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Date;

import java.sql.Timestamp;

//Class object that creates a 'request' which stores the information of a request
//This object can then be passed to the database as well as to the active requests list
public abstract class Request {
    //Format for requestID: type, nodeID, time, assigner
    protected String requestID;
    private String nodeID;
    private Timestamp submittedTime;
    private String assigner;
    private String note;
    private RequestProgressStatus status;
    private Timestamp completedTime;


    /*public Request(Node location, String assigner, int requestID) {
        this.location = location;
        this.assigner = assigner;
        this.requestID = requestID;
    }*/
    //Use this to generate new requests
    public Request(String nodeID, String assigner, String note) {
        this.nodeID=nodeID;
        this.submittedTime=new Timestamp(System.currentTimeMillis());
        this.assigner=assigner;
        this.note=note;
        this.status=RequestProgressStatus.TO_DO;
        this.completedTime=null;
    }

    //Use to retrieve uncompleted requests
    public Request(String requestID, String nodeID, Timestamp submittedTime, String assigner, String note, RequestProgressStatus status){
        this.requestID=requestID;
        this.nodeID=nodeID;
        this.submittedTime=submittedTime;
        this.assigner=assigner;
        this.note=note;
        this.status=status;
        this.completedTime=null;
    }

    //Use to retrieve completed requests
    public Request(String requestID, String nodeID, Timestamp submittedTime, String assigner, String note, Timestamp completedTime){
        this.requestID=requestID;
        this.nodeID=nodeID;
        this.submittedTime=submittedTime;
        this.assigner=assigner;
        this.note=note;
        this.status=RequestProgressStatus.DONE;
        this.completedTime=completedTime;
    }

    public void updateLocation(String nodeID){
        this.nodeID=nodeID;
    }

    public void updateNote(String newNote){
        this.note=newNote;
    }

    public void inProgress(){
        this.status=RequestProgressStatus.IN_PROGRESS;
    }

    public void complete(){
        this.status=RequestProgressStatus.DONE;
        this.completedTime=new Timestamp(System.currentTimeMillis());
    }

    //Getters for testing
    public String getRequestID() {
        return requestID;
    }
    public String getNodeID(){return this.nodeID;}
    public Timestamp getSubmittedTime() {
        return submittedTime;
    }
    public String getassigner(){
        return this.assigner;
    }
    public String getNote() {
        return note;
    }
    public Timestamp getCompletedTime() {
        return completedTime;
    }
    public RequestProgressStatus getStatus() {
        return status;
    }

    public void setAssigner(String assigner) {
        this.assigner = assigner;
    }

    public void setNodeID(String nodeID) {
        this.nodeID = nodeID;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public void setStatus(RequestProgressStatus status) {
        this.status = status;
    }
}
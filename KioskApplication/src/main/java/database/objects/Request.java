package database.objects;

import utility.Request.RequestProgressStatus;

import java.sql.Timestamp;

//Class object that creates a 'request' which stores the information of a request
//This object can then be passed to the database as well as to the active requests list
public abstract class Request {
    //Format for requestID: type, time, NodeID >36 characters
    protected String requestID;
    private String nodeID;
    private String assigner;
    private String completer;
    private String note;
    private RequestProgressStatus status;
    private Timestamp submittedTime;
    private Timestamp startedTime;
    private Timestamp completedTime;

    //Use this to generate new requests don't put in a nodeID larger than 10
    /*public Request(String nodeID, String assigner, String note) {
        long t = System.currentTimeMillis();
        this.nodeID=nodeID;
        this.submittedTime=new Timestamp(t);
        this.assigner=assigner;
        this.note=note;
        this.status=RequestProgressStatus.TO_DO;
        //TODO: make completed timestamp less hacky, implement a null design pattern possibly
        this.completedTime=new Timestamp(t-1);
        this.requestID=submittedTime.toString()+nodeID;
    }*/

    //Use to retrieve requests
    public Request(String requestID, String nodeID, String assigner,String completer, String note, Timestamp submittedTime, Timestamp startedTime, Timestamp completedTime, RequestProgressStatus status){
        this.requestID=requestID;
        this.nodeID=nodeID;
        this.assigner=assigner;
        this.completer=completer;
        this.note=note;
        this.submittedTime=submittedTime;
        this.startedTime=startedTime;
        this.completedTime=completedTime;
        this.status=status;
    }

    public void updateLocation(String nodeID){
        this.nodeID=nodeID;
    }

    public void updateNote(String newNote){
        this.note=newNote;
    }

    public boolean markInProgress(String completer){
        if(this.status==RequestProgressStatus.TO_DO){
            this.completer=completer;
            this.status=RequestProgressStatus.IN_PROGRESS;
            this.startedTime=new Timestamp(System.currentTimeMillis());
            return true;
        }
        else {
            return false;
        }
    }

    public boolean complete(){
        if (this.status==RequestProgressStatus.IN_PROGRESS){
            this.status=RequestProgressStatus.DONE;
            this.completedTime=new Timestamp(System.currentTimeMillis());
            return true;
        }
        else{
            return false;
        }
    }

        //Getters for testing
    public String getRequestID() {
        return requestID;
    }

    public String getNodeID(){return this.nodeID;}

    public String getAssigner(){
        return this.assigner;
    }

    public String getCompleter() {
        return completer;
    }

    public String getNote() {
        return note;
    }

    public Timestamp getSubmittedTime() {
        return submittedTime;
    }

    public Timestamp getStartedTime() {
        return startedTime;
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

    public void setSubmittedTime(Timestamp submittedTime) {
        this.submittedTime = submittedTime;
    }

    public void setCompletedTime(Timestamp completedTime) {
        this.completedTime = completedTime;
    }
}
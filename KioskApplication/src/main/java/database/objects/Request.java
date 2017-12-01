package database.objects;

import utility.request.RequestProgressStatus;

import java.sql.Timestamp;

//Class object that creates a 'request' which stores the information of a request
//This object can then be passed to the database as well as to the active requests list
public abstract class Request {
    //Format for requestID: type, nodeID, time, assignerID
    protected String requestID;
    private String nodeID;
    private int assignerID;
    private int completerID;
    private String note;
    private RequestProgressStatus status;
    private Timestamp submittedTime;
    private Timestamp startedTime;
    private Timestamp completedTime;

    //Use to retrieve requests
    public Request(String requestID, String nodeID, int assignerID,int completerID, String note, Timestamp submittedTime, Timestamp startedTime, Timestamp completedTime, RequestProgressStatus status){
        this.requestID=requestID;
        this.nodeID=nodeID;
        this.assignerID =assignerID;
        this.completerID =completerID;
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

    public boolean markInProgress(int completerID){
        if(this.status==RequestProgressStatus.TO_DO){
            this.completerID =completerID;
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

    //Returns time from TO_DO to IN_PROGRESS
    public long travelTime(){
        if(status!=RequestProgressStatus.TO_DO){
            return (this.startedTime.getTime()-this.submittedTime.getTime());
        }
        else{
            return 0;
        }
    }

    //returns time from IN_PROGRESS to DONE
    public long timeToComplete(){
        if(status==RequestProgressStatus.DONE){
            return this.completedTime.getTime()-this.startedTime.getTime();
        }
        else{
            return 0;
        }
    }

        //Getters for testing
    public String getRequestID() {
        return requestID;
    }

    public String getNodeID(){return this.nodeID;}

    public int getAssignerID(){
        return this.assignerID;
    }

    public int getCompleterID() {
        return completerID;
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

    public void setAssignerID(int assignerID) {
        this.assignerID = assignerID;
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
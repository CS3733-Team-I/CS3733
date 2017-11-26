package database.objects;

import utility.Request.Language;
import utility.Request.RequestProgressStatus;

import java.sql.Timestamp;

public class SecurityRequest extends Request {
    private int priority;

    //for new Security Requests
    public SecurityRequest(String nodeID, String assigner, String note, int priority) {
        super(nodeID, assigner, note);
        this.priority = priority;
        this.requestID = "Sec" + this.requestID;
    }


    //for retrieving Security Requests
    public SecurityRequest(String requestID, String nodeID, String assignee, String note, Timestamp submittedTime, Timestamp completedTime, RequestProgressStatus status, int priority) {
        super(requestID, nodeID, assignee, note, submittedTime, completedTime, status);
        this.priority = priority;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj==null) return false;

        if(obj.getClass() == this.getClass()){
            SecurityRequest other = (SecurityRequest) obj;
            return(this.requestID.equals(other.getRequestID())) &&
                    this.getNodeID().equals(other.getNodeID())&&
                    this.getAssigner().equals(other.getAssigner())&&
                    this.getNote().equals(other.getNote())&&
                    this.getSubmittedTime().equals(other.getSubmittedTime())&&
                    this.getCompletedTime().equals(other.getCompletedTime())&&
                    this.getStatus()==other.getStatus()&&
                    this.priority==other.getPriority();
        }
        else{
            return false;
        }
    }

    //Getters for testing

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }
}

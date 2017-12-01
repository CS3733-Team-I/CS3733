package database.objects;

import utility.request.RequestProgressStatus;

import java.sql.Timestamp;

public class SecurityRequest extends Request {
    private int priority;

    public SecurityRequest(String requestID, String nodeID, int assignerID, int completerID, String note,
                           Timestamp submittedTime, Timestamp startedTime, Timestamp completedTime,
                           RequestProgressStatus status, int priority) {
        super(requestID, nodeID, assignerID, completerID, note, submittedTime, startedTime, completedTime, status);
        this.priority = priority;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj==null) return false;

        if(obj.getClass() == this.getClass()){
            SecurityRequest other = (SecurityRequest) obj;
            return(this.requestID.equals(other.getRequestID())) &&
                    this.getNodeID().equals(other.getNodeID())&&
                    this.getAssignerID()==other.getAssignerID()&&
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

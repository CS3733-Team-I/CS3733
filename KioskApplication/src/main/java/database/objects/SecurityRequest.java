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
        this.requestID = "Sec" + this.getSubmittedTime().toString() + nodeID;
    }

    //for retrieving uncomplete Security Requests
    public SecurityRequest(String requestID, String nodeID, Timestamp submittedTime, String assignee, String note, RequestProgressStatus status, int priority) {
        super(requestID, nodeID, submittedTime, assignee, note, status);
        this.priority = priority;
    }

    //for retrieving complete Security Requests
    public SecurityRequest(String requestID, String nodeID, Timestamp submittedTime, String assignee, String note, Timestamp completedTime, int priority){
        super(requestID, nodeID, submittedTime, assignee, note, completedTime);
        this.priority = priority;
    }

    //Getters for testing

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }
}

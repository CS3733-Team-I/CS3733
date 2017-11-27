package database.objects;

import utility.Request.Language;
import utility.Request.RequestProgressStatus;

import java.sql.Timestamp;

//Adds the field language
public class InterpreterRequest extends Request {
    private Language language;

    //for retrieving iRs
    public InterpreterRequest(String requestID, String nodeID, String assignee, String completer,
                              String note, Timestamp submittedTime, Timestamp startedTime, Timestamp completedTime,
                              RequestProgressStatus status, Language language) {
        super(requestID, nodeID, assignee, completer, note, submittedTime, startedTime, completedTime, status);
        this.language = language;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj==null) return false;

        if(obj.getClass() == this.getClass()){
            InterpreterRequest other = (InterpreterRequest)obj;
            return(this.requestID.equals(other.getRequestID())) &&
                    this.getNodeID().equals(other.getNodeID())&&
                    this.getAssigner().equals(other.getAssigner())&&
                    this.getNote().equals(other.getNote())&&
                    this.getSubmittedTime().equals(other.getSubmittedTime())&&
                    this.getCompletedTime().equals(other.getCompletedTime())&&
                    this.getStatus()==other.getStatus()&&
                    this.language==other.getLanguage();
        }
        else{
            return false;
        }
    }

    //Getters for testing
    public Language getLanguage() {
        return this.language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }
}
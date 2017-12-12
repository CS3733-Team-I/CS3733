package database.objects;

import utility.request.Language;
import utility.request.RequestProgressStatus;

import java.sql.Timestamp;

//Adds the field language
public class InterpreterRequest extends Request {
    private Language language;

    public InterpreterRequest(String requestID, String nodeID, int assignerID, int completerID,
                              String note, Timestamp submittedTime, Timestamp startedTime, Timestamp completedTime,
                              RequestProgressStatus status, Language language, int uRequestID) {
        super(requestID, nodeID, assignerID, completerID, note, submittedTime, startedTime, completedTime, status, uRequestID);
        this.language = language;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj==null) return false;

        if(obj.getClass() == this.getClass()){
            InterpreterRequest other = (InterpreterRequest)obj;
            return(this.requestID.equals(other.getRequestID())) &&
                    this.getNodeID().equals(other.getNodeID())&&
                    this.getAssignerID()==other.getAssignerID()&&
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
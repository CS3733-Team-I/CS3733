package database.objects;

import utility.Request.Language;
import utility.Request.RequestProgressStatus;

import java.sql.Timestamp;

//Adds the field language
public class InterpreterRequest extends Request {
    private Language language;

    //for new IRs
    public InterpreterRequest(String nodeID, String employee, String note, Language language) {
        super(nodeID, employee, note);
        this.language = language;
        this.requestID = "Int" + this.getSubmittedTime().toString() + nodeID;
    }

    //for retrieving iRs
    public InterpreterRequest(String requestID, String nodeID, String assignee, String note, Timestamp submittedTime, Timestamp completedTime, RequestProgressStatus status, Language language) {
        super(requestID, nodeID, assignee, note, submittedTime, completedTime, status);
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
                    this.getStatus()==other.getStatus()&&
                    this.language==other.getLanguage();//&&
                    //this.getCompletedTime().equals(other.getCompletedTime());
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
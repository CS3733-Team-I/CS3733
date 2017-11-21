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
        this.requestID = "Interpreter" + nodeID + this.getSubmittedTime().toString() + employee;
    }

    //for retrieving uncomplete iRs
    public InterpreterRequest(String requestID, String nodeID, Timestamp submittedTime, String assignee, String note, RequestProgressStatus status, Language language) {
        super(requestID, nodeID, submittedTime, assignee, note, status);
        this.language = language;
    }

    //for retrieving complete iRs
    public InterpreterRequest(String requestID, String nodeID, Timestamp submittedTime, String assignee, String note, Timestamp completedTime, Language language) {
        super(requestID, nodeID, submittedTime, assignee, note, completedTime);
        this.language = language;
    }

    //Getters for testing
    public Language getLanguage() {
        return this.language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }
}
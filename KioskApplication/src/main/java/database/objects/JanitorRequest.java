package database.objects;

import utility.request.RequestProgressStatus;

import java.sql.Timestamp;

public class JanitorRequest extends Request {

    public JanitorRequest(String requestID, String nodeID, int assignerID, int completerID,
                          String note, Timestamp submittedTime, Timestamp startedTime, Timestamp completedTime,
                          RequestProgressStatus status, int uRequestID){
        super(requestID, nodeID, assignerID, completerID, note, submittedTime, startedTime, completedTime, status, uRequestID);
    }
}

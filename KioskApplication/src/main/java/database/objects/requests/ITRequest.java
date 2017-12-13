package database.objects.requests;

import database.objects.Request;
import utility.request.ITService;
import utility.request.RequestProgressStatus;

import java.sql.Timestamp;

public class ITRequest extends Request {
    private ITService itService;

    public ITRequest(String requestID, String nodeID, int assignerID, int completerID,
                     String note, Timestamp submittedTime, Timestamp startedTime, Timestamp completedTime,
                     RequestProgressStatus status, ITService itService){
        super(requestID, nodeID, assignerID, completerID, note, submittedTime, startedTime, completedTime, status);
        this.itService = itService;
    }

    public ITService getItService() {
        return itService;
    }
}

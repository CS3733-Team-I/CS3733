package database.objects.requests;

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

    public void setItService(ITService itService) {
        this.itService = itService;
    }

    @Override
    public boolean equals(Object obj){
        if(obj==null) return false;

        if(obj.getClass() == this.getClass()){
            ITRequest other = (ITRequest) obj;
            return(requestID.equals(other.getRequestID())&&
                    getNodeID().equals(other.getNodeID())&&
                    getAssignerID()==other.getAssignerID())&&
                    getCompleterID()==other.getCompleterID()&&
                    getNote().equals(other.getNote())&&
                    getSubmittedTime().equals(other.getSubmittedTime())&&
                    getCompletedTime().equals(other.getCompletedTime())&&
                    getStatus()==other.getStatus()&&
                    getItService()==other.getItService();
        }
        else{
            return false;
        }
    }
}

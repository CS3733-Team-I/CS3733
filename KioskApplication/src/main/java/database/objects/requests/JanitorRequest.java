package database.objects.requests;

import utility.request.RequestProgressStatus;

import java.sql.Timestamp;

public class JanitorRequest extends Request {

    public JanitorRequest(String requestID, String nodeID, int assignerID, int completerID,
                          String note, Timestamp submittedTime, Timestamp startedTime, Timestamp completedTime,
                          RequestProgressStatus status){
        super(requestID, nodeID, assignerID, completerID, note, submittedTime, startedTime, completedTime, status);
    }

    @Override
    public boolean equals(Object obj){
        if(obj==null) return false;

        if(obj.getClass() == this.getClass()){
            JanitorRequest other = (JanitorRequest) obj;
            return(requestID.equals(other.getRequestID())&&
                    getNodeID().equals(other.getNodeID())&&
                    getAssignerID()==other.getAssignerID())&&
                    getCompleterID()==other.getCompleterID()&&
                    getNote().equals(other.getNote())&&
                    getSubmittedTime().equals(other.getSubmittedTime())&&
                    getCompletedTime().equals(other.getCompletedTime())&&
                    getStatus()==other.getStatus();
        }
        else{
            return false;
        }
    }
}

package database.objects;

import utility.request.RequestProgressStatus;

import java.sql.Timestamp;

public class FoodRequest extends Request{
    private String restuarant;
    private String order;
    private String deliveryDate;

    public FoodRequest(String requestID, String nodeID, int assignerID, int completerID, String note,
                       Timestamp submittedTime, Timestamp startedTime, Timestamp completedTime,
                       RequestProgressStatus status, String restuarant, String order, String deliveryDate){
        super(requestID, nodeID, assignerID, completerID, note, submittedTime, startedTime, completedTime, status);
        this.restuarant = restuarant;
        this.order = order;
        this.deliveryDate = deliveryDate;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj==null) return false;

        if(obj.getClass() == this.getClass()){
            FoodRequest other = (FoodRequest) obj;
            return(this.requestID.equals(other.getRequestID())) &&
                    this.getNodeID().equals(other.getNodeID())&&
                    this.getAssignerID()==other.getAssignerID()&&
                    this.getNote().equals(other.getNote())&&
                    this.getSubmittedTime().equals(other.getSubmittedTime())&&
                    this.getCompletedTime().equals(other.getCompletedTime())&&
                    this.getStatus()==other.getStatus()&&
                    this.restuarant==other.getRestuarant()&&
                    this.order==other.getOrder()&&
                    this.deliveryDate==other.getDeliveryDate();
        }
        else{
            return false;
        }
    }

    public String getRestuarant() {
        return restuarant;
    }

    public void setRestuarant(String restuarant) {
        this.restuarant = restuarant;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public String getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(String deliveryDate) {
        this.deliveryDate = deliveryDate;
    }
}

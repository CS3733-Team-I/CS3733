package database.objects;

import utility.request.RequestProgressStatus;

import java.sql.Timestamp;

public class FoodRequest extends Request{
    private String restaurantID;
    private Timestamp deliveryDate;

    public FoodRequest(String requestID, String nodeID, int assignerID, int completerID, String note,
                       Timestamp submittedTime, Timestamp startedTime, Timestamp completedTime,
                       RequestProgressStatus status, String restaurantID, Timestamp deliveryDate, int uRequestID){
        super(requestID, nodeID, assignerID, completerID, note, submittedTime, startedTime, completedTime, status, uRequestID);
        this.restaurantID = restaurantID;
        this.deliveryDate = deliveryDate;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj==null) return false;

        if(obj.getClass() == this.getClass()){
            FoodRequest other = (FoodRequest) obj;
            return(this.requestID.equals(other.getRequestID())) &&
                    this.getNodeID().equals(other.getNodeID())&&
                    this.getAssignerID() == other.getAssignerID()&&
                    this.getNote().equals(other.getNote())&&
                    this.getSubmittedTime().equals(other.getSubmittedTime())&&
                    this.getCompletedTime().equals(other.getCompletedTime())&&
                    this.getStatus().equals(other.getStatus())&&
                    this.restaurantID.equals(other.getRestaurantID()) &&
                    this.deliveryDate.equals(other.getDeliveryDate());
        }
        else{
            return false;
        }
    }

    public String getRestaurantID() {
        return restaurantID;
    }

    public void setRestaurantID(String restaurantID) {
        this.restaurantID = restaurantID;
    }

    public Timestamp getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(Timestamp deliveryDate) {
        this.deliveryDate = deliveryDate;
    }
}

package database.objects.ActivityLog;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;

import java.sql.Timestamp;

public class ActivityLog extends RecursiveTreeObject<ActivityLog> {
    int activityID;
    private Timestamp time;
    private ActivityType activityType;
    private int employeeID;
    private String details;

    public ActivityLog(Timestamp time, ActivityType activityType, int employeeID, String details){
        this.time=time;
        this.activityType=activityType;
        this.employeeID=employeeID;
        this.details=details;
    }

    public int getActivityID() {
        return activityID;
    }

    public void setActivityID(int activityID) {
        this.activityID = activityID;
    }

    public Timestamp getTime() {
        return time;
    }

    public ActivityType getActivityType() {
        return activityType;
    }

    public int getEmployeeID() {
        return employeeID;
    }

    public String getDetails() {
        return details;
    }
}

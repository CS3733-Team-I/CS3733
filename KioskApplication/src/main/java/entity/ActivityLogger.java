package entity;

import database.DatabaseController;
import database.objects.*;
import database.objects.ActivityLog.ActivityLog;
import database.objects.ActivityLog.ActivityType;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.LinkedList;

public class ActivityLogger {
    private DatabaseController databaseController;
    private HashMap<Integer,ActivityLog> logHashMap;

    private static class ActivityLoggerSingletonHelper{
        private static final ActivityLogger instance = new ActivityLogger();
    }

    public static ActivityLogger getInstance(){
        return ActivityLoggerSingletonHelper.instance;
    }

    private ActivityLogger(){
        databaseController = DatabaseController.getInstance();
        logHashMap = new HashMap<>();
        readAllFromDatabase();
    }

    /**
     * Fills the hashmap for the ActivityLoggerSingleton
     */
    private void readAllFromDatabase(){
        logHashMap.clear();
        for (ActivityLog log : databaseController.getAllActivityLogs()){
            logHashMap.put(log.getActivityID(),log);
        }
    }

    /**
     * For getting all activity logs from the singleton's hashmap
     * @return
     */
    public LinkedList<ActivityLog> getAllLogs(){
        LinkedList<ActivityLog> logList = new LinkedList<>();
        for(ActivityLog log: logHashMap.values()){
            logList.add(log);
        }
        return logList;
    }

    /**
     * For tracking Request submission
     * @param request
     */
    public void logRequestAdd(IEmployee currentLogin, Request request){
        Timestamp time = new Timestamp(System.currentTimeMillis());
        String details = "Submitted Request: "+request.getRequestID();
        //TODO: Make string constructor for details
        details=details+" at Node: "+request.getNodeID();
        ActivityLog log = new ActivityLog(time, ActivityType.REQUEST, currentLogin.getID(), details);
        submitLog(log);
    }

    /**
     * For tracking request changes
     * @param newRequest
     * @param oldRequest
     */
    public void logRequestChange(IEmployee currentLogin, Request newRequest, Request oldRequest){
        Timestamp time = new Timestamp(System.currentTimeMillis());
        String details = "Updated Request: "+newRequest.getRequestID();
        //TODO: Make string constructor for details
        ActivityLog log = new ActivityLog(time, ActivityType.REQUEST, currentLogin.getID(), details);
        submitLog(log);
    }

    /**
     * For tracking request deletions
     * @param request
     */
    public void logRequestDelete(IEmployee currentLogin, Request request){
        Timestamp time = new Timestamp(System.currentTimeMillis());
        String details = "Deleted Request: "+request.getRequestID();
        //TODO: Make string constructor for details
        ActivityLog log = new ActivityLog(time, ActivityType.REQUEST, currentLogin.getID(), details);
        submitLog(log);
    }

    public void logEmployeeAdd(IEmployee currentLogin, Employee employee){
        String details = "Added Employee: "+employee.getID();
        Timestamp time = new Timestamp(System.currentTimeMillis());
        ActivityLog log = new ActivityLog(time, ActivityType.EMPLOYEE, currentLogin.getID(), details);
        submitLog(log);
    }

    public void logEmployeeUpdate(IEmployee currentLogin, Employee newEmployee, Employee oldEmployee){
        String details = "Updated Employee: "+newEmployee.getID();
        Timestamp time = new Timestamp(System.currentTimeMillis());
        ActivityLog log = new ActivityLog(time, ActivityType.EMPLOYEE, currentLogin.getID(), details);
        submitLog(log);
    }

    public void logEmployeeDelete(IEmployee currentLogin, Employee employee){
        String details = "Deleted Employee: "+employee.getID();
        Timestamp time = new Timestamp(System.currentTimeMillis());
        ActivityLog log = new ActivityLog(time, ActivityType.EMPLOYEE, currentLogin.getID(), details);
        submitLog(log);
    }

    public void logLogin(IEmployee currentLogin){
        String details = "Logged in";
        long currentTime = System.currentTimeMillis();
        Timestamp time = new Timestamp(currentTime);
        ActivityLog log = new ActivityLog(time, ActivityType.LOGIN, currentLogin.getID(), details);
        submitLog(log);
    }

    public void logLogout(IEmployee currentLogin){

    }

    public void logNodeAdd(IEmployee currentLogin, Node node){
        String details = "Added Node: "+node.getNodeID();
        Timestamp time = new Timestamp(System.currentTimeMillis());
        ActivityLog log = new ActivityLog(time, ActivityType.MAP, currentLogin.getID(), details);
        submitLog(log);
    }

    public void logNodeUpdate(IEmployee currentLogin, Node node){
        String details = "Updated node: "+node.getNodeID();
        Timestamp time = new Timestamp(System.currentTimeMillis());
        ActivityLog log = new ActivityLog(time, ActivityType.MAP, currentLogin.getID(), details);
        submitLog(log);
    }

    public void logNodeDelete(IEmployee currentLogin, Node node){
        String details = "Deleted Node: "+node.getNodeID();
        Timestamp time = new Timestamp(System.currentTimeMillis());
        ActivityLog log = new ActivityLog(time, ActivityType.MAP, currentLogin.getID(), details);
        submitLog(log);
    }

    public void logEdgeAdd(IEmployee currentLogin, Edge edge){
        String details = "Added Edge: "+edge.getEdgeID();
        Timestamp time = new Timestamp(System.currentTimeMillis());
        ActivityLog log = new ActivityLog(time, ActivityType.MAP, currentLogin.getID(), details);
        submitLog(log);
    }

    public void logEdgeUpdate(IEmployee currentLogin, Edge edge){
        String details = "Updated Edge: "+edge.getEdgeID();
        Timestamp time = new Timestamp(System.currentTimeMillis());
        ActivityLog log = new ActivityLog(time, ActivityType.MAP, currentLogin.getID(), details);
        submitLog(log);
    }

    public void logEdgeDelete(IEmployee currentLogin, Edge edge){
        String details = "Deleted Edge: "+edge.getEdgeID();
        Timestamp time = new Timestamp(System.currentTimeMillis());
        ActivityLog log = new ActivityLog(time, ActivityType.MAP, currentLogin.getID(), details);
        submitLog(log);
    }

    /**
     * logs when the pathfinding search algorithm is changed
     * TODO: encapsulate Beam width changes
     * @param searchAlgorithm
     */
    public void logSearchAlgorithmChanged(IEmployee currentLogin, String searchAlgorithm){
        String details = "Search Algorithm changed to: "+searchAlgorithm;
        Timestamp time = new Timestamp(System.currentTimeMillis());
        ActivityLog log = new ActivityLog(time, ActivityType.SETTINGS, currentLogin.getID(), details);
        submitLog(log);
    }

    public void logDefaultNodeChanged(IEmployee currentLogin, Node newNode, Node oldNode){
        String details = "Kiosk Location changed to: "+newNode.getNodeID()+" from: "+oldNode.getNodeID();
        Timestamp time = new Timestamp(System.currentTimeMillis());
        ActivityLog log = new ActivityLog(time, ActivityType.SETTINGS, currentLogin.getID(), details);
        submitLog(log);
    }

    /**
     * Adds the ActivityLog to the database and puts it in the hashmap
     * @param log
     */
    private void submitLog(ActivityLog log){
        int logID = databaseController.addActivityLog(log);
        log.setActivityID(logID);
        logHashMap.put(log.getActivityID(),log);
    }
}

package entity;

import database.DatabaseController;
import database.objects.ActivityLog.ActivityLog;
import database.objects.ActivityLog.ActivityType;
import database.objects.Edge;
import database.objects.Employee;
import database.objects.Node;
import database.objects.Request;
import database.utility.DatabaseException;
import database.utility.DatabaseExceptionType;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.LinkedList;

public class ActivityLogger {
    private LoginEntity loginEntity;
    private DatabaseController databaseController;
    private HashMap<Integer,ActivityLog> logHashMap;

    private static class ActivityLoggerSingletonHelper{
        private static final ActivityLogger instance = new ActivityLogger();
    }

    public static ActivityLogger getInstance(){
        return ActivityLoggerSingletonHelper.instance;
    }

    private ActivityLogger(){
        loginEntity=LoginEntity.getInstance();
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
    public void logRequestAdd(Request request){
        Timestamp time = new Timestamp(System.currentTimeMillis());
        String details = "Submitted Request: "+request.getRequestID();
        //TODO: Make string constructor for details
        details=details+" at Node: "+request.getNodeID();
        ActivityLog log = new ActivityLog(time, ActivityType.REQUEST, loginEntity.getCurrentLoginID(), details);
        submitLog(log);
    }

    /**
     * For tracking request changes
     * @param newRequest
     * @param oldRequest
     */
    public void logRequestChange(Request newRequest, Request oldRequest){
        Timestamp time = new Timestamp(System.currentTimeMillis());
        String details = "Updated Request: "+newRequest.getRequestID();
        //TODO: Make string constructor for details
        ActivityLog log = new ActivityLog(time, ActivityType.REQUEST, loginEntity.getCurrentLoginID(), details);
        submitLog(log);
    }

    /**
     * For tracking request deletions
     * @param request
     */
    public void logRequestDelete(Request request){
        Timestamp time = new Timestamp(System.currentTimeMillis());
        String details = "Deleted Request: "+request.getRequestID();
        //TODO: Make string constructor for details
        ActivityLog log = new ActivityLog(time, ActivityType.REQUEST, loginEntity.getCurrentLoginID(), details);
        submitLog(log);
    }

    public void logEmployeeAdd(Employee employee){
        String details = "Added Employee: "+employee.getID();
        Timestamp time = new Timestamp(System.currentTimeMillis());
        ActivityLog log = new ActivityLog(time, ActivityType.EMPLOYEE, loginEntity.getCurrentLoginID(), details);
        submitLog(log);
    }

    public void logEmployeeUpdate(Employee newEmployee, Employee oldEmployee){
        String details = "";
        Timestamp time = new Timestamp(System.currentTimeMillis());
        ActivityLog log = new ActivityLog(time, ActivityType.EMPLOYEE, loginEntity.getCurrentLoginID(), details);
        submitLog(log);
    }

    public void logEmployeeDelete(Employee employee){
        String details = "";
        Timestamp time = new Timestamp(System.currentTimeMillis());
        ActivityLog log = new ActivityLog(time, ActivityType.EMPLOYEE, loginEntity.getCurrentLoginID(), details);
        submitLog(log);
    }

    public void logNodeAdd(Node node){
        String details = "";
        Timestamp time = new Timestamp(System.currentTimeMillis());
        ActivityLog log = new ActivityLog(time, ActivityType.MAP, loginEntity.getCurrentLoginID(), details);
        submitLog(log);
    }

    public void logNodeUpdate(Node newNode,Node oldNode){
        String details = "";
        Timestamp time = new Timestamp(System.currentTimeMillis());
        ActivityLog log = new ActivityLog(time, ActivityType.MAP, loginEntity.getCurrentLoginID(), details);
        submitLog(log);
    }

    public void logNodeDelete(Node node){
        String details = "";
        Timestamp time = new Timestamp(System.currentTimeMillis());
        ActivityLog log = new ActivityLog(time, ActivityType.MAP, loginEntity.getCurrentLoginID(), details);
        submitLog(log);
    }

    public void logEdgeAdd(Edge edge){
        String details = "";
        Timestamp time = new Timestamp(System.currentTimeMillis());
        ActivityLog log = new ActivityLog(time, ActivityType.MAP, loginEntity.getCurrentLoginID(), details);
        submitLog(log);
    }

    public void logEdgeUpdate(Edge newEdge, Edge oldEdge){
        String details = "";
        Timestamp time = new Timestamp(System.currentTimeMillis());
        ActivityLog log = new ActivityLog(time, ActivityType.MAP, loginEntity.getCurrentLoginID(), details);
        submitLog(log);
    }

    public void logEdgeDelete(Edge edge){
        String details = "";
        Timestamp time = new Timestamp(System.currentTimeMillis());
        ActivityLog log = new ActivityLog(time, ActivityType.MAP, loginEntity.getCurrentLoginID(), details);
        submitLog(log);
    }

    /**
     * logs when the pathfinding search algorithm is changed
     * TODO: encapsulate Beam width changes
     * @param searchAlgorithm
     */
    public void logSearchAlgorithmChanged(String searchAlgorithm){
        String details = "";
        Timestamp time = new Timestamp(System.currentTimeMillis());
        ActivityLog log = new ActivityLog(time, ActivityType.SETTINGS, loginEntity.getCurrentLoginID(), details);
        submitLog(log);
    }

    public void logDefaultNodeChanged(Node newNode, Node oldNode){
        String details = "";
        Timestamp time = new Timestamp(System.currentTimeMillis());
        ActivityLog log = new ActivityLog(time, ActivityType.SETTINGS, loginEntity.getCurrentLoginID(), details);
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

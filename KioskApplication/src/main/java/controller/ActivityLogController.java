package controller;

import database.objects.ActivityLog.ActivityLog;
import database.objects.Employee;
import entity.ActivityLogger;
import entity.LoginEntity;
import entity.SystemSettings;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.fxml.FXML;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeSortMode;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;

import java.util.HashMap;
import java.util.LinkedList;

public class ActivityLogController {
    @FXML private TreeTableView<ActivityLog> logView;
    private final TreeItem<ActivityLog> root = new TreeItem<>();

    private SystemSettings systemSettings;
    private ActivityLogger activityLogger;

    private HashMap<Integer,String> usernameHashMap;

    public ActivityLogController(){
        activityLogger = ActivityLogger.getInstance();
        systemSettings = SystemSettings.getInstance();
        usernameHashMap = new HashMap<>();
    }

    @FXML
    public void initialize(){
        root.setExpanded(true);
        // Setup for table columns
        TreeTableColumn<ActivityLog,String> timeColumn = new TreeTableColumn<>(systemSettings.getResourceBundle().getString("activity.timestamp"));
        timeColumn.setResizable(true);
        timeColumn.setPrefWidth(80);
        timeColumn.setCellValueFactory(
                (TreeTableColumn.CellDataFeatures<ActivityLog,String> param)->
                        new ReadOnlyStringWrapper(param.getValue().getValue().getTime().toString())
        );
        TreeTableColumn<ActivityLog,String> activityTypeColumn = new TreeTableColumn<>(systemSettings.getResourceBundle().getString("activity.activity"));
        activityTypeColumn.setResizable(true);
        activityTypeColumn.setPrefWidth(60);
        activityTypeColumn.setCellValueFactory(
                (TreeTableColumn.CellDataFeatures<ActivityLog,String> param)->
                        new ReadOnlyStringWrapper(param.getValue().getValue().getActivityType().toString())
        );
        TreeTableColumn<ActivityLog,String> employeeColumn = new TreeTableColumn<>(systemSettings.getResourceBundle().getString("activity.employee"));
        employeeColumn.setResizable(true);
        employeeColumn.setPrefWidth(100);
        employeeColumn.setCellValueFactory(
                (TreeTableColumn.CellDataFeatures<ActivityLog,String> param)->
                        new ReadOnlyStringWrapper(usernameHashMap.get(param.getValue().getValue().getEmployeeID()))
        );
        TreeTableColumn<ActivityLog,String> detailsColumn = new TreeTableColumn<>(systemSettings.getResourceBundle().getString("activity.details"));
        detailsColumn.setResizable(true);
        detailsColumn.setPrefWidth(200);
        detailsColumn.setCellValueFactory(
                (TreeTableColumn.CellDataFeatures<ActivityLog,String> param)->
                        new ReadOnlyStringWrapper(param.getValue().getValue().getDetails())
        );
        logView.getColumns().setAll(timeColumn,activityTypeColumn,employeeColumn,detailsColumn);
        logView.setRoot(root);
        logView.setShowRoot(false);
        refreshLogs();
        activityLogger.addObserver(((o, arg) -> {
            refreshLogs();
        }));
    }

    public void refreshLogs(){
        root.getChildren().clear();
        LinkedList<ActivityLog> logs = activityLogger.getAllLogs();
        logs.stream().forEach((log) -> {
            root.getChildren().add(new TreeItem<ActivityLog>(log));
        });
        usernameHashMap.clear();
        for(Employee employee:LoginEntity.getInstance().getAllLogins()){
            usernameHashMap.put(employee.getID(),employee.getUsername());
        }
    }
}

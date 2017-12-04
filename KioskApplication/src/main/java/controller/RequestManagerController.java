package controller;

import com.jfoenix.controls.*;
import controller.map.MapController;
import database.objects.Edge;
import database.objects.Request;
import entity.LoginEntity;
import entity.MapEntity;
import entity.RequestEntity;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import utility.ApplicationScreen;
import utility.KioskPermission;
import utility.node.NodeFloor;
import utility.request.RequestProgressStatus;
import utility.request.RequestType;

import java.io.IOException;
import java.util.LinkedList;

public class RequestManagerController extends ScreenController {


    LoginEntity l;

    RequestEntity r;

@FXML private JFXListView<String> activeRequests;
    @FXML private Label totalRequests;
    @FXML private TextField txtID;
    @FXML private JFXButton completeButton;
    @FXML private JFXPopup popup;
    @FXML private HBox row8;
    @FXML private HBox row9;
    //filter buttons
    @FXML private JFXCheckBox foodFilter;
    @FXML private JFXCheckBox janitorFilter;
    @FXML private JFXCheckBox securityFilter;
    @FXML private JFXCheckBox interpreterFilter;
    @FXML private JFXCheckBox maintenanceFilter;
    @FXML private JFXCheckBox itFilter;
    @FXML private JFXCheckBox transportationFilter;

    public RequestManagerController(MainWindowController parent, MapController map) {
        super(parent, map);
        r = RequestEntity.getInstance();
        l = LoginEntity.getInstance();
        r.readAllFromDatabase();
    }

    /**
     * When an employee is logged in this mehtod checks to see the employee Request Type
     * it takes that information and filters out the requests to show relevant requests
     */
    @FXML
    public void setup(){
        RequestType employeeType = l.getServiceAbility(l.getUsername());
        if(l.getCurrentPermission().equals(KioskPermission.EMPLOYEE) && !employeeType.equals(RequestType.GENERAL)){
            foodFilter.setSelected(false);
            foodFilter.setDisable(true);
            janitorFilter.setSelected(false);
            janitorFilter.setDisable(true);
            securityFilter.setSelected(false);
            securityFilter.setDisable(true);
            interpreterFilter.setSelected(false);
            interpreterFilter.setDisable(true);
            maintenanceFilter.setSelected(false);
            maintenanceFilter.setDisable(true);
            itFilter.setSelected(false);
            itFilter.setDisable(true);
            transportationFilter.setSelected(false);
            transportationFilter.setDisable(true);
            switch (employeeType){
                case FOOD:
                    foodFilter.setSelected(true);
                    break;
                case SECURITY:
                    securityFilter.setSelected(true);
                    break;
                case INTERPRETER:
                    interpreterFilter.setSelected(true);
                    break;
                case JANITOR:
                    janitorFilter.setSelected(true);
                    break;
            }
        }
    }

    /**
     * unopened request button. Displays all of the new requests
     */
    @FXML
    void newRequests(){
        buttonAction(RequestProgressStatus.TO_DO);
    }

    /**
     * in Progress request button. Displays all of the current requests
     */
    @FXML
    void inProgressRequests(){
        buttonAction(RequestProgressStatus.IN_PROGRESS);
    }

    /**
     * Completed request button. Displays all of the finished requests
     */
    @FXML
    void doneRequests(){
        buttonAction(RequestProgressStatus.DONE);
    }

    /**
     * Generic method that updates list of requests
     * @param status RequestProgressStatus is passed through to determine which requests to display
     */
    @FXML
    void buttonAction(RequestProgressStatus status){
        setup();
        buttonSetupt(status);
        LinkedList<Request> allRequests = filterRequests();
        showRequests(status, allRequests);
    }

    /**
     * Displays buttons on the sidebar to assign requests, mark as complete, and delete requests
     * @param status RequestProgressStatus is passed through to determine which requests to display
     */
    private void buttonSetupt(RequestProgressStatus status) {
        row8.getChildren().clear();
        row9.getChildren().clear();
        //Checks to see when a cell in the ListView, activeRequests, is selected
        activeRequests.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                // Your action here
                System.out.println("Selected item: " + newValue);
                row8.getChildren().clear();
                row9.getChildren().clear();
                String requestID = newValue;
                JFXButton statusUpdater;
                if(!l.getCurrentPermission().equals(KioskPermission.EMPLOYEE)){ //Admin or super
                    switch (status){
                        case TO_DO:
                            ObservableList<String> listOfEmployees = FXCollections.observableArrayList();
                            listOfEmployees.addAll(l.getAllEmployeeType(r.checkRequestType(requestID)));
                            JFXComboBox employees = new JFXComboBox(listOfEmployees);
                            employees.setPromptText("Select Employee");
                            row8.getChildren().add(employees);

                            statusUpdater = new JFXButton("Assign");
                            statusUpdater.setOnAction(new EventHandler<ActionEvent>() {
                                @Override
                                public void handle(ActionEvent e) {
                                    r.markInProgress((String) employees.getValue(),requestID);
                                    newRequests();
                                }
                            });
                            row9.getChildren().add(statusUpdater);
                            break;
                        //Admins and Supers can't complete a request

                        case DONE:
                            statusUpdater = new JFXButton("Delete");
                            statusUpdater.setOnAction(new EventHandler<ActionEvent>() {
                                @Override
                                public void handle(ActionEvent e) {
                                    r.deleteRequest(requestID);
                                    doneRequests();
                                }
                            });
                            row9.getChildren().add(statusUpdater);
                            break;
                    }
                }else{
                    switch (status){
                        case TO_DO:
                            statusUpdater = new JFXButton("Assign Me");
                            statusUpdater.setOnAction(new EventHandler<ActionEvent>() {
                                @Override
                                public void handle(ActionEvent e) {
                                    r.markInProgress(l.getUserID(),requestID);
                                    newRequests();
                                }
                            });
                            row9.getChildren().add(statusUpdater);
                            break;
                        case IN_PROGRESS:
                            statusUpdater = new JFXButton("Completed");
                            statusUpdater.setOnAction(new EventHandler<ActionEvent>() {
                                @Override
                                public void handle(ActionEvent e) {
                                    r.completeRequest(requestID);
                                    inProgressRequests();
                                }
                            });
                            row9.getChildren().add(statusUpdater);
                            break;
                    }
                }
            }
        });
    }

    /**
     * Checks the checkboxes to see what filters to add.
     * filters by request type, but must press a button on
     * the sidebar to see the results of this method
     * @return the list of requests to be displayed
     */
    @FXML
    LinkedList<Request> filterRequests() {
        r.readAllFromDatabase();
        LinkedList<Request> allRequests = new LinkedList<Request>();
        if (securityFilter.isSelected()) {
            for (Request sR : r.getAllSecurity()) {
                allRequests.add(sR);
            }
        }
        if (interpreterFilter.isSelected()) {
            for (Request iR : r.getAllinterpters()) {
                allRequests.add(iR);
            }
        }
        return allRequests;
    }

    /**
     * Creates a list of request IDs and displays them in the ListView activeRequests
     * @param status RequestProgressStatus so the method knows which requests it is displaying
     * @param allRequests the list from which the method will filter to display a list of requestIDs
     */
    private void showRequests(RequestProgressStatus status, LinkedList<Request> allRequests) {
        activeRequests.setItems(null);
        ObservableList<String> requestids = FXCollections.observableArrayList();
        LinkedList<Request> requests = r.filterByStatus(allRequests,status);
        for (int i = 0; i < requests.size(); i++) {
            String id = requests.get(i).getRequestID();
            requestids.add(id);
        }
        activeRequests.setItems(requestids);
    }

    /**
     * Creates what goes into the popup when a listview cell is selected
     * @param requestID To determine which request to display the information of
     */
    public void initializePopup(String requestID){
        Request request = r.getRequest(requestID);
        Label id = new Label(requestID);
        String location = MapEntity.getInstance().getNode(request.getNodeID()).getLongName();
        Label employee = new Label("Employee: ");
        Label assigner = new Label(request.getAssigner()); //Some reason this returns more than needed
        Label typeOfRequest = new Label(r.checkRequestType(requestID).toString());
        Label locationOfRequest = new Label(location);
        Label extraField;
        RequestType RT = r.checkRequestType(requestID);
        switch (RT){
            case INTERPRETER:
                String language = r.getInterpreterRequest(requestID).getLanguage().toString();
                extraField = new Label("Language: "+language);
                break;
            default: //security
                int priority = r.getSecurityRequest(requestID).getPriority();
                extraField = new Label("Priority: "+ priority);
                break;
        }
        VBox vbox = new VBox(id,employee,assigner,typeOfRequest,locationOfRequest, extraField);

        popup = new JFXPopup(vbox);
    }

    /**
     * Method to display popup information when a list view cell is selected
     * @param event the mouse click on an ID triggers this method
     */
    @FXML
    public void displayInfo(MouseEvent event){
        String requestID = activeRequests.getSelectionModel().getSelectedItem();
        initializePopup(requestID); //Don't like that this is here
        popup.show(activeRequests,JFXPopup.PopupVPosition.TOP, JFXPopup.PopupHPosition.LEFT, event.getX(),event.getY());
    }

    /**
     * opens the reports pop up window to display the graphs
     * @throws IOException
     */
    @FXML
    void showReports() throws IOException{
        getParent().openRequestTrackingTable();
    }

    /**
     * sets RequestManagerView as fxml file for this controller
     * @return displays the request Manager
     */
    @Override
    public javafx.scene.Node getContentView() {
        if (contentView == null) {
            contentView = loadView("/view/RequestManagerView.fxml");
        }

        newRequests();

        return contentView;
    }

    /**
     * reads requests from a database
     * @throws IOException
     */
    @FXML
    public void refreshRequests() throws IOException {
        r.readAllFromDatabase();
    }

    @Override
    public void onMapLocationClicked(javafx.scene.input.MouseEvent e, Point2D location) { }

    @Override
    public void onMapNodeClicked(database.objects.Node node) { }

    @Override
    public void onMapEdgeClicked(Edge edge) { }

    @Override
    public void onMapFloorChanged(NodeFloor floor) { }

    @Override
    public void resetScreen() {
        getMapController().setAnchor(0,500,0,0);
        getMapController().setPath(null);
        getMapController().reloadDisplay();

        // Set default nodes/edges visibility
        getMapController().setNodesVisible(true);
        getMapController().setEdgesVisible(false);

        // Set if the options box is visible
        getMapController().setOptionsBoxVisible(false);
    }
}

package controller;

import com.jfoenix.controls.*;
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
//    @FXML private JFXListView<VBox> activeRequests;
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

    @FXML
    void viewRequests() throws IOException {
        System.out.println("request Manager Pressed\n");
        getParent().switchToScreen(ApplicationScreen.REQUEST_MANAGER);
    }

    @FXML
    void newRequests(){
        buttonAction(RequestProgressStatus.TO_DO);
    }

    @FXML
    void inProgressRequests(){
        buttonAction(RequestProgressStatus.IN_PROGRESS);
    }

    @FXML
    void doneRequests(){
        buttonAction(RequestProgressStatus.DONE);
    }

    @FXML
    void buttonAction(RequestProgressStatus status){
        setup();
        buttonSetupt(status);
        LinkedList<Request> allRequests = filterRequests();
        showRequests(status, allRequests);
    }

    private void buttonSetupt(RequestProgressStatus status) {
        row8.getChildren().clear();
        row9.getChildren().clear();
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
//                case IN_PROGRESS:
//                    statusUpdater = new JFXButton("Complete");
//                    statusUpdater.setOnAction(new EventHandler<ActionEvent>() {
//                        @Override
//                        public void handle(ActionEvent e) {
//                            r.completeRequest(requestID);
//                        }
//                    });
//                    row9.getChildren().add(statusUpdater);
//                    break;
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

                        //Employees can't delete requests after they are complete
//                case DONE:
//                    break;
                    }
                }
            }
        });
    }

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

    public void initializePopup(String requestID){
        Request request = r.getRequest(requestID);
        Label id = new Label(requestID);
        String location = MapEntity.getInstance().getNode(request.getNodeID()).getLongName();
        Label employee = new Label("Employee: ");
        Label assigner = new Label(request.getAssigner()); //Some reason this returns more than needed
        Label typeOfRequest = new Label(r.checkRequestType(requestID).toString());
        Label locationOfRequest = new Label(location);
        VBox vbox = new VBox(id,employee,assigner,typeOfRequest,locationOfRequest);
        popup = new JFXPopup(vbox);
    }

    @FXML
    public void displayInfo(MouseEvent event){
        String requestID = activeRequests.getSelectionModel().getSelectedItem();
        initializePopup(requestID);
        popup.show(activeRequests,JFXPopup.PopupVPosition.TOP, JFXPopup.PopupHPosition.LEFT, event.getX(),event.getY());
    }

    @FXML
    void showReports() throws IOException{
        getParent().openRequestTrackingTable();
    }

    @Override
    public javafx.scene.Node getContentView() {
        if (contentView == null) {
            contentView = loadView("/view/RequestManagerView.fxml");
        }

        return contentView;
    }

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
    }
}

package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXListView;
import database.objects.Edge;
import database.objects.Request;
import entity.LoginEntity;
import entity.MapEntity;
import entity.RequestEntity;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import utility.ApplicationScreen;
import utility.node.NodeFloor;
import utility.KioskPermission;
import utility.request.RequestProgressStatus;
import utility.request.RequestType;

import java.io.IOException;
import java.util.LinkedList;

public class RequestManagerController extends ScreenController {


    LoginEntity l;

    RequestEntity r;
    @FXML
    private JFXListView<VBox> activeRequests;
    @FXML
    private Label totalRequests;
    @FXML
    private TextField txtID;
    @FXML
    private JFXButton completeButton;
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
        }else{
            foodFilter.setSelected(true);
            foodFilter.setDisable(false);
            janitorFilter.setSelected(true);
            janitorFilter.setDisable(false);
            securityFilter.setSelected(true);
            securityFilter.setDisable(false);
            interpreterFilter.setSelected(true);
            interpreterFilter.setDisable(false);
            maintenanceFilter.setSelected(true);
            maintenanceFilter.setDisable(false);
            itFilter.setSelected(true);
            itFilter.setDisable(false);
            transportationFilter.setSelected(true);
            transportationFilter.setDisable(false);
        }
    }

    @FXML
    void viewRequests() throws IOException {
        System.out.println("request Manager Pressed\n");
        getParent().switchToScreen(ApplicationScreen.REQUEST_MANAGER);
    }

    @FXML
    void newRequests(){
        setup();
        LinkedList<Request> allRequests = filterRequests();
        showRequests(RequestProgressStatus.TO_DO, "Assign", allRequests);
    }

    @FXML
    void inProgressRequests(){
        setup();
        LinkedList<Request> allRequests = filterRequests();
        showRequests(RequestProgressStatus.IN_PROGRESS, "Complete", allRequests);
    }

    @FXML
    void doneRequests(){
        setup();
        LinkedList<Request> allRequests = filterRequests();
        showRequests(RequestProgressStatus.DONE, "Delete", allRequests);
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

    @FXML
    void showRequests(RequestProgressStatus status, String buttonName, LinkedList<Request> allRequests){
        activeRequests.setItems(null);
        ObservableList<VBox> vBoxes = FXCollections.observableArrayList();

        if(allRequests.isEmpty() || r.filterByStatus(allRequests,status).isEmpty()){
            Label emptyList = new Label("No Requests");
            VBox vBox = new VBox();
            vBox.getChildren().add(emptyList);
            ObservableList<VBox> items = FXCollections.observableArrayList (vBox);
            activeRequests.setItems(items);
        }else{
            LinkedList<Request> requests = r.filterByStatus(allRequests,status);
            for (int i = 0; i < requests.size(); i++) {
                VBox vbox = new VBox();
                String id = requests.get(i).getRequestID();
                TextField requestTextField = new TextField(requests.get(i).getRequestID());
                String location = MapEntity.getInstance().getNode(requests.get(i).getNodeID()).getLongName();
                requestTextField.setEditable(false);
                Label employee = new Label("Employee: " + requests.get(i).getAssigner()); //Some reason this returns more than needed
                Label typeOfRequest = new Label(r.checkRequestType(id).toString());
                Label locationOfRequest = new Label(location);

                vbox.getChildren().add(requestTextField);
                vbox.getChildren().add(employee);
                vbox.getChildren().add(typeOfRequest);
                vbox.getChildren().add(locationOfRequest);

                if(!status.equals(RequestProgressStatus.TO_DO)){
                    String completer;
                    if(r.checkRequestType(id).equals(RequestType.INTERPRETER)){
                        completer = r.getInterpreterRequest(id).getCompleter();
                    }else{
                        completer = r.getSecurityRequest(id).getCompleter();
                    }
                    Label completerLabel = new Label("Completed by: "+completer);
                    vbox.getChildren().add(completerLabel);
                }
                vbox.getChildren().add(addButton(status,buttonName,id));
                vBoxes.add(vbox);
            }
            activeRequests.setItems(vBoxes);
        }
    }

    @FXML
    JFXButton addButton(RequestProgressStatus status, String buttonName, String id){
        KioskPermission permission = l.getCurrentPermission();
        JFXButton reqButton = new JFXButton();
        switch (permission){
            case EMPLOYEE:
                if(!status.equals(RequestProgressStatus.DONE)){
                    reqButton = new JFXButton(buttonName);
                    reqButton.setOnAction(new EventHandler<ActionEvent>() {
                        @Override public void handle(ActionEvent e) {
                            onCompletePressed(id);
                        }
                    });
                    reqButton.setStyle("-fx-background-color: #DFB951;");
//                    activeRequests.getChildren().add(reqButton);
//                    return reqButton;
                }
                break;
            case ADMIN: case SUPER_USER:
                if(!status.equals(RequestProgressStatus.DONE)) {
                    reqButton = new JFXButton("Cancel");
                    reqButton.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent e) {
                            r.deleteRequest(id);
                            if(status.equals(RequestProgressStatus.TO_DO)){
                                newRequests();
                            }else{
                                inProgressRequests();
                            }
                        }
                    });
                    reqButton.setStyle("-fx-background-color: #DFB951;");
//                    activeRequests.getChildren().add(reqButton);
//                    return reqButton;
                }else{
                    reqButton = new JFXButton(buttonName);
                    reqButton.setOnAction(new EventHandler<ActionEvent>() {
                        @Override public void handle(ActionEvent e) {
                            onCompletePressed(id);
                        }
                    });
                    reqButton.setStyle("-fx-background-color: #DFB951;");
//                    activeRequests.getChildren().add(reqButton);
                    return reqButton;
                }
        }
        return reqButton;
    }

    @FXML
    void onCompletePressed(String ID){
        Request request;
        if(r.checkRequestType(ID).equals(RequestType.INTERPRETER)){
            request = r.getInterpreterRequest(ID);
        }else{
            request = r.getSecurityRequest(ID);
        }

        RequestProgressStatus status = request.getStatus();
        switch (status){
            case DONE:
                r.deleteRequest(ID);
                doneRequests();
                break;
            case IN_PROGRESS:
                r.completeRequest(request.getRequestID());
                inProgressRequests();
                break;
            case TO_DO:
                r.markInProgress(l.getUserID(),request.getRequestID());
                System.out.println(request.getStatus());
                newRequests();
                break;
        }
        System.out.println("Complete Pressed \n");
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
        getMapController().setAnchor(0,400,0,0);
    }
}

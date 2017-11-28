package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import database.objects.Edge;
import database.objects.Request;
import entity.LoginEntity;
import entity.MapEntity;
import entity.RequestEntity;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import utility.ApplicationScreen;
import utility.Node.NodeFloor;
import utility.Request.RequestProgressStatus;
import utility.Request.RequestType;

import java.io.IOException;
import java.util.LinkedList;

public class RequestManagerController extends ScreenController {


    LoginEntity l;

    RequestEntity r;

    @FXML
    private VBox activeRequests;
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

    }

    @FXML
    void viewRequests() throws IOException {
        System.out.println("Request Manager Pressed\n");

        getParent().switchToScreen(ApplicationScreen.REQUEST_MANAGER);
    }

    @FXML
    void newRequests(){
        LinkedList<Request> allRequests = filterRequests();
        showRequests(RequestProgressStatus.TO_DO, "Assign", allRequests);
    }

    @FXML
    void inProgressRequests(){
        LinkedList<Request> allRequests = filterRequests();
        showRequests(RequestProgressStatus.IN_PROGRESS, "Complete", allRequests);
    }

    @FXML
    void doneRequests(){
        LinkedList<Request> allRequests = filterRequests();
        showRequests(RequestProgressStatus.DONE, "Delete", allRequests);
    }

    @FXML
    LinkedList<Request> filterRequests() {
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
//        r.readAllFromDatabase();    //Do we need this???
        activeRequests.getChildren().clear();

        if(allRequests.isEmpty() || r.filterByStatus(allRequests,status).isEmpty()){
            Label emptyList = new Label("No Requests");
            activeRequests.getChildren().add(emptyList);
        }else{

            LinkedList<Request> requests = r.filterByStatus(allRequests,status);
            for (int i = 0; i < requests.size(); i++) {
                String id = requests.get(i).getRequestID();
                TextField requestTextField = new TextField(requests.get(i).getRequestID());
                String location = MapEntity.getInstance().getNode(requests.get(i).getNodeID()).getLongName();
                requestTextField.setEditable(false);
                Label requestID = new Label("Employee: " + requests.get(i).getAssigner());
                Label typeOfRequest = new Label(r.checkRequestType(id).toString());
                Label locationOfRequest = new Label(location);
                Label spacer = new Label("");

                JFXButton selectID = new JFXButton(buttonName);
                selectID.setOnAction(new EventHandler<ActionEvent>() {
                    @Override public void handle(ActionEvent e) {
                        onCompletePressed(id);
                    }
                });
                selectID.setStyle("-fx-background-color: #DFB951;");

                activeRequests.getChildren().add(requestTextField);
                activeRequests.getChildren().add(requestID);
                activeRequests.getChildren().add(typeOfRequest);
                activeRequests.getChildren().add(locationOfRequest);
                activeRequests.getChildren().add(selectID);
                activeRequests.getChildren().add(spacer);
            }
        }
    }

    @FXML
    void onCompletePressed(String ID){
//        String ID = txtID.getText();
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
                request.complete();
                inProgressRequests();
                break;
            case TO_DO:
                request.markInProgress(l.getUserName());
                newRequests();
                break;
        }
        txtID.clear();
        System.out.println("Complete Pressed \n");
    }

    @FXML
    void submitRequest(){
        getParent().switchToScreen(ApplicationScreen.REQUEST_SUBMITTER);
    }

    @FXML
    void showReports() throws IOException{
        RequestTrackingDataController test = new RequestTrackingDataController(this,r.getLanguageFrequency());
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/TESTViewInterpreterRequestLangHistogram.fxml"));
        loader.setController(test);
        Node view = loader.load();
        BorderPane histogram = new BorderPane();

        AnchorPane.setTopAnchor(histogram, 0.0);
        AnchorPane.setLeftAnchor(histogram, 0.0);
        AnchorPane.setBottomAnchor(histogram, 0.0);
        AnchorPane.setRightAnchor(histogram, 0.0);
        histogram.setCenter(view);
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

package controller;

import com.jfoenix.controls.JFXButton;
import database.DatabaseController;
import database.objects.Edge;
import database.objects.Request;
import entity.MapEntity;
import entity.RequestEntity;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import utility.ApplicationScreen;
import utility.Node.NodeFloor;
import utility.Request.RequestProgressStatus;



import java.io.IOException;
import java.util.LinkedList;

public class RequestManagerController extends ScreenController {

    public RequestManagerController(MainWindowController parent, MapController map) {
        super(parent, map);

    }

    @FXML
    private VBox activeRequests;
    @FXML
    private Label totalRequests;
    @FXML
    private TextField txtID;
    @FXML
    private JFXButton completeButton;

    @FXML
    void viewRequests() throws IOException {
        System.out.println("Request Manager Pressed\n");

        getParent().switchToScreen(ApplicationScreen.REQUEST_MANAGER);
    }

    @FXML
    void newRequests(){
        completeButton.setText("Assign Selected");
        showRequests(RequestProgressStatus.TO_DO);
    }

    @FXML
    void inProgressRequests(){
        completeButton.setText("Complete Selected");
        showRequests(RequestProgressStatus.IN_PROGRESS);
    }

    @FXML
    void doneRequests(){
        completeButton.setText("Delete Selected");
        showRequests(RequestProgressStatus.DONE);
    }

    @FXML
    void showRequests(RequestProgressStatus status){
        activeRequests.getChildren().clear();
        LinkedList<Request> requests = RequestEntity.getInstance().getStatusRequests(status);
        for (int i = 0; i < requests.size(); i++) {
            String id = requests.get(i).getRequestID();
            TextField requestTextField = new TextField(requests.get(i).getRequestID());
            String location = MapEntity.getInstance().getNode(requests.get(i).getNodeID()).getLongName();
            requestTextField.setEditable(false);
            Label requestID = new Label("Employee: " + requests.get(i).getAssigner());
            String requestType = requests.get(i).getRequestID().substring(0,3);
            Label typeOfRequest;
            switch (requestType){
                case "Int":
                    typeOfRequest = new Label("Type: Interpreter");
                    break;
                case "Sec":
                    typeOfRequest = new Label("Type: Security");
                    break;
                default:
                    typeOfRequest = new Label("Type: Generic");
                    break;
            }

            Label locationOfRequest = new Label(location);
            JFXButton selectID = new JFXButton("Select");
            selectID.setOnAction(new EventHandler<ActionEvent>() {
                @Override public void handle(ActionEvent e) {
                    txtID.setText(id);
                }
            });
            selectID.setStyle("-fx-background-color: #DFB951;");

            //TODO find what type of reqeust it is
            activeRequests.getChildren().add(requestTextField);
            activeRequests.getChildren().add(requestID);
            activeRequests.getChildren().add(typeOfRequest);
            activeRequests.getChildren().add(locationOfRequest);
            activeRequests.getChildren().add(selectID);
        }
    }

    @FXML
    void selectIDPressed(String id){
        txtID.setText(id);
    }

    @FXML
    void onBackPressed() throws IOException {
        System.out.println("Cancel Pressed\n");

        getParent().switchToScreen(ApplicationScreen.MAP_BUILDER);
    }

    @FXML
    void onCompletePressed(){
        String ID = txtID.getText();
        Request request = RequestEntity.getInstance().getInterpreterRequest(ID);
        RequestProgressStatus status = request.getStatus();
        switch (status){
            case DONE:
                RequestEntity.getInstance().deleteRequest(ID);
                doneRequests();
                break;
            case IN_PROGRESS:
                request.complete();
                inProgressRequests();
                break;
            case TO_DO:
                request.inProgress();
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

    @Override
    public javafx.scene.Node getContentView() {
        if (contentView == null) {
            contentView = loadView("/view/RequestManagerView.fxml");
        }

        return contentView;
    }

    @Override
    public void onMapLocationClicked(Point2D location) { }

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

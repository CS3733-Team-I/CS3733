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



import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.LinkedList;

public class RequestManagerController extends ScreenController {


    RequestEntity r;

    @FXML
    private VBox activeRequests;
    @FXML
    private Label totalRequests;
    @FXML
    private TextField txtID;
    @FXML
    private JFXButton completeButton;

    public RequestManagerController(MainWindowController parent, MapController map) {
        super(parent, map);
        r = RequestEntity.getInstance();

    }

    @FXML
    void viewRequests() throws IOException {
        System.out.println("Request Manager Pressed\n");

        getParent().switchToScreen(ApplicationScreen.REQUEST_MANAGER);
    }

    @FXML
    void newRequests(){
        showRequests(RequestProgressStatus.TO_DO, "Assign");
    }

    @FXML
    void inProgressRequests(){
        showRequests(RequestProgressStatus.IN_PROGRESS, "Complete");
    }

    @FXML
    void doneRequests(){
        showRequests(RequestProgressStatus.DONE, "Delete");
    }

    @FXML
    void showRequests(RequestProgressStatus status, String buttonName){
        r.readAllFromDatabase();    //Do qw need this???
        activeRequests.getChildren().clear();
        LinkedList<Request> requests = r.getStatusRequests(status);
        Label spacer = new Label("");
        if(requests.isEmpty()){
            Label emptyList = new Label("No Requests");
            activeRequests.getChildren().add(emptyList);
        }else{
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
                JFXButton selectID = new JFXButton(buttonName);
                selectID.setOnAction(new EventHandler<ActionEvent>() {
                    @Override public void handle(ActionEvent e) {
                        onCompletePressed(id);
                    }
                });
                selectID.setStyle("-fx-background-color: #DFB951;");

                activeRequests.getChildren().addAll(requestTextField,requestID,typeOfRequest,locationOfRequest,selectID,spacer);
            }
        }
    }

    @FXML
    void onCompletePressed(String ID){
//        String ID = txtID.getText();
        Request request = r.getInterpreterRequest(ID);
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

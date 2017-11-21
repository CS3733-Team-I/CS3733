package controller;

import com.jfoenix.controls.JFXButton;
import database.DatabaseController;
import database.objects.Edge;
import database.objects.Request;
import entity.RequestEntity;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import utility.ApplicationScreen;
import utility.Node.NodeFloor;


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
    void viewRequests() throws IOException {
        System.out.println("Request Manager Pressed\n");

        getParent().switchToScreen(ApplicationScreen.ADMIN_VIEWREQUEST);
    }

    @FXML
    void showRequests(){
        activeRequests.getChildren().clear();
        LinkedList<Request> requests = RequestEntity.getInstance().getAllRequests();
        for (int i = 0; i < requests.size(); i++) {
            String id = requests.get(i).getRequestID();
            TextField requestTextField = new TextField(requests.get(i).getassigner());
            String location = DatabaseController.getNode(requests.get(i).getNodeID()).getLongName();
            requestTextField.setEditable(false);
            Label requestID = new Label("ID: " + id);
            Label typeOfRequest = new Label("Type: Interpreter");
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

    public void btncode(ActionEvent e){

    }

    @FXML
    void selectIDPressed(String id){
        txtID.setText(id);
    }

    @FXML
    void onBackPressed() throws IOException {
        System.out.println("Cancel Pressed\n");

        getParent().switchToScreen(ApplicationScreen.ADMIN_MENU);
    }

    @FXML
    void onCompletePressed(){
        String ID = txtID.getText();
        RequestEntity.getInstance().deleteRequest(ID);
        txtID.clear();
        System.out.println("Complete Pressed \n");
        showRequests();
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

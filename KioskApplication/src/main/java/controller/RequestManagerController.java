package controller;

import database.DatabaseController;
import database.objects.Edge;
import entity.InterpreterRequest;
import entity.Request;
import entity.RequestEntity;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import utility.ApplicationScreen;
import utility.NodeFloor;

import java.io.IOException;
import java.util.ArrayList;

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
        LinkedList<Request> requests = RequestEntity.getAllRequests();
        for (int i = 0; i < requests.size(); i++) {
            int id = requests.get(i).getRequestID();
            TextField requestTextField = new TextField(requests.get(i).getEmployee() + " ID: " + id);
            requestTextField.setEditable(false);
            Label typeOfRequest = new Label("Type: generic");
            Label locationOfRequest = new Label(requests.get(i).getLocation().getLongName());
            //TODO find what type of reqeust it is
            activeRequests.getChildren().add(requestTextField);
            activeRequests.getChildren().add(typeOfRequest);
            activeRequests.getChildren().add(locationOfRequest);
        }
    }

    @FXML
    void onBackPressed() throws IOException {
        System.out.println("Cancel Pressed\n");

        getParent().switchToScreen(ApplicationScreen.ADMIN_MENU);
    }

    @FXML
    void onCompletePressed(){
        int ID = Integer.parseInt(txtID.getText());
        DatabaseController.deleteRequest(ID);
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

package controller;

import database.DatabaseController;
import database.objects.Edge;
import entity.Request;
import javafx.fxml.FXML;
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
        ArrayList<Request> requests = DatabaseController.getAllRequests();
        for (int i = 0; i < requests.size(); i++) {
            TextField requestTextField = new TextField("Request ID: " + requests.get(i).getRequestID());
            requestTextField.setEditable(false);
            activeRequests.getChildren().add(requestTextField);
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
        System.out.println("Complete Pressed \n");
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

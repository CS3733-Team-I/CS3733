package controller;

import database.DatabaseController;
import database.objects.Edge;
import entity.Request;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import utility.ApplicationScreen;
import utility.NodeFloor;

import java.io.IOException;
import java.util.ArrayList;

public class RequestManagerController extends ScreenController {

    //something
    public RequestManagerController(MainWindowController parent, MapController map) {
        super(parent, map);
    }

    @FXML
    private ComboBox activeRequests;
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
        ArrayList<String> reqIDs = new ArrayList<String>();
        for (int i = 0; i < requests.size(); i++) {
             reqIDs.add("Request ID: " + requests.get(i).getRequestID());
        }

        activeRequests.getItems().clear();
        activeRequests.getItems().addAll(reqIDs);
        int requestNum = DatabaseController.getAllRequests().size();
        totalRequests.setText("Total Requests: " + requestNum + ".");
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
        // TODO implement this
    }
}

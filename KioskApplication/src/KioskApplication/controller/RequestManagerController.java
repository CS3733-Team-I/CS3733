package KioskApplication.controller;

import KioskApplication.database.DatabaseController;
import KioskApplication.entity.Request;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import java.io.IOException;
import java.util.ArrayList;

import static KioskApplication.controller.AdminWindowController.SidebarType.SIDEBAR_MENU;

public class RequestManagerController {

    AdminWindowController parent;

    DatabaseController dbController;

    public RequestManagerController(AdminWindowController parent) {

        this.parent = parent;

        dbController = DatabaseController.getInstance();
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

        this.parent.switchTo(AdminWindowController.SidebarType.SIDEBAR_VIEWREQUEST);
    }

    @FXML
    void showRequests(){
        ArrayList<Request> requests = dbController.getAllRequests();
        ArrayList<String> reqIDs = new ArrayList<String>();
        for (int i = 0; i < requests.size(); i++) {
             reqIDs.add("Request ID: " + requests.get(i).getRequestID());
        }

        activeRequests.getItems().clear();
        activeRequests.getItems().addAll(reqIDs);
        int requestNum = dbController.getAllRequests().size();
        totalRequests.setText("Total Requests: " + requestNum + ".");
    }

    @FXML
    void onBackPressed() throws IOException {
        System.out.println("Cancel Pressed\n");

        this.parent.switchTo(SIDEBAR_MENU);
    }

    @FXML
    void onCompletePressed(){
        int ID = Integer.parseInt(txtID.getText());
        dbController.deleteRequest(ID);
        System.out.println("Complete Pressed \n");
    }

}
